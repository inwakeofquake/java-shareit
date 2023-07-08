package shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItServer.class)
class BookingDtoTest {

    BookingDto bookingDto;
    JsonContent<BookingDto> result;

    @Autowired
    private JacksonTester<BookingDto> json;

    @BeforeEach
    void setUp() throws Exception {
        bookingDto = new BookingDto(
                1L,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3),
                ItemDto.builder().build(),
                1L,
                1L,
                BookingStatus.WAITING
        );
        result = json.write(bookingDto);
    }

    @Test
    void testBookingDtoId() throws Exception {

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }

    @Test
    void testBookingDtoStart() {

        assertThat(result).hasJsonPath("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(
                bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void testBookingDtoEnd() {

        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(
                bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void testBookingDtoItemId() {

        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }

    @Test
    void testBookingDtoBookerId() {

        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }

    @Test
    void testBookingDtoStatus() {

        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
