package com.example.Leave.Management.repositories;

import com.example.Leave.Management.entities.LeaveTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveTypeRepository extends JpaRepository<LeaveTypes , Integer> {

}
