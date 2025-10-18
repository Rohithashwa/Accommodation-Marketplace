package com.dcl.accommodate.repository;

import com.dcl.accommodate.model.Host;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HostRepository extends JpaRepository<Host, UUID> {


}
