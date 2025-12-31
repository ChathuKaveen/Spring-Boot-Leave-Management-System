package com.example.Leave.Management.repositories;

import com.example.Leave.Management.entities.Leaves;
import com.example.Leave.Management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeavesRepository extends JpaRepository<Leaves , Long> {
    List<Leaves> findByUser(User u);
}
