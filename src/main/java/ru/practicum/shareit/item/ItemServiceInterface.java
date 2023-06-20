package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

public interface ItemServiceInterface {
    Item add(@Valid ItemDto itemDto, Long userId);

    Item update(Long id, ItemDto itemDto, Long userId);

    ItemDto get(Long id, Long userId);

    List<ItemDto> getAll(Long userId);

    List<Item> search(String text);

    void delete(Long id, Long userId);

    CommentDto addComment(Long itemId, CommentDto comment, Long userId);
}
