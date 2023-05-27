package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidInputException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserServiceInterface {

    private final UserStorageInterface userStorage;

    @Override
    public UserDto add(UserDto userDto) {
        if (userStorage.getUserByEmail(userDto.getEmail()) != null) {
            throw new InvalidInputException("User with email " + userDto.getEmail() + " already exists");
        }
        User user = UserMapper.toUser(userDto);
        userStorage.add(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(Long id) {
        User user = userStorage.get(id);
        if (user == null) {
            throw new InvalidInputException("User with id " + id + " not found");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(Long id, UserDto user) {
        User sourceUser = userStorage.get(id);
        if (user == null) {
            throw new InvalidInputException("User with id " + id + " not found");
        }
        User userByEmail = userStorage.getUserByEmail(user.getEmail());
        if ((userByEmail != null) && (userByEmail.getId() != id)) {
            throw new InvalidInputException("User with email " + user.getEmail() + " already exists");
        }
        userStorage.update(id, UserMapper.toUser(user));
        User updatedUser = userStorage.get(id);
        log.info("Обновлен пользователь c {} на {}", sourceUser, updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }


}
