package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NoSuchIdException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchIdException("User not found"));
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    public List<ItemRequestDto> getOwnRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchIdException("User not found"));
        return itemRequestRepository.findByRequestorOrderByCreatedDesc(user)
                .stream().map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }
//комментарий ради комита :(
    public List<ItemRequestDto> getAllRequests(Long userId,
                                               Pageable pageable) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new NoSuchIdException("User not found");
        }
        return itemRequestRepository.findAllByOrderByCreatedDesc(pageable)
                .stream()
                .filter(ir -> !Objects.equals(ir.getRequestor().getId(), userId))
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    public ItemRequestDto getRequest(Long requestId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchIdException("User not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchIdException("Not found"));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }
}

