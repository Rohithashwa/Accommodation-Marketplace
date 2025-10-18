package com.dcl.accommodate.dto.response;

import com.dcl.accommodate.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Profile {

    sealed interface  UserProfile permits HostProfile{}

    @AllArgsConstructor
    @Setter
    @Getter
    @Builder
    public non-sealed static class HostProfile implements UserProfile {

        @JsonProperty("user_id")
        UUID userId;

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

        @JsonProperty("profile")
        List<HostInfo> profile;
    }


}
