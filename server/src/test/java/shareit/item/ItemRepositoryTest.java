//package shareit.item;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.domain.Sort;
//import org.springframework.test.context.ContextConfiguration;
//import ru.practicum.shareit.ShareItServer;
//import ru.practicum.shareit.item.ItemRepository;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.user.UserRepository;
//import ru.practicum.shareit.user.model.User;
//
//import java.util.List;
//
//@DataJpaTest
//@ContextConfiguration(classes = ShareItServer.class)
//class ItemRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ItemRepository itemRepository;
//
//    private User owner;
//    private Item item;
//
//    @BeforeEach
//    void setUp() {
//        owner = User.builder()
//                .name("user1")
//                .email("user1@email.com")
//                .build();
//
//        owner = userRepository.save(owner);
//
//        item = Item.builder()
//                .name("name")
//                .description("description")
//                .available(true)
//                .owner(owner)
//                .build();
//
//        item = itemRepository.save(item);
//    }
//
//    @Test
//    void findByOwner() {
//        List<Item> items = itemRepository.findByOwner(owner, Sort.unsorted());
//        Assertions.assertFalse(items.isEmpty());
//        Assertions.assertEquals(items.get(0).getOwner(), owner);
//    }
//
//    @Test
//    void searchAvailableByText() {
//        List<Item> items = itemRepository.search("name");
//        Assertions.assertTrue(items.get(0).getName().contains(item.getName()));
//    }
//
//    @Test
//    void searchAvailableByTextinDescription() {
//        item.setDescription("specific description");
//        item = itemRepository.save(item);
//
//        List<Item> items = itemRepository.search("specific");
//        Assertions.assertTrue(items.get(0).getDescription().contains("specific"));
//    }
//
//    @Test
//    void searchDoesntReturnUnavailableItems() {
//        item.setName("unavailable item");
//        item.setAvailable(false);
//        item = itemRepository.save(item);
//
//        List<Item> items = itemRepository.search("unavailable");
//        Assertions.assertTrue(items.isEmpty());
//    }
//}
