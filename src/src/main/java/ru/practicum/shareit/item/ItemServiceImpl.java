package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NoSuchIdException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorageInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemServiceInterface {

    @Autowired
    private UserStorageInterface userStorage;
    @Autowired
    private ItemStorageInterface itemStorage;

    @Override
    public Item add(ItemDto itemDto, Long userId) {
        User user = userStorage.get(userId);
        if (user == null) {
            throw new NoSuchIdException("User not found");
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        itemStorage.add(item);
        log.info("Item {} successfully added", itemDto.getName());
        return item;
    }

    @Override
    public Item update(Long id, ItemDto itemDto, Long userId) {
        Item item = itemStorage.get(id);
        if (item == null) {
            log.warn("Item not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
        if (!item.getOwner().getId().equals(userId)) {
            log.warn("Unauthorized attempt to update item");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not the owner of the item");
        }
        item = ItemMapper.toItem(itemDto);
        item.setOwner(userStorage.get(userId));
        log.info("Item {} successfully updated", itemDto.getName());
        return itemStorage.update(id, item);
    }

    @Override
    public Item get(Long id) {
        Item item = itemStorage.get(id);
        if (item == null) {
            log.warn("Item not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
        return item;
    }

    @Override
    public List<Item> getAll(Long userId) {
        return itemStorage.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        String lowerText = text.toLowerCase();
        return itemStorage.values().stream()
                .filter(item -> item.getAvailable()
                        && (item.getName().toLowerCase().contains(lowerText)
                        || item.getDescription().toLowerCase().contains(lowerText)))
                .collect(Collectors.toList());
    }
}

