package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemServiceInterface itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestBody @Valid ItemDto itemDto,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Adding item: {}", itemDto.getName());
        Item addedItem = itemService.add(itemDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ItemMapper.toItemDto(addedItem));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> editItem(@PathVariable Long itemId,
                                            @RequestBody ItemDto itemDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Updating item {}", itemDto.getName());
        Item updatedItem = itemService.update(itemId, itemDto, userId);
        return ResponseEntity.ok(ItemMapper.toItemDto(updatedItem));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId) {
        Item item = itemService.get(itemId);
        log.info("Getting item {}", item.getName());
        return ResponseEntity.ok(ItemMapper.toItemDto(item));
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting all items shared by user with ID {}", userId);
        return itemService.getAll(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Searching for items filtered by text {}", text);
        return itemService.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}

