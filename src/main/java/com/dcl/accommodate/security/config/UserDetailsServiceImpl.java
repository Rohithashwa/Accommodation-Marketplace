package com.dcl.accommodate.security.config;

import com.dcl.accommodate.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(this::mapToUserDetails)
                .orElseThrow(()->new UsernameNotFoundException("user not found with the email :"+email));
    }

    private UserDetails mapToUserDetails(com.dcl.accommodate.model.User user){
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
