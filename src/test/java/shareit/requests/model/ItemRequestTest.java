package shareit.requests.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;

class ItemRequestTest {

    @Test
    void testEquals() {
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(1L)
                .build();

        ItemRequest itemRequest3 = ItemRequest.builder()
                .id(null)
                .build();

        Assertions.assertEquals(itemRequest1, itemRequest2);
        Assertions.assertEquals(itemRequest1, itemRequest1);
        Assertions.assertNotEquals(itemRequest1, itemRequest3);
        Assertions.assertNotEquals(itemRequest3, itemRequest1);
    }

    @Test
    void testHashCode() {
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(1L)
                .build();

        Assertions.assertEquals(itemRequest1.hashCode(), itemRequest2.hashCode());
    }
}