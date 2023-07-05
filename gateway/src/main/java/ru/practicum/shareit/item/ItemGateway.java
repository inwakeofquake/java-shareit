package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utility.Constants.HEADER_USER_ID;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemGateway {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER_ID) Long userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(HEADER_USER_ID) Long userId,
                                         @PathVariable Long itemId,
                                         @RequestBody ItemDto itemDto) {
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader(HEADER_USER_ID) Long userId,
                                      @PathVariable Long itemId) {
        return itemClient.get(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader(HEADER_USER_ID) Long userId,
                                      @PositiveOrZero @RequestParam(defaultValue = "0") Long from,
                                      @Positive @RequestParam(defaultValue = "10") Long size) {
        return itemClient.get(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(HEADER_USER_ID) Long userId,
                                         @RequestParam String text,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Long from,
                                         @Positive @RequestParam(defaultValue = "10") Long size
    ) {
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> comment(@RequestHeader(HEADER_USER_ID) Long userId,
                                          @PathVariable Long itemId,
                                          @Valid @RequestBody CommentDto commentDto
    ) {
        return itemClient.comment(userId, itemId, commentDto);
    }

}