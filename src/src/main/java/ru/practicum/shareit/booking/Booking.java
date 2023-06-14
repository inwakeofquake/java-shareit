package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {

    public enum Status {
        WAITING, APPROVED, REJECTED, CANCELED
    }

    @NotNull
    private String id; // Unique booking identifier

    @NotNull
    private LocalDateTime start; // Start date and time of booking

    @NotNull
    private LocalDateTime end; // End date and time of booking

    @NotNull
    private Item item; // The item the user is booking

    @NotNull
    private User booker; // The user who makes the booking

    @NotNull
    private Status status; // Booking status
}
