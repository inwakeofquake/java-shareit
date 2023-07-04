package shareit.booking.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;

class BookingTest {

    @Test
    void testEquals() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .build();

        Booking booking2 = Booking.builder()
                .id(1L)
                .build();

        Booking booking3 = Booking.builder()
                .id(null)
                .build();

        Assertions.assertEquals(booking1, booking2);
        Assertions.assertEquals(booking1, booking1);
        Assertions.assertNotEquals(booking1, booking3);
        Assertions.assertNotEquals(booking3, booking1);
    }

    @Test
    void testHashCode() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .build();

        Booking booking2 = Booking.builder()
                .id(1L)
                .build();

        Assertions.assertEquals(booking1.hashCode(), booking2.hashCode());
    }
}