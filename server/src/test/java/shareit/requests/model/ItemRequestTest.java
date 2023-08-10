package shareit.requests.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;

class ItemRequestTest {

    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;

    @BeforeEach
    void setUp() {
        itemRequest1 = ItemRequest.builder()
                .id(1L)
                .build();

        itemRequest2 = ItemRequest.builder()
                .id(1L)
                .build();

        itemRequest3 = ItemRequest.builder()
                .id(null)
                .build();
    }

    @Test
    void testEquals() {
        Assertions.assertEquals(itemRequest1, itemRequest2);
        Assertions.assertEquals(itemRequest1, itemRequest1);
        Assertions.assertNotEquals(itemRequest1, itemRequest3);
        Assertions.assertNotEquals(itemRequest3, itemRequest1);
    }

    @Test
    void testHashCode() {
        Assertions.assertEquals(itemRequest1.hashCode(), itemRequest2.hashCode());
    }
}
