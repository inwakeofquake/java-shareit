package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private static final String header = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking create(@RequestBody @Valid BookingDto booking, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating booking for user ID: {}", userId);
        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public Booking approveOrReject(@PathVariable Long bookingId,
                                   @RequestParam boolean approved,
                                   @RequestHeader(header) Long userId) {
        log.info("Approving or rejecting booking with ID: {} for user ID: {}", bookingId, userId);
        return bookingService.approveOrReject(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public Booking get(@PathVariable Long bookingId,
                       @RequestHeader(header) Long userId) {
        log.info("Getting booking with ID: {} for user ID: {}", bookingId, userId);
        return bookingService.get(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Booking> getUserBookings(
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "1000") int size,
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestHeader(header) Long userId) {
        if ((from<0) || (size == 0)) {
            throw new BadRequestException("Bad page params");
        }
        log.info("Getting user bookings with state: {} for user ID: {}", state, userId);
        return bookingService.getUserBookings(state, userId, PageRequest.of(from/size, size, Sort.by("start").descending()));
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<Booking> getOwnerBookings(
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "1000") int size,
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestHeader(header) Long userId) {
        if ((from<0) || (size == 0)) {
            throw new BadRequestException("Bad page params");
        }
        log.info("Getting owner bookings with state: {} for user ID: {}", state, userId);
        return bookingService.getOwnerBookings(state, userId, PageRequest.of(from/size, size,Sort.by("start").descending()));
    }
}
