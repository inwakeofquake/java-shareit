package shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NoSuchIdException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

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

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");
    }

    @Test
    void add_ItemDtoAndUserId_Item() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item result = itemService.add(itemDto, 1L);

        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(true, result.getAvailable());
        assertEquals(user, result.getOwner());
    }

    @Test
    void add_ItemDtoAndInvalidUserId_ThrowsNoSuchIdException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        assertThrows(NoSuchIdException.class, () -> itemService.add(itemDto, 1L));
    }

    @Test
    void add_UserDoesNotExist_ThrowsException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchIdException.class, () -> {
            itemService.add(itemDto, 1L);
        });
    }

    @Test
    void update_ValidInput_Item() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Old Item");
        item.setDescription("Old Description");
        item.setAvailable(false);
        item.setOwner(user);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item");
        itemDto.setDescription("New Description");
        itemDto.setAvailable(true);

        Item result = itemService.update(1L, itemDto, 1L);

        assertEquals("New Item", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(true, result.getAvailable());
        assertEquals(user, result.getOwner());
    }

    @Test
    void update_InvalidUserId_ThrowsResponseStatusException() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Old Item");
        item.setDescription("Old Description");
        item.setAvailable(false);
        item.setOwner(user);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item");
        itemDto.setDescription("New Description");
        itemDto.setAvailable(true);

        assertThrows(ResponseStatusException.class, () -> itemService.update(1L, itemDto, 2L));
    }

    @Test
    void get_ValidInput_ItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDto result = itemService.get(1L, 1L);

        assertEquals("Item", result.getName());
        assertEquals("Description", result.getDescription());
        assertEquals(true, result.getAvailable());
        assertEquals(user.getId(), result.getOwner().getId());
    }

    @Test
    void get_ItemDoesNotExist_ThrowsException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            itemService.get(1L, 1L);
        });
    }

    @Test
    void getAll_ValidInput_ListOfItemDtos() {
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

        List<ItemDto> result = itemService.getAll(1L);

        assertEquals(2, result.size());
        assertEquals("Item1", result.get(0).getName());
        assertEquals("Item2", result.get(1).getName());
    }

    @Test
    void search_ValidInput_ListOfItems() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.search("Item")).thenReturn(items);

        List<Item> result = itemService.search("Item");

        assertEquals(1, result.size());
        assertEquals("Item", result.get(0).getName());
    }

    @Test
    void delete_ValidInput_NoReturn() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> itemService.delete(1L, 1L));
    }

    @Test
    void delete_NonOwnerUser_ThrowsException() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("anotherUser");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));

        assertThrows(UnauthorizedAccessException.class, () -> {
            itemService.delete(1L, 2L);
        });
    }

    @Test
    void addComment_ValidInput_CommentDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

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
        when(bookingRepository.findByBooker_IdAndEndIsBefore(any(Long.class),
                any(LocalDateTime.class),
                any(Sort.class))).thenReturn(bookings);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(1L, commentDto, 1L);

        assertNotNull(result);
        assertEquals("Comment", result.getText());
        assertEquals(user.getName(), result.getAuthorName());
    }

    @Test
    void addComment_BlankComment_ThrowsException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("");

        assertThrows(UnsupportedStateException.class, () -> {
            itemService.addComment(1L, commentDto, 1L);
        });
    }
}
