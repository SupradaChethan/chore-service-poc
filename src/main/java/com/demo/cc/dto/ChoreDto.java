package com.demo.cc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoreDto {

    private Long id;
    private String description;
    private LocalTime time;
    private LocalDate date;
    private Long userId;
}
