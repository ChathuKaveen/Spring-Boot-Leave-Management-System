package com.example.Leave.Management.repositories;

import com.example.Leave.Management.entities.DayType;
import com.example.Leave.Management.entities.LeaveDayType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveDayTypeRepository extends JpaRepository<LeaveDayType , Integer> {
    LeaveDayType findIdByType(DayType value);
}
