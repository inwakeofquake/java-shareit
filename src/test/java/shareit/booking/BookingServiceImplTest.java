package shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
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

        List<Booking> allBookings = bookingService.getUserBookings("ALL", user.getId(), Pageable.unpaged());
        assertEquals(bookings.getContent(), allBookings);
        verify(bookingRepository, times(1)).findByBooker(user, Pageable.unpaged());

        reset(bookingRepository);

        when(bookingRepository.findByBookerAndStatus(user, BookingStatus.WAITING, Pageable.unpaged()))
                .thenReturn(bookings);

        List<Booking> waitingBookings = bookingService.getUserBookings("WAITING", user.getId(),
                Pageable.unpaged());
        assertEquals(bookings.getContent(), waitingBookings);
        verify(bookingRepository, times(1)).findByBookerAndStatus(user, BookingStatus.WAITING,
                Pageable.unpaged());
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

}