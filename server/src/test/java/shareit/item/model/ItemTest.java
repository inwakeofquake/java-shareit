package shareit.item.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.model.Item;

@ContextConfiguration(classes = ShareItServer.class)
class ItemTest {

    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        item1 = Item.builder()
                .id(1L)
                .build();

        item2 = Item.builder()
                .id(1L)
                .build();

        item3 = Item.builder()
                .id(null)
                .build();
    }

    @Test
    void testEquals() {
        Assertions.assertEquals(item1, item2);
        Assertions.assertEquals(item1, item1);
        Assertions.assertNotEquals(item1, item3);
        Assertions.assertNotEquals(item3, item1);
    }

    @Test
    void testHashCode() {
        Assertions.assertEquals(item1.hashCode(), item2.hashCode());
    }
}
