package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NoSuchIdException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking create(BookingDto bookingDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        if (!item.getAvailable()) {
            throw new BadRequestException("Item not available");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadRequestException("Booking end date is before start date");
        }
        if (bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new BadRequestException("Booking end date equals start date");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new NoSuchIdException("Cannot book own item");
        }
        boolean allow = true;
        for (Booking booking : bookingRepository.findByItem(item)) {
            if (booking.status != BookingStatus.APPROVED)
                continue;
            if (booking.getEnd().isAfter(bookingDto.getStart()) &&
                    booking.getStart().isBefore(bookingDto.getEnd())) {
                allow = false;
                break;
            }
        }
        if (!allow) {
            throw new NoSuchIdException("Booking conflict");
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveOrReject(Long bookingId, boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not the owner of the item");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change state");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public Booking get(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not the owner or the booker of the booking");
        }
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(String stateString, Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchIdException("User not found"));

        BookingRequestState state;
        try {
            state = BookingRequestState.valueOf(stateString);
        } catch (Exception e) {
            throw new UnsupportedStateException("Unknown state: " + stateString);
        }

        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBooker(user, pageable).toList();
                break;
            case PAST:
                bookings = bookingRepository.findByBookerAndEndIsBefore(user, LocalDateTime.now(),
                        pageable).toList();
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerAndStartIsAfter(user, LocalDateTime.now(),
                        pageable).toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), pageable).toList();
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerAndStatus(user, BookingStatus.WAITING,
                        pageable).toList();
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerAndStatus(user, BookingStatus.REJECTED,
                        pageable).toList();
                break;
            default:
                bookings = new ArrayList<>();
                break;
        }
        return bookings;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getOwnerBookings(String stateString, Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchIdException("User not found"));

        BookingRequestState state;
        try {
            state = BookingRequestState.valueOf(stateString);
        } catch (Exception e) {
            throw new UnsupportedStateException("Unknown state: " + stateString);
        }
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemOwner(user, pageable).toList();
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerAndEndIsBefore(user, LocalDateTime.now(),
                        pageable).toList();
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerAndStartIsAfter(user, LocalDateTime.now(),
                        pageable).toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerAndStartIsBeforeAndEndIsAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), pageable).toList();
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerAndStatus(user, BookingStatus.WAITING,
                        pageable).toList();
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerAndStatus(user, BookingStatus.REJECTED,
                        pageable).toList();
                break;
            default:
                bookings = new ArrayList<>();
                break;
        }
        return bookings;
    }
}

