package io.ylab.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class BookingDto {
    @NotNull
    private int chamberNumber;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
