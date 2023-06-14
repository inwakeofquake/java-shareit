package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NoSuchIdException;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserStorageImpl implements UserStorageInterface {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private long generatorId = 0;

    private long getGeneratedId() {
        return ++generatorId;
    }

    @Override
    public User add(User user) {
        user.setId(getGeneratedId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long id, User user) {
        if (get(id) == null) {
            throw new NoSuchIdException("No such user ID");
        }
        User storageUser = users.get(id);
        if (user.getEmail() != null)
            storageUser.setEmail(user.getEmail());
        if (user.getName() != null)
            storageUser.setName(user.getName());
        return storageUser;
    }

    @Override
    public User get(Long id) {
        return users.get(id);
    }

    @Override
    public ArrayList<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean hasId(long id) {
        return users.containsKey(id);
    }

    @Override
    public User getUserByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email))
                return user;
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }
}

