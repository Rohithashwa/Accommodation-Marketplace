package com.dcl.accommodate.dto.response;

import com.dcl.accommodate.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;


@AllArgsConstructor
@Getter
@Setter
public class GuestProfileResponse implements UserProfileResponse{

    @JsonProperty("user_id")
    String userId;

    @JsonProperty("first_name")
    String firstName;

    @JsonProperty("last_name")
    String lastName;

    @JsonProperty("date_of_birth")
    LocalDate dateOfBirth;

    @JsonProperty("email")
    String email;

    @JsonProperty("phone_number")
    String phoneNumber;

    @JsonProperty("role")
    UserRole userRole;

    @JsonProperty("avatar")
    String avatar;

    @JsonProperty("created_date")
    Instant createdDate;

    @JsonProperty("last_modified_date")
    Instant lastModifiedDate;

}
