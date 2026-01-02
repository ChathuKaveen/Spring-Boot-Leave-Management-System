package com.example.Leave.Management.repositories;

import com.example.Leave.Management.entities.SupervisorMember;
import com.example.Leave.Management.entities.SupervisorType;
import com.example.Leave.Management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupervisorMemberRepository extends JpaRepository<SupervisorMember , Long> {
    Optional<SupervisorMember> findByUserAndType(User u  , SupervisorType type);

    @Query("""
            SELECT s FROM SupervisorMember s WHERE s.id <> :id AND (s.user = :user AND s.type =:supervisor)
            """)
    List<SupervisorMember> findByUserAndTypeForUpdate(@Param("user") User user  ,@Param("supervisor") SupervisorType type ,@Param("id") Long id);

    @Query("""
                SELECT s
                FROM SupervisorMember s
                WHERE s.supervisor = :supervisor
            """)
    List<SupervisorMember> findDirectSubordinates(@Param("supervisor") User supervisor);
}
