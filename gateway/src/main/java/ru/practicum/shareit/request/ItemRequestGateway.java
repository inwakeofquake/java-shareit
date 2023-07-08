package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static ru.practicum.shareit.utility.Constants.HEADER_USER_ID;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestGateway {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER_ID) Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader(HEADER_USER_ID) Long userId) {
        return itemRequestClient.get(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> get(@RequestHeader(HEADER_USER_ID) Long userId,
                                      @RequestParam(defaultValue = "0") Long from,
                                      @RequestParam(defaultValue = "10") Long size
    ) {
        return itemRequestClient.get(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@RequestHeader(HEADER_USER_ID) Long userId,
                                      @PathVariable Long requestId
    ) {
        return itemRequestClient.get(userId, requestId);
    }
}
