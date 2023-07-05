package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utility.Constants.HEADER_USER_ID;

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CommentRepository commentRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto add(@RequestBody @Valid ItemDto itemDto,
                       @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Adding item: {}", itemDto.getName());
        Item addedItem = itemService.add(itemDto, userId);
        return ItemMapper.toItemDto(addedItem);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@PathVariable Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Updating item {}", itemDto.getName());
        Item updatedItem = itemService.update(itemId, itemDto, userId);
        return ItemMapper.toItemDto(updatedItem);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto get(@PathVariable Long itemId,
                       @RequestHeader(value = HEADER_USER_ID, required = false, defaultValue = "-1") Long userId) {
        ItemDto item = itemService.get(itemId, userId);
        log.info("Getting item {}", item.getName());
        return item;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAll(@RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Getting all items shared by user with ID {}", userId);
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Searching for items filtered by text {}", text);
        return itemService.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Deleting item with id : {}", id);
        itemService.delete(id, userId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto addCommentToItem(@RequestHeader(HEADER_USER_ID) Long userId,
                                       @PathVariable Long itemId,
                                       @RequestBody CommentDto comment) {
        log.info("Adding comment to item with ID: {} by user ID: {}", itemId, userId);
        return itemService.addComment(itemId, comment, userId);
    }

    @GetMapping("/{itemId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<Comment> getCommentsForItem(@PathVariable Long itemId) {
        log.info("Getting comments for item with ID: {}", itemId);
        return commentRepository.findByItem_Id(itemId);
    }
}