package shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utility.NoSuchIdException;
import ru.practicum.shareit.utility.UnauthorizedAccessException;
import ru.practicum.shareit.utility.UnsupportedStateException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

        itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
    }

    @Test
    void addItemDtoAndUserIdItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Item result = itemService.add(itemDto, 1L);

        assertEquals("Item", result.getName());
        assertEquals("Description", result.getDescription());
        assertEquals(true, result.getAvailable());
        assertEquals(user, result.getOwner());
    }

    @Test
    void addItemDtoAndUserIdAndInvalidRequestIdThrowsNoSuchIdException() {
        assertThrows(NoSuchIdException.class, () -> itemService.add(itemDto, 1L));
    }

    @Test
    void addItemDtoAndInvalidUserIdThrowsNoSuchIdException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchIdException.class, () -> itemService.add(itemDto, 1L));
    }

    @Test
    void addUserDoesNotExistThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchIdException.class, () -> itemService.add(itemDto, 1L));
    }

    @Test
    void updateValidInputItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDto newItemDto = new ItemDto();
        newItemDto.setName("New Item");
        newItemDto.setDescription("New Description");
        newItemDto.setAvailable(true);

        Item result = itemService.update(1L, newItemDto, 1L);

        assertEquals("New Item", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(true, result.getAvailable());
        assertEquals(user, result.getOwner());
    }

    @Test
    void updateInvalidUserIdThrowsResponseStatusException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDto newItemDto = new ItemDto();
        newItemDto.setName("New Item");
        newItemDto.setDescription("New Description");
        newItemDto.setAvailable(true);

        assertThrows(ResponseStatusException.class, () -> itemService.update(1L, newItemDto, 2L));
    }

    @Test
    void getValidInputItemDto() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBooking(any(Item.class),
                any(LocalDateTime.class), any(BookingStatus.class),
                any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));
        when(bookingRepository.findNextBooking(any(Item.class),
                any(LocalDateTime.class),
                any(BookingStatus.class),
                any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        ItemDto result = itemService.get(1L, 1L);

        assertEquals("Item", result.getName());
        assertEquals("Description", result.getDescription());
        assertEquals(true, result.getAvailable());
        assertEquals(user.getId(), result.getOwner().getId());
    }

    @Test
    void getItemDoesNotExistThrowsException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> itemService.get(1L, 1L));
    }

    @Test
    void getAllValidInputListOfItemDtos() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item1");
        item1.setDescription("Description1");
        item1.setAvailable(true);
        item1.setOwner(user);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item2");
        item2.setDescription("Description2");
        item2.setAvailable(false);
        item2.setOwner(user);

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwner(user, Sort.by("id").ascending())).thenReturn(items);
        when(bookingRepository.findLastBooking(any(Item.class),
                any(LocalDateTime.class), any(BookingStatus.class),
                any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));
        when(bookingRepository.findNextBooking(any(Item.class),
                any(LocalDateTime.class),
                any(BookingStatus.class),
                any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        List<ItemDto> result = itemService.getAll(1L);

        assertEquals(2, result.size());
        assertEquals("Item1", result.get(0).getName());
        assertEquals("Item2", result.get(1).getName());
    }

    @Test
    void searchValidInputListOfItems() {
        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.search("Item")).thenReturn(items);

        List<Item> result = itemService.search("Item");

        assertEquals(1, result.size());
        assertEquals("Item", result.get(0).getName());
    }

    @Test
    void deleteValidInputNoReturn() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> itemService.delete(1L, 1L));
    }

    @Test
    void deleteNonOwnerUserThrowsException() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("anotherUser");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));

        assertThrows(UnauthorizedAccessException.class, () -> itemService.delete(1L, 2L));
    }

    @Test
    void addCommentValidInputCommentDto() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment");

        Comment comment = new Comment();
        comment.setText("Comment");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndEndIsBefore(any(Long.class),
                any(LocalDateTime.class),
                any(Sort.class))).thenReturn(bookings);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(1L, commentDto, 1L);

        assertNotNull(result);
        assertEquals("Comment", result.getText());
        assertEquals(user.getName(), result.getAuthorName());
    }

    @Test
    void addCommentBlankCommentThrowsException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("");

        assertThrows(UnsupportedStateException.class, () -> {
            itemService.addComment(1L, commentDto, 1L);
        });
    }

    @Test
    void addCommentUserHasNoBookingsThrowsUnsupportedStateException() {

        Long itemId = 1L;
        Long userId = 1L;

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment");

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);

        List<Booking> bookings = new ArrayList<>();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndEndIsBefore(eq(userId), any(LocalDateTime.class),
                eq(Sort.by(Sort.Direction.DESC, "end")))).thenReturn(bookings);

        assertThrows(UnsupportedStateException.class, () -> {
            itemService.addComment(itemId, commentDto, userId);
        });
    }
}

