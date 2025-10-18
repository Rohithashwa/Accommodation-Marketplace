package com.dcl.accommodate.dto.response;

import com.dcl.accommodate.enums.UserRole;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder
public record HostInfo(
        UserRole role,
        String bio,
        Instant date,
        boolean isSuperHost,
        Double responseRate

) {

}
