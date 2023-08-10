package shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utility.BadRequestException;
import ru.practicum.shareit.utility.NoSuchIdException;
import ru.practicum.shareit.utility.UnsupportedStateException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private Booking pastBooking, futureBooking, currentBooking;

    private BookingDto bookingDto;

    private Booking booking;

    private User user;

    private Item item;

    private final Long userId = 1L;

    @BeforeEach
    public void setUp() {

        user = User.builder()
                .id(1L)
                .build();

        User itemOwner = User.builder()
                .id(2L)
                .build();

        item = Item.builder()
                .id(1L)
                .name("test item")
                .available(true)
                .owner(itemOwner)
                .build();

        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        pastBooking = createPastBooking();
        futureBooking = createFutureBooking();
        currentBooking = createCurrentBooking();
    }

    private Booking createPastBooking() {
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);

        return Booking.builder()
                .id(4L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    private Booking createFutureBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(3);

        return Booking.builder()
                .id(5L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    private Booking createCurrentBooking() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        return Booking.builder()
                .id(6L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void create() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.create(bookingDto, userId);

        assertEquals(booking, result);

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createUserDoesNotExistThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            bookingService.create(bookingDto, userId);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(0)).findById(any());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void approveOrReject() {
        Long bookingId = 1L;
        boolean approved = true;

        User owner = User.builder().id(2L).build();
        item.setOwner(owner);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking result = bookingService.approveOrReject(bookingId, approved, owner.getId());

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void approveOrRejectNotFound() {
        Long bookingId = 1L;
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> bookingService.approveOrReject(bookingId, approved, userId));

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void approveOrRejectUserIsNotOwnerThrowsException() {
        Long bookingId = 1L;
        boolean approved = true;

        User notOwner = User.builder().id(3L).build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ResponseStatusException.class, () -> {
            bookingService.approveOrReject(bookingId, approved, notOwner.getId());
        });

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBooking() {
        Long bookingId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking foundBooking = bookingService.get(bookingId, user.getId());

        assertNotNull(foundBooking, "Booking was not found");
        assertEquals(booking, foundBooking, "The retrieved booking does not match the expected value");
        assertEquals(bookingId, foundBooking.getId(), "The retrieved booking's ID " +
                "does not match the expected value");
        assertEquals(user.getId(), foundBooking.getBooker().getId(), "The retrieved booking's user ID " +
                "does not match the expected value");
        assertEquals(BookingStatus.WAITING, foundBooking.getStatus(), "The retrieved booking's status " +
                "does not match the expected value");
    }

    @Test
    void getUserBookings() {
        Integer from = 0;
        Integer size = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setStatus(BookingStatus.APPROVED);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStatus(BookingStatus.REJECTED);

        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking1);
        bookingList.add(booking2);
        Page<Booking> bookings = new PageImpl<>(bookingList);

        when(bookingRepository.findByBooker(user, CustomPageRequest.of(from, size, Sort.by("start")
                .descending()))).thenReturn(bookings);

        List<Booking> allBookings = bookingService.getUserBookings("ALL", user.getId(), from, size);
        assertEquals(bookings.getContent(), allBookings);
        verify(bookingRepository, times(1))
                .findByBooker(user, CustomPageRequest.of(from, size, Sort.by("start").descending()));

        reset(bookingRepository);

        when(bookingRepository.findByBookerAndStatus(user, BookingStatus.WAITING, CustomPageRequest
                .of(from, size, Sort.by("start").descending()))).thenReturn(bookings);

        List<Booking> waitingBookings = bookingService.getUserBookings("WAITING", user.getId(), from, size);
        assertEquals(bookings.getContent(), waitingBookings);
        verify(bookingRepository, times(1))
                .findByBookerAndStatus(user, BookingStatus.WAITING, CustomPageRequest
                        .of(from, size, Sort.by("start").descending()));
    }

    @Test
    void getOwnerBookings() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Booking booking1 = new Booking();
        booking1.setItem(item);
        booking1.setStatus(BookingStatus.APPROVED);

        Booking booking2 = new Booking();
        booking2.setItem(item);
        booking2.setStatus(BookingStatus.REJECTED);

        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking1);
        bookingList.add(booking2);
        Page<Booking> bookings = new PageImpl<>(bookingList);

        when(bookingRepository.findByItemOwner(user, Pageable.unpaged())).thenReturn(bookings);

        List<Booking> allBookings = bookingService.getOwnerBookings("ALL", user.getId(), Pageable.unpaged());
        assertEquals(bookings.getContent(), allBookings);
        verify(bookingRepository, times(1)).findByItemOwner(user, Pageable.unpaged());

        reset(bookingRepository);

        when(bookingRepository.findByItemOwnerAndStatus(user, BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(bookings);

        List<Booking> waitingBookings = bookingService.getOwnerBookings("WAITING", user.getId(),
                Pageable.unpaged());
        assertEquals(bookings.getContent(), waitingBookings);
        verify(bookingRepository, times(1)).findByItemOwnerAndStatus(user,
                BookingStatus.WAITING, Pageable.unpaged());
    }

    @Test
    void createItemDoesNotExistThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            bookingService.create(bookingDto, userId);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void createItemNotAvailableThrowsException() {
        item.setAvailable(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> {
            bookingService.create(bookingDto, userId);
        });
    }

    @Test
    void createEndDateBeforeStartDateThrowsException() {
        LocalDateTime start = LocalDateTime.now().plusDays(2L);
        LocalDateTime end = LocalDateTime.now().plusDays(1L);

        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> {
            bookingService.create(bookingDto, userId);
        });
    }

    @Test
    void createEndDateEqualsStartDateThrowsException() {
        LocalDateTime start = LocalDateTime.of(2023, 5, 5, 10, 10);
        LocalDateTime end = LocalDateTime.of(2023, 5, 5, 10, 10);

        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> {
            bookingService.create(bookingDto, userId);
        });
    }

    @Test
    void createBookOwnItemThrowsException() {
        Long userId = 2L;
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(NoSuchIdException.class, () -> {
            bookingService.create(bookingDto, userId);
        });
    }

    @Test
    void createBookingConflictThrowsException() {
        List<Booking> bookings = new ArrayList<>();

        Booking existingBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .item(item)
                .booker(User.builder().id(3L).build())
                .status(BookingStatus.APPROVED)
                .build();

        bookings.add(existingBooking);
        when(bookingRepository.findByItem(item)).thenReturn(bookings);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(NoSuchIdException.class, () -> {
            bookingService.create(bookingDto, userId);
        });
    }

    @Test
    void getUserBookingsWithNonExistentUserIdThrowsException() {
        Long nonExistentUserId = 123L; // Non-existent user ID
        String stateString = "ALL";
        Integer from = 0;
        Integer size = 5;

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        assertThrows(NoSuchIdException.class, () -> {
            bookingService.getUserBookings(stateString, nonExistentUserId, from, size);
        });
    }

    @Test
    void getUserBookingsWithInvalidStateThrowsException() {
        String invalidStateString = "INVALID_STATE";
        Integer from = 0;
        Integer size = 5;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UnsupportedStateException.class, () -> {
            bookingService.getUserBookings(invalidStateString, userId, from, size);
        });
    }

    @Test
    void getOwnerBookingsWithNonExistentUserIdThrowsException() {
        Long nonExistentUserId = 123L; // Non-existent user ID
        String stateString = "ALL";
        Pageable pageable = PageRequest.of(0, 5);

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        assertThrows(NoSuchIdException.class, () -> {
            bookingService.getOwnerBookings(stateString, nonExistentUserId, pageable);
        });
    }

    @Test
    void getOwnerBookingsWithInvalidStateThrowsException() {
        String invalidStateString = "INVALID_STATE";
        Pageable pageable = PageRequest.of(0, 5);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UnsupportedStateException.class, () -> {
            bookingService.getOwnerBookings(invalidStateString, userId, pageable);
        });
    }

    @Test
    void getUserBookingsPastBookingsReturnsPastBookings() {
        Booking pastBooking = createPastBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerAndEndIsBefore(eq(user), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(pastBooking)));

        List<Booking> bookings = bookingService.getUserBookings(BookingRequestState.PAST.name(),
                user.getId(), 0, 10);

        assertTrue(bookings.contains(pastBooking));
    }

    @Test
    void getUserBookingsFutureBookingsReturnsFutureBookings() {
        Booking futureBooking = createFutureBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerAndStartIsAfter(eq(user), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(futureBooking)));

        List<Booking> bookings = bookingService.getUserBookings(BookingRequestState.FUTURE.name(),
                user.getId(), 0, 10);

        assertTrue(bookings.contains(futureBooking));
    }

    @Test
    void getUserBookingsCurrentBookingsReturnsCurrentBookings() {
        Booking currentBooking = createCurrentBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfter(eq(user),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(currentBooking)));

        List<Booking> bookings = bookingService.getUserBookings(BookingRequestState
                .CURRENT.name(), user.getId(), 0, 10);

        assertTrue(bookings.contains(currentBooking));
    }

    @Test
    void getOwnerBookingsAllReturnsBookings() {
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwner(user, pageable)).thenReturn(new PageImpl<>(List.of(booking, pastBooking,
                futureBooking, currentBooking)));

        List<Booking> result = bookingService.getOwnerBookings("ALL", userId, pageable);

        assertEquals(4, result.size());
    }

    @Test
    void getOwnerBookingsPastReturnsPastBookings() {
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerAndEndIsBefore(eq(user), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(pastBooking)));

        List<Booking> result = bookingService.getOwnerBookings("PAST", userId, pageable);

        assertEquals(1, result.size());
        assertTrue(result.contains(pastBooking));
    }

    @Test
    void getOwnerBookingsCurrentReturnsCurrentBookings() {
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerAndStartIsBeforeAndEndIsAfter(eq(user), any(LocalDateTime.class),
                any(LocalDateTime.class), eq(pageable))).thenReturn(new PageImpl<>(List.of(currentBooking)));

        List<Booking> result = bookingService.getOwnerBookings("CURRENT", userId, pageable);

        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookingsWaitingReturnsWaitingBookings() {
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerAndStatus(user, BookingStatus.WAITING, pageable))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> result = bookingService.getOwnerBookings("WAITING", userId, pageable);

        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
    }

    @Test
    void getOwnerBookingsRejectedReturnsRejectedBookings() {
        Pageable pageable = Pageable.unpaged();
        Booking rejectedBooking = Booking.builder()
                .id(7L)
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerAndStatus(user, BookingStatus.REJECTED, pageable))
                .thenReturn(new PageImpl<>(List.of(rejectedBooking)));

        List<Booking> result = bookingService.getOwnerBookings("REJECTED", userId, pageable);

        assertEquals(1, result.size());
        assertTrue(result.contains(rejectedBooking));
    }

    @Test
    void getOwnerBookingsInvalidStateThrowsException() {
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UnsupportedStateException.class, () -> bookingService
                .getOwnerBookings("INVALID", userId, pageable));
    }

    @Test
    void getOwnerBookingsNullStateThrowsException() {
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UnsupportedStateException.class, () -> bookingService
                .getOwnerBookings(null, userId, pageable));
    }
}
