package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.utility.Constants.HEADER_USER_ID;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking create(@RequestBody @Valid BookingDto booking, @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Creating booking for user ID: {}", userId);
        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public Booking approveOrReject(@PathVariable Long bookingId,
                                   @RequestParam boolean approved,
                                   @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Approving or rejecting booking with ID: {} for user ID: {}", bookingId, userId);
        return bookingService.approveOrReject(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public Booking get(@PathVariable Long bookingId,
                       @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Getting booking with ID: {} for user ID: {}", bookingId, userId);
        return bookingService.get(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Booking> getUserBookings(
            @PositiveOrZero @NotNull @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @NotNull @RequestParam(value = "size", defaultValue = "1000") Integer size,
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Getting user bookings with state: {} for user ID: {}", state, userId);
        return bookingService.getUserBookings(state, userId, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<Booking> getOwnerBookings(
            @PositiveOrZero @NotNull @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @NotNull @RequestParam(value = "size", defaultValue = "1000") Integer size,
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Getting owner bookings with state: {} for user ID: {}", state, userId);
        return bookingService.getOwnerBookings(state, userId, PageRequest.of(from / size, size, Sort.by("start").descending()));
    }
}
