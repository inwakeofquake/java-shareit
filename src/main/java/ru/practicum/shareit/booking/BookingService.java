package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(BookingDto bookingDto, Long userId);

    Booking approveOrReject(Long bookingId, boolean approved, Long userId);

    Booking get(Long bookingId, Long userId);

    List<Booking> getUserBookings(String state, Long userId, Pageable pageable);

    List<Booking> getOwnerBookings(String state, Long userId, Pageable pageable);
}
