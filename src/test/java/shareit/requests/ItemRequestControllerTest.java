package shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utility.InvalidInputException;
import ru.practicum.shareit.utility.NoSuchIdException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utility.Constants.HEADER_USER_ID;

@WebMvcTest(ItemRequestController.class)
@ContextConfiguration(classes = ShareItApp.class)
class ItemRequestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemRequestServiceImpl mockItemRequestServiceImpl;

    private User user;
    private ItemRequestDto requestDto;
    private List<ItemRequestDto> requests;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("John").email("john@example.com").build();
        requestDto = ItemRequestDto.builder()
                .description(UUID.randomUUID().toString())
                .user(user)
                .created(LocalDateTime.now())
                .build();

        requests = new ArrayList<>();
        IntStream.rangeClosed(1, 5).forEach(i -> {
            ItemRequestDto request = ItemRequestDto.builder()
                    .description("Request " + i)
                    .user(user)
                    .created(LocalDateTime.now())
                    .build();
            requests.add(request);
        });
    }

    @AfterEach
    void tearDown() {
        user = null;
        requestDto = null;
        requests = null;
    }

    @Test
    void testCreateRequest() throws Exception {

        when(mockItemRequestServiceImpl.createRequest(any(ItemRequestDto.class),
                anyLong())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header(HEADER_USER_ID, "1")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.user.id", is(user.getId().intValue())));
    }

    @Test
    void testGetOwnRequests() throws Exception {

        when(mockItemRequestServiceImpl.getOwnRequests(anyLong())).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(requests.size())))
                .andExpect(jsonPath("$[0].user.id", is(user.getId().intValue())));
    }

    @Test
    void testGetAllRequests() throws Exception {

        when(mockItemRequestServiceImpl.getAllRequests(anyLong(), any(PageRequest.class))).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(requests.size())))
                .andExpect(jsonPath("$[0].user.id", is(user.getId().intValue())));
    }

    @Test
    void testGetRequest() throws Exception {

        when(mockItemRequestServiceImpl.getRequest(anyLong(), anyLong())).thenReturn(requestDto);

        mockMvc.perform(get("/requests/1")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.user.id", is(user.getId().intValue())));
    }

    @Test
    void testUserNotFound() throws Exception {

        when(mockItemRequestServiceImpl.getOwnRequests(anyLong())).thenThrow(new NoSuchIdException("No such ID"));

        mockMvc.perform(get("/requests")
                        .header(HEADER_USER_ID, "99")) // Nonexistent User ID
                .andExpect(status().isNotFound());
    }

    @Test
    void testEmptyList() throws Exception {

        List<ItemRequestDto> emptyList = new ArrayList<>();
        when(mockItemRequestServiceImpl.getOwnRequests(anyLong())).thenReturn(emptyList);

        mockMvc.perform(get("/requests")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testCreateInvalidRequest() throws Exception {

        ItemRequestDto invalidRequestDto = new ItemRequestDto();
        invalidRequestDto.setDescription("");

        when(mockItemRequestServiceImpl.createRequest(any(ItemRequestDto.class),
                anyLong())).thenThrow(new InvalidInputException("Invalid input"));

        mockMvc.perform(post("/requests")
                        .header(HEADER_USER_ID, "1")
                        .content(objectMapper.writeValueAsString(invalidRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllRequestsWithBadParams() throws Exception {

        mockMvc.perform(get("/requests/all?from=-1")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/requests/all?size=0")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isBadRequest());
    }

}


