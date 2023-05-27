package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserDto> addUser(@RequestBody @Valid UserDto userDto) {
        log.info("Adding user {}", userDto.getName());
        UserDto addedUser = userService.add(userDto);
        log.info("User {} has been added", addedUser.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(addedUser);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        log.info("Fetching user with id {}", userId);
        UserDto user = userService.getById(userId);
        log.info("Fetched user with id {}", userId);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@PathVariable Long userId, @RequestBody UserDto user) {
        log.info("Updating user: {}", user);
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(userId, user));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Fetching all users");
        List<UserDto> users = userService.getAll();
        log.info("Fetched all users");
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Deleting user with id : {}", id);
        userService.delete(id);
    }

}

