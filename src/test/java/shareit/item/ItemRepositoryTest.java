package shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@DataJpaTest
@ContextConfiguration(classes = ShareItApp.class)
class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByOwner() {
        User owner = User.builder()
                .name("user2")
                .email("user2@email.com")
                .build();

        owner = userRepository.save(owner);

        Item item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        item = itemRepository.save(item);

        List<Item> items = itemRepository.findByOwner(owner, Sort.unsorted());
        Assertions.assertFalse(items.isEmpty());
        Assertions.assertEquals(items.get(0).getOwner(), owner);
    }

    @Test
    void searchAvailableByText() {
        User owner = User.builder()
                .name("user1")
                .email("user1@email.com")
                .build();

        owner = userRepository.save(owner);

        Item item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        item = itemRepository.save(item);

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Item> items = itemRepository.search("name");
        Assertions.assertTrue(items.get(0).getName().contains(item.getName()));
    }

    @Test
    void searchAvailableByText_inDescription() {
        User owner = User.builder()
                .name("user3")
                .email("user3@email.com")
                .build();

        owner = userRepository.save(owner);

        Item item = Item.builder()
                .name("name")
                .description("specific description")
                .available(true)
                .owner(owner)
                .build();

        item = itemRepository.save(item);

        List<Item> items = itemRepository.search("specific");
        Assertions.assertTrue(items.get(0).getDescription().contains("specific"));
    }

    @Test
    void searchDoesntReturnUnavailableItems() {
        User owner = User.builder()
                .name("user4")
                .email("user4@email.com")
                .build();

        owner = userRepository.save(owner);

        Item item = Item.builder()
                .name("unavailable item")
                .description("description")
                .available(false)
                .owner(owner)
                .build();

        item = itemRepository.save(item);

        List<Item> items = itemRepository.search("unavailable");
        Assertions.assertTrue(items.isEmpty());
    }
}