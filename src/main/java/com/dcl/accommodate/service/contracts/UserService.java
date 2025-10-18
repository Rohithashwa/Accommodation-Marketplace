package com.dcl.accommodate.service.contracts;


import com.dcl.accommodate.dto.request.HostRequest;
import com.dcl.accommodate.dto.request.UserLoginRequest;
import com.dcl.accommodate.dto.request.UserRegistrationRequest;
import com.dcl.accommodate.dto.request.UserUpdateRequest;
import com.dcl.accommodate.dto.response.AuthResponse;
import com.dcl.accommodate.dto.response.Profile;
import com.dcl.accommodate.dto.response.UserResponse;

public interface UserService {

    public void registerUser(UserRegistrationRequest registration);

    AuthResponse loginUser(UserLoginRequest request);

    AuthResponse refreshLogin();

    UserResponse userProfile();

    UserResponse updateUserById(UserUpdateRequest request);

    void convertHost();

    Profile.HostProfile updateHost(HostRequest request);
}
