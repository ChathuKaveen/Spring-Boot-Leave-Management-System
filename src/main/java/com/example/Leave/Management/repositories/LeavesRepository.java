package com.example.Leave.Management.repositories;

import com.example.Leave.Management.entities.Leaves;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeavesRepository extends JpaRepository<Leaves , Long> {
}
