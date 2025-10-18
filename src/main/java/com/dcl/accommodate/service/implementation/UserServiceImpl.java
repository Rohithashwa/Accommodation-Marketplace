package com.dcl.accommodate.service.implementation;

import com.dcl.accommodate.dto.request.HostRequest;
import com.dcl.accommodate.dto.request.UserLoginRequest;
import com.dcl.accommodate.dto.request.UserRegistrationRequest;
import com.dcl.accommodate.dto.request.UserUpdateRequest;
import com.dcl.accommodate.dto.response.AuthResponse;
import com.dcl.accommodate.dto.response.HostInfo;
import com.dcl.accommodate.dto.response.Profile;
import com.dcl.accommodate.dto.response.UserResponse;
import com.dcl.accommodate.enums.JwtType;
import com.dcl.accommodate.enums.UserRole;
import com.dcl.accommodate.exceptions.UserAlreadyExistByEmailException;
import com.dcl.accommodate.model.Host;
import com.dcl.accommodate.model.User;
import com.dcl.accommodate.repository.HostRepository;
import com.dcl.accommodate.repository.UserRepository;
import com.dcl.accommodate.security.jwt.JwtService;

import static com.dcl.accommodate.security.util.CurrentUser.*;

import com.dcl.accommodate.service.contracts.UserService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final HostRepository hostRepository;

    @Override
    public void registerUser(UserRegistrationRequest registration) {
        if (repository.existsByEmail(registration.email()))
            throw new UserAlreadyExistByEmailException("User already registered with such email ID");
        var user = this.toUser(registration);
        //All users are GUEST by default
        user.setUserRole(UserRole.GUEST);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
    }

    @Override
    public AuthResponse loginUser(UserLoginRequest request) {
        var token = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var auth = authenticationManager.authenticate(token);

        if (!auth.isAuthenticated())
            throw new UsernameNotFoundException("Failed to authenticate username and password");

        var user = repository.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return grantTokens(user);
    }

    private AuthResponse grantTokens(User user) {
        JwtService.TokenResult accessToken = generateAccessToken(user);
        JwtService.TokenResult refreshToken = generateRefreshToken(user);

        return new AuthResponse(
                user.getUserId().toString(),
                accessToken.token(),
                accessToken.ttl().toSeconds(),
                refreshToken.token(),
                refreshToken.ttl().toSeconds()
        );
    }

    @Override
    public AuthResponse refreshLogin() {
        Supplier<UsernameNotFoundException> userNotFound = () -> new UsernameNotFoundException(("User not found"));
        UUID userId = getCurrentUserId()
                .orElseThrow(userNotFound);

        User user = repository.findById(userId)
                .orElseThrow(userNotFound);

        return grantTokens(user);
    }

    @Override
    public UserResponse userProfile() {
        Supplier<UsernameNotFoundException> userNotFound = () -> {
            log.info("User not found or authenticated.");
            return new UsernameNotFoundException("Failed to identify user. Authentication Failed.");
        };
        UUID userId = getCurrentUserId().orElseThrow(userNotFound);

        User user = repository.findById(userId).orElseThrow(userNotFound);
        return toUserResponse(user);
    }

    @Override
    public UserResponse updateUserById(UserUpdateRequest request) {
        UUID userId = getCurrentUserId().get();

        User user = repository.findById(userId).get();

        updateUser(user, request);

        User save = repository.save(user);

        return toUserResponse(save);
    }

    @Override
    public void convertHost() {
        Supplier<UsernameNotFoundException> userNotFound = () -> {
            log.info("User not found or authenticated.");
            return new UsernameNotFoundException("Failed to identify user. Authentication Failed.");
        };
        UUID userId = getCurrentUserId().orElseThrow(userNotFound);
        Optional<User> user = repository.findById(userId);
        user.get().setUserRole(UserRole.HOST);

        repository.save(user.get());
        Host host = Host.builder()
                .userId(userId)
                .build();
        hostRepository.save(host);

    }

    @Override
    public Profile.HostProfile updateHost(HostRequest request) {
        Supplier<UsernameNotFoundException> userNotFound = ()-> new UsernameNotFoundException("Failed to identify user. Authentication Failed.");
        UUID userId = getCurrentUserId().orElseThrow(userNotFound);

        User user = repository.findById(userId).get();

        var data = hostRepository.findById(userId).orElseThrow(userNotFound);
        Host host = updateHost(request, userId, data);


        var save = hostRepository.save(host);

        var info = getHostProfile(user, host);

        return info;

    }

    private static Host updateHost(HostRequest request, UUID userId, Host data) {
        Host host = Host.builder()
                .bio(request.bio())
                .userId(userId)
                .hostedSince(data.getHostedSince())
                .isSuperHost(data.isSuperHost())
                .responseRate(data.getResponseRate())
                .build();
        return host;
    }

    private static Profile.HostProfile getHostProfile(User user, Host host) {
        HostInfo hostInfo = HostInfo.builder()
                .role(user.getUserRole())
                .bio(host.getBio())
                .date(host.getHostedSince())
                .isSuperHost(host.isSuperHost())
                .responseRate(host.getResponseRate())
                .build();

        var info = Profile.HostProfile.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .userRole(user.getUserRole())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .avatar(user.getAvatar())
                .createdDate(user.getCreatedDate())
                .lastModifiedDate(user.getLastModifiedDate())
                .profile(List.of(hostInfo))
                .build();
        return info;
    }

    private static void updateUser(User user, UserUpdateRequest request) {
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setDateOfBirth(request.dateOfBirth());
    }

    private static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .userRole(user.getUserRole())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .avatar(user.getAvatar())
                .createdDate(user.getCreatedDate())
                .lastModifiedDate(user.getLastModifiedDate())
                .build();
    }

    private JwtService.TokenResult generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getUserRole().name());

        var tokenConfig = new JwtService.TokenConfig(
                claims,
                user.getUserId().toString(),
                JwtType.ACCESS
        );

        return jwtService.generateToken(tokenConfig);
    }

    private JwtService.TokenResult generateRefreshToken(User user) {
        var tokenConfig = new JwtService.TokenConfig(
                new HashMap<>(),
                user.getUserId().toString(),
                JwtType.REFRESH
        );

        return jwtService.generateToken(tokenConfig);
    }

    private User toUser(UserRegistrationRequest registration) {
        return User.builder()
                .firstName(registration.firstName())
                .lastName(registration.lastName())
                .email(registration.email())
                .phoneNumber(registration.phoneNumber())
                .password(registration.password())
                .dateOfBirth(registration.dateOfBirth())
                .build();
    }
}