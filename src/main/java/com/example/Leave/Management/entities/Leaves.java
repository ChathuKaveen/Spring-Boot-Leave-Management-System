package com.example.Leave.Management.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "leaves")
public class Leaves {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="uid")
    private User user;

    @ManyToOne
    @JoinColumn( name = "leave_type_id")
    private LeaveTypes leaveTypes;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "days")
    private double days;

    @Column(name = "from_date")
    private LocalDate from_date;

    @Column(name = "to_date")
    private LocalDate to_date;

    @ManyToOne
    @JoinColumn( name = "from_date_type")
    private LeaveDayType from_date_type;

    @ManyToOne
    @JoinColumn( name = "to_date_type")
    private LeaveDayType to_date_type;

    @Column(name = "reason")
    private String reason;

    @Column(name = "updated_by")
    private Long updated_by;

    @Column(name = "updated_on" , updatable = false , insertable = false)
    private LocalDateTime updated_on;


}
