package com.dcl.accommodate.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HostRequest(
        @JsonProperty("user_bio")
        String bio
) {
}
