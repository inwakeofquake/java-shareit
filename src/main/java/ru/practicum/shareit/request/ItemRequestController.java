package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utility.BadRequestException;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.utility.Constants.HEADER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final ItemRequestServiceImpl itemRequestServiceImpl;

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                        @RequestHeader(HEADER_USER_ID) Long userId) {
        return itemRequestServiceImpl.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader(HEADER_USER_ID) Long userId) {
        return itemRequestServiceImpl.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(HEADER_USER_ID) Long userId,
                                               @RequestParam(value = "from", defaultValue = "0") int from,
                                               @RequestParam(value = "size", defaultValue = "1000") int size) {
        if ((from < 0) || (size == 0)) {
            throw new BadRequestException("Bad page params");
        }

        return itemRequestServiceImpl.getAllRequests(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable Long requestId,
                                     @RequestHeader(HEADER_USER_ID) Long userId) {
        return itemRequestServiceImpl.getRequest(requestId, userId);
    }
}
