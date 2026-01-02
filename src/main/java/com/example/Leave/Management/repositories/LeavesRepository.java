package com.example.Leave.Management.repositories;

import com.example.Leave.Management.entities.Leaves;
import com.example.Leave.Management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeavesRepository extends JpaRepository<Leaves , Long> {
    List<Leaves> findByUser(User u);

    @Query("""
    SELECT l
    FROM Leaves l
    WHERE l.user.id = :uid
      AND l.status IN ('PENDING', 'APPROVED')
      AND l.from_date <= :toDate
      AND l.to_date >= :fromDate
      AND l.id <> :id
""")
    List<Leaves> findOverlappingLeavesForUpdate( @Param("id") Long id,
                                        @Param("uid") Long uid,
                                       @Param("fromDate") LocalDate fromDate,
                                       @Param("toDate") LocalDate toDate);

    @Query("""
    SELECT l
    FROM Leaves l
    WHERE l.user.id = :uid
      AND l.status IN ('PENDING', 'APPROVED')
      AND l.from_date <= :toDate
      AND l.to_date >= :fromDate
""")
    List<Leaves> findOverlappingLeaves(@Param("uid") Long uid,
                                     @Param("fromDate") LocalDate fromDate,
                                     @Param("toDate") LocalDate toDate);



    @Query("""
                SELECT s
                FROM Leaves s
                WHERE s.user IN  :users
            """)
    List<Leaves> findLeavesByUsers(@Param("users") List<User> users);
}
