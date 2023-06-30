package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookingDto {
    private Long id;
    @NotNull
    @Future
    private LocalDateTime start;
    @NotNull
    @FutureOrPresent
    private LocalDateTime end;
    private ItemDto item;
    @NotNull
    private Long itemId;
    private Long bookerId;
    public BookingStatus status;
}