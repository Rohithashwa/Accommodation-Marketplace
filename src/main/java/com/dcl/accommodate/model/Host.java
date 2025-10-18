package com.dcl.accommodate.model;

import com.dcl.accommodate.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profile")
@EntityListeners(AuditingEntityListener.class)
public class Host {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "user_bio", length = 1000)
    private String bio;


    @Column(name = "hosted_since")
    @CreatedDate
    private Instant hostedSince;

    @Column(name = "super_host")
    private boolean isSuperHost;

    @Column(name = "responseRate")
    private double responseRate;
}
