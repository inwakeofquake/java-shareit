package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ItemStorageImpl implements ItemStorageInterface {
    private final Map<Long, Item> itemStorage = new ConcurrentHashMap<>();
    private long generatorId = 0;

    private long getGeneratedId() {
        return ++generatorId;
    }

    @Override
    public Item add(Item item) {
        item.setId(getGeneratedId());
        return itemStorage.put(item.getId(), item);
    }

    @Override
    public Item get(Long id) {
        return itemStorage.get(id);
    }

    @Override
    public Item update(Long id, Item item) {
        Item storageItem = itemStorage.get(id);
        if (item.getDescription() != null)
            storageItem.setDescription(item.getDescription());
        if (item.getName() != null)
            storageItem.setName(item.getName());
        if (item.getAvailable() != null)
            storageItem.setAvailable(item.getAvailable());
        return itemStorage.get(id);
    }

    @Override
    public Item delete(Long id) {
        return itemStorage.remove(id);
    }

    @Override
    public Collection<Item> values() {
        return itemStorage.values();
    }
}

