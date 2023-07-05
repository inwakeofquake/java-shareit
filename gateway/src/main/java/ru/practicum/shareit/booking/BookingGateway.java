package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.utility.UnsupportedStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utility.Constants.HEADER_USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingGateway {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER_ID) Long userId,
                                         @RequestBody @Valid BookingDto bookingDto) {
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(HEADER_USER_ID) Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader(HEADER_USER_ID) Long userId,
                                      @PathVariable Long bookingId) {
        return bookingClient.get(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader(HEADER_USER_ID) Long userId,
                                      @RequestParam(name = "state", defaultValue = "ALL") String value,
                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
                                      @Positive @RequestParam(name = "size", defaultValue = "10") Long size) {
        BookingRequestState state = validateState(value);
        return bookingClient.get(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @RequestParam(name = "state", defaultValue = "ALL") String value,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Long size) {
        BookingRequestState state = validateState(value);
        return bookingClient.getByOwner(userId, state, from, size);
    }

    private BookingRequestState validateState(String value) {
        BookingRequestState state = BookingRequestState.ALL;
        try {
            state = BookingRequestState.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: " + value);
        }
        return state;
    }

}
