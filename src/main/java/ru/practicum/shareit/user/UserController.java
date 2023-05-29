package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserServiceInterface userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@RequestBody @Valid UserDto userDto) {
        log.info("Adding user {}", userDto.getName());
        UserDto addedUser = userService.add(userDto);
        log.info("User {} has been added", addedUser.getName());
        return addedUser;
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto get(@PathVariable Long userId) {
        log.info("Fetching user with id {}", userId);
        UserDto user = userService.getById(userId);
        log.info("Fetched user with id {}", userId);
        return user;
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto user) {
        log.info("Updating user: {}", user);
        return userService.update(userId, user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAll() {
        log.info("Fetching all users");
        List<UserDto> users = userService.getAll();
        log.info("Fetched all users");
        return users;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        log.info("Deleting user with id : {}", id);
        userService.delete(id);
    }
}