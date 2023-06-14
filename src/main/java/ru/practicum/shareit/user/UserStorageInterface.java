package ru.practicum.shareit.user;

import java.util.ArrayList;

public interface UserStorageInterface {

    User add(User user);

    User update(Long id, User user);

    User get(Long id);

    ArrayList<User> getAll();

    boolean hasId(long id);

    User getUserByEmail(String email);

    void delete(Long id);
}
