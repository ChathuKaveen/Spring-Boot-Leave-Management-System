package com.example.Leave.Management.dtos.LeavesDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class LeavePageResponse<T> {
    List<T> data;
    int page;
    int size;
    int totalPages;
    long totalElements;
}
