package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorageInterface {

    Item add(Item item);

    Item get(Long id);

    Item update(Long id, Item item);

    Item delete(Long id);

    Collection<Item> values();
}
