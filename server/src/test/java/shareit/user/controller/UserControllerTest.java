package shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utility.InvalidInputException;
import ru.practicum.shareit.utility.NoSuchIdException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = ShareItServer.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService mockUserService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDtoCreateTest;

    private UserDto userDtoCreated;

    private UserDto userDtoUpdateTest;

    private UserDto userDtoUpdated;

    @BeforeEach
    void setUp() {
        userDtoCreateTest = UserDto.builder()
                .name("userCreate")
                .email("userTest@email.com")
                .build();
        userDtoCreated = UserDto.builder()
                .id(1L)
                .name("userCreate")
                .email("userTest@email.com")
                .build();
        userDtoUpdateTest = UserDto.builder()
                .name("userUpdate")
                .build();
        userDtoUpdated = UserDto.builder()
                .id(1L)
                .name("userUpdate")
                .email("userTest@email.com")
                .build();
    }

    @AfterEach
    void tearDown() {
        userDtoCreateTest = null;
        userDtoCreated = null;
        userDtoUpdateTest = null;
        userDtoUpdated = null;
    }

    @Test
    void create() throws Exception {
        when(mockUserService.add(userDtoCreateTest))
                .thenReturn(userDtoCreated);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoCreateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoCreated.getName())))
                .andExpect(jsonPath("$.email", is(userDtoCreated.getEmail())));
    }

    @Test
    void createInvalidInputThrowsException() throws Exception {
        UserDto invalidUserDto = UserDto.builder().build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        when(mockUserService.update(1L, userDtoUpdateTest))
                .thenReturn(userDtoUpdated);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoUpdateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdated.getName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdated.getEmail())));
    }

    @Test
    void updateUserDoesNotExistThrowsException() throws Exception {
        when(mockUserService.update(1L, userDtoUpdateTest))
                .thenThrow(new NoSuchIdException("User not found"));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoUpdateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserDoesNotExistThrowsInvalidInputException() throws Exception {
        when(mockUserService.update(1L, userDtoUpdateTest))
                .thenThrow(new InvalidInputException("User not found"));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoUpdateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void getUserDto() throws Exception {
        when(mockUserService.getById(1L))
                .thenReturn(userDtoUpdated);

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdated.getName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdated.getEmail())));
    }

    @Test
    void getUserDtoUserDoesNotExistThrowsException() throws Exception {
        when(mockUserService.getById(1L))
                .thenThrow(new NoSuchIdException("User not found"));

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserDto() throws Exception {
        mvc.perform(delete("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(mockUserService).delete(1L);
    }

    @Test
    void deleteUserDtoUserDoesNotExistThrowsException() throws Exception {
        doThrow(new NoSuchIdException("User not found"))
                .when(mockUserService).delete(1L);

        mvc.perform(delete("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers() throws Exception {
        when(mockUserService.getAll())
                .thenReturn(List.of(userDtoUpdated));

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDtoUpdated.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDtoUpdated.getName())))
                .andExpect(jsonPath("$[0].email", is(userDtoUpdated.getEmail())));
    }
}