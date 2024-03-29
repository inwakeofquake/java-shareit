package shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utility.InvalidInputException;
import ru.practicum.shareit.utility.NoSuchIdException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = ShareItServer.class)
class UserServiceImplTest {

    @Autowired
    private final UserServiceImpl userService;

    @MockBean
    private final UserRepository userRepository;

    private List<User> users;

    @BeforeEach
    void setUp() {
        UserDto user1 = UserDto.builder()
                .id(1L)
                .name("test name")
                .email("test@test.ru")
                .build();
        UserDto user2 = UserDto.builder()
                .id(2L)
                .name("test name 2")
                .email("test2@test.ru")
                .build();

        User user1Entity = User.builder()
                .id(1L)
                .name("test name")
                .email("test@test.ru")
                .build();
        User user2Entity = User.builder()
                .id(2L)
                .name("test name 2")
                .email("test2@test.ru")
                .build();

        users = List.of(user1Entity, user2Entity);
        List<UserDto> userDtos = List.of(user1, user2);
    }

    @Test
    void create() {
        UserDto userDto = UserDto.builder()
                .name("name")
                .email("user@email.com")
                .build();

        User user = User.builder()
                .id(1L)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        when(userRepository.save(any()))
                .thenReturn(user);

        userDto = userService.add(userDto);
        assertThat(userDto, is(notNullValue()));
    }

    @Test
    void update() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("name updated")
                .email("userUpdated@email.com")
                .build();

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("user@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        when(userRepository.save(any()))
                .thenReturn(user);

        userDto = userService.update(1L, userDto);

        assertThat(userDto, is(notNullValue()));
    }

    @Test
    void throwUserNotFoundException() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("name updated")
                .email("userUpdated@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        final InvalidInputException updateException = Assertions.assertThrows(
                InvalidInputException.class,
                () -> userService.update(1L, userDto)
        );

        final NoSuchIdException getException = Assertions.assertThrows(
                NoSuchIdException.class,
                () -> userService.getById(1L)
        );

        assertThat(updateException.getMessage(), is("User with id 1 not found"));
        assertThat(getException.getMessage(), is("User with id 1 not found"));
    }

    @Test
    void getUser() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("user@test.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.getById(1L);

        assertThat(userDto, is(notNullValue()));
    }

    @Test
    void delete() {
        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAll() {
        when(userRepository.findAll())
                .thenReturn(users);

        List<UserDto> userDtos = userService.getAll();

        assertThat(userDtos, is(notNullValue()));
    }
}
