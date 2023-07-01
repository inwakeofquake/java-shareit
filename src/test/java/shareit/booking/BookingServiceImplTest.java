package shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingRequestState;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
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
    void create_UserDoesNotExist_ThrowsException() {
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
    void approveOrReject_notFound() {
        Long bookingId = 1L;
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> bookingService.approveOrReject(bookingId, approved, userId));

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void approveOrReject_UserIsNotOwner_ThrowsException() {
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

        when(bookingRepository.findByBooker(user, Pageable.unpaged())).thenReturn(bookings);

        List<Booking> allBookings = bookingService.getUserBookings("ALL", user.getId(), null, null);
        assertEquals(bookings.getContent(), allBookings);
        verify(bookingRepository, times(1)).findByBooker(user, Pageable.unpaged());

        reset(bookingRepository);

        when(bookingRepository.findByBookerAndStatus(user, BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(bookings);

        List<Booking> waitingBookings = bookingService.getUserBookings("WAITING", user.getId(),
                null, null);
        assertEquals(bookings.getContent(), waitingBookings);
        verify(bookingRepository, times(1)).findByBookerAndStatus(user, BookingStatus.WAITING,
                Pageable.unpaged());
    }

    @Test
    void getUserBookingsThrowsBadPageParams() {
        assertThrows(BadRequestException.class, () -> bookingService
                .getUserBookings("ALL", 1L, -1, 0));
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
    void create_ItemDoesNotExist_ThrowsException() {
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
    void create_ItemNotAvailable_ThrowsException() {
        item.setAvailable(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> {
            bookingService.create(bookingDto, userId);
        });
    }

    @Test
    void create_EndDateBeforeStartDate_ThrowsException() {
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
    void create_EndDateEqualsStartDate_ThrowsException() {
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
    void create_BookOwnItem_ThrowsException() {
        Long userId = 2L;
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(NoSuchIdException.class, () -> {
            bookingService.create(bookingDto, userId);
        });
    }

    @Test
    void create_BookingConflict_ThrowsException() {
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
    void getUserBookings_WithNonExistentUserId_ThrowsException() {
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
    void getUserBookings_WithInvalidState_ThrowsException() {
        String invalidStateString = "INVALID_STATE";
        Integer from = 0;
        Integer size = 5;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UnsupportedStateException.class, () -> {
            bookingService.getUserBookings(invalidStateString, userId, from, size);
        });
    }

    @Test
    void getUserBookings_WithInvalidFrom_ThrowsException() {
        String stateString = "ALL";
        Integer invalidFrom = -1;
        Integer size = 5;

        assertThrows(BadRequestException.class, () -> {
            bookingService.getUserBookings(stateString, userId, invalidFrom, size);
        });
    }

    @Test
    void getUserBookings_WithInvalidSize_ThrowsException() {
        String stateString = "ALL";
        Integer from = 0;
        Integer invalidSize = 0;

        assertThrows(BadRequestException.class, () -> {
            bookingService.getUserBookings(stateString, userId, from, invalidSize);
        });
    }

    @Test
    void getOwnerBookings_WithNonExistentUserId_ThrowsException() {
        Long nonExistentUserId = 123L; // Non-existent user ID
        String stateString = "ALL";
        Pageable pageable = PageRequest.of(0, 5);

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        assertThrows(NoSuchIdException.class, () -> {
            bookingService.getOwnerBookings(stateString, nonExistentUserId, pageable);
        });
    }

    @Test
    void getOwnerBookings_WithInvalidState_ThrowsException() {
        String invalidStateString = "INVALID_STATE";
        Pageable pageable = PageRequest.of(0, 5);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UnsupportedStateException.class, () -> {
            bookingService.getOwnerBookings(invalidStateString, userId, pageable);
        });
    }

    @Test
    void getUserBookings_PastBookings_ReturnsPastBookings() {
        Booking pastBooking = createPastBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerAndEndIsBefore(eq(user), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(pastBooking)));

        List<Booking> bookings = bookingService.getUserBookings(BookingRequestState.PAST.name(),
                user.getId(), 0, 10);

        assertTrue(bookings.contains(pastBooking));
    }

    @Test
    void getUserBookings_FutureBookings_ReturnsFutureBookings() {
        Booking futureBooking = createFutureBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerAndStartIsAfter(eq(user), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(futureBooking)));

        List<Booking> bookings = bookingService.getUserBookings(BookingRequestState.FUTURE.name(),
                user.getId(), 0, 10);

        assertTrue(bookings.contains(futureBooking));
    }

    @Test
    void getUserBookings_CurrentBookings_ReturnsCurrentBookings() {
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
    void getOwnerBookings_All_ReturnsBookings() {
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwner(user, pageable)).thenReturn(new PageImpl<>(List.of(booking, pastBooking,
                futureBooking, currentBooking)));

        List<Booking> result = bookingService.getOwnerBookings("ALL", userId, pageable);

        assertEquals(4, result.size());
    }

    @Test
    void getOwnerBookings_Past_ReturnsPastBookings() {
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerAndEndIsBefore(eq(user), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(pastBooking)));

        List<Booking> result = bookingService.getOwnerBookings("PAST", userId, pageable);

        assertEquals(1, result.size());
        assertTrue(result.contains(pastBooking));
    }

    @Test
    void getOwnerBookings_Current_ReturnsCurrentBookings() {
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerAndStartIsBeforeAndEndIsAfter(eq(user), any(LocalDateTime.class),
                any(LocalDateTime.class), eq(pageable))).thenReturn(new PageImpl<>(List.of(currentBooking)));

        List<Booking> result = bookingService.getOwnerBookings("CURRENT", userId, pageable);

        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookings_Waiting_ReturnsWaitingBookings() {
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerAndStatus(user, BookingStatus.WAITING, pageable))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> result = bookingService.getOwnerBookings("WAITING", userId, pageable);

        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
    }

    @Test
    void getOwnerBookings_Rejected_ReturnsRejectedBookings() {
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
    void getOwnerBookings_InvalidState_ThrowsException() {
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UnsupportedStateException.class, () -> bookingService
                .getOwnerBookings("INVALID", userId, pageable));
    }

    @Test
    void getOwnerBookings_NullState_ThrowsException() {
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UnsupportedStateException.class, () -> bookingService
                .getOwnerBookings(null, userId, pageable));
    }
}
