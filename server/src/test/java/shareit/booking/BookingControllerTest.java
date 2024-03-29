package shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.utility.UnsupportedStateException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utility.Constants.HEADER_USER_ID;

@WebMvcTest(controllers = BookingController.class)
@ContextConfiguration(classes = ShareItServer.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;

    private Booking booking;

    private LocalDateTime start;

    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1L);

        end = LocalDateTime.now().plusDays(2L);

        bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
    }

    @AfterEach
    void tearDown() {
        start = null;
        end = null;
        bookingDto = null;
        booking = null;
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(bookingDto, 1L))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void approveOrReject() throws Exception {
        when(bookingService.approveOrReject(1L, true, 2L))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header(HEADER_USER_ID, 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.get(1L, 1L))
                .thenReturn(booking);

        mvc.perform(get("/bookings/1")
                        .header(HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUserBookings() throws Exception {
        when(bookingService.getUserBookings("ALL", 1L, 0, 1000))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "1000")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUserBookingsBadRequest() throws Exception {
        mvc.perform(get("/bookings/")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "-1")
                        .param("size", "0")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserBookingsUnsupportedState() throws Exception {
        when(bookingService.getUserBookings("SOMES", 1L, 0, 10))
                .thenThrow(new UnsupportedStateException("Invalid state"));

        mvc.perform(get("/bookings/")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "SOMES")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOwnerBookings() throws Exception {
        when(bookingService.getOwnerBookings("ALL", 1L, PageRequest.of(0, 1000, Sort.by("start").descending())))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "1000")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
