package shareit.item.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.model.Item;

@ContextConfiguration(classes = ShareItApp.class)
class ItemTest {

    @Test
    void testEquals() {
        Item item1 = Item.builder()
                .id(1L)
                .build();

        Item item2 = Item.builder()
                .id(1L)
                .build();

        Item item3 = Item.builder()
                .id(null)
                .build();

        Assertions.assertEquals(item1, item2);
        Assertions.assertEquals(item1, item1);
        Assertions.assertNotEquals(item1, item3);
        Assertions.assertNotEquals(item3, item1);
    }

    @Test
    void testHashCode() {
        Item item1 = Item.builder()
                .id(1L)
                .build();

        Item item2 = Item.builder()
                .id(1L)
                .build();

        Assertions.assertEquals(item1.hashCode(), item2.hashCode());
    }
}