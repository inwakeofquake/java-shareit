package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utility.InvalidInputException;
import ru.practicum.shareit.utility.NoSuchIdException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        repository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NoSuchIdException("User with id " + id + " not found"));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User sourceUser = repository.findById(id)
                .orElseThrow(() -> new InvalidInputException("User with id " + id + " not found"));
        Optional<User> userByEmail = repository.findByEmail(userDto.getEmail());
        if ((userByEmail.isPresent()) && (!Objects.equals(userByEmail.get().getId(), id))) {
            throw new InvalidInputException("User with email " + userDto.getEmail() + " already exists");
        }
        if (userDto.getName() != null) {
            sourceUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            sourceUser.setEmail(userDto.getEmail());
        }
        User updatedUser = repository.findById(id)
                .orElseThrow(() -> new InvalidInputException("User with id " + id + " not found"));
        log.info("User with ID {} has been updated and has ID {}", sourceUser, updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

