package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto add(UserDto userDto);

    UserDto getById(Long id);

    List<UserDto> getAll();

    UserDto update(Long id, UserDto user);

    void delete(Long id);
}
