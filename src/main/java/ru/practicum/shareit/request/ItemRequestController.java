package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final static String header = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                        @RequestHeader(header) Long userId) {
        return itemRequestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader(header) Long userId) {
        return itemRequestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(header) Long userId,
                                               @RequestParam(value = "from", defaultValue = "0") int from,
                                               @RequestParam(value = "size", defaultValue = "1000") int size) {
        if ((from < 0) || (size == 0)) {
            throw new BadRequestException("Bad page params");
        }

        return itemRequestService.getAllRequests(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable Long requestId,
                                     @RequestHeader(header) Long userId) {
        return itemRequestService.getRequest(requestId, userId);
    }
}
