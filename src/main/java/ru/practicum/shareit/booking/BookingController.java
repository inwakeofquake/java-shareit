package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingServiceImpl bookingService;

    @PostMapping
    public Booking create(@RequestBody @Valid BookingDto booking, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking approveOrReject(@PathVariable Long bookingId,
                                   @RequestParam boolean approved,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.approveOrReject(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public Booking get(@PathVariable Long bookingId,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.get(bookingId, userId);
    }

    @GetMapping
    public List<Booking> getUserBookings(
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getUserBookings(state, userId);
    }

    @GetMapping("/owner")
    public List<Booking> getOwnerBookings(
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getOwnerBookings(state, userId);
    }
}
