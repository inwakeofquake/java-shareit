package shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = ShareItServer.class)
class ItemServiceIntegrationTest {

    @Autowired
    private final ItemService itemService;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        User owner = User.builder()
                .name("owner")
                .email("owner@email.com")
                .build();
        userRepository.save(owner);

        Item item = Item.builder()
                .name("item1")
                .description("item1 desc")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .name("booker")
                .email("booker@email.com")
                .build();

        LocalDateTime created = LocalDateTime.now();

        Booking lastBooking = Booking.builder()
                .start(created.minusMonths(5))
                .end(created.minusMonths(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        Booking nextBooking = Booking.builder()
                .start(created.plusDays(1L))
                .end(created.plusDays(2L))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(lastBooking);
        bookingRepository.save(nextBooking);

    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DirtiesContext
    @Test
    void create() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        Item createdItem = itemService.add(itemDto, 1L);

        assertThat(createdItem, is(notNullValue()));
        assertThat(createdItem.getId(), is(notNullValue()));
    }

    @DirtiesContext
    @Test
    void getItem() throws Exception {
        Optional<User> owner = userRepository.findByName("owner");
        Long ownerId = owner.get().getId();
        Item savedItem = itemRepository.findByName("item1");
        Long itemId = savedItem.getId();

        ItemDto itemDto = itemService.get(itemId, ownerId);

        assertThat(itemDto.getId(), is(itemId));
    }
}