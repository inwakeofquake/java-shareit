package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utility.NoSuchIdException;
import ru.practicum.shareit.utility.UnauthorizedAccessException;
import ru.practicum.shareit.utility.UnsupportedStateException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Item add(@Valid ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchIdException("User not found"));
        itemDto.setOwner(UserMapper.toUserDto(user));
        Item item = ItemMapper.toItem(itemDto);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NoSuchIdException("Item request not found"));
            item.setRequest(itemRequest);
        }
        itemRepository.save(item);
        log.info("Item {} successfully added", itemDto.getName());
        return item;
    }

    @Override
    public Item update(Long id, ItemDto itemDto, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        if (!item.getOwner().getId().equals(userId)) {
            log.warn("Unauthorized attempt to update item");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not the owner of the item");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        log.info("Item {} successfully updated", itemDto.getName());
        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto get(Long id, Long userId) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        ItemDto itemDto;
        if (item.getOwner().getId().equals(userId)) {
            itemDto = setLastNextBookings(item);
        } else
            itemDto = ItemMapper.toItemDto(item);

        return addCommentsToItem(itemDto);
    }

    private ItemDto addCommentsToItem(ItemDto itemDto) {
        itemDto.setComments(commentRepository.findByItem_Id(itemDto.getId())
                .stream().map(CommentMapper.INSTANCE::toCommentDto).collect(Collectors.toList()));
        return itemDto;
    }

    private ItemDto setLastNextBookings(Item item) {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        itemDto.setLastBooking(BookingMapper.toBookingDto(
                bookingRepository.findLastBooking(
                        item,
                        LocalDateTime.now(),
                        BookingStatus.APPROVED,
                        PageRequest.of(0, 1)).getContent().stream().findFirst().orElse(null)));

        itemDto.setNextBooking(BookingMapper.toBookingDto(
                bookingRepository.findNextBooking(
                        item,
                        LocalDateTime.now(),
                        BookingStatus.APPROVED,
                        PageRequest.of(0, 1)).getContent().stream().findFirst().orElse(null)));

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAll(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchIdException("User not found"));
        List<Item> items = itemRepository.findByOwner(user, Sort.by("id").ascending());
        return items.stream()
                .map(this::setLastNextBookings)
                .map(this::addCommentsToItem)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text);
    }

    @Override
    public void delete(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchIdException("Item with id " + id + " not found."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchIdException("User not found"));
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("User with id " + userId + " is not allowed to delete this item.");
        }
        itemRepository.delete(item);
    }

    @Override
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long userId) {
        if (commentDto.getText().isBlank()) {
            throw new UnsupportedStateException("Blank comment not allowed");
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchIdException("Item not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchIdException("User not found"));
        Sort sort = Sort.by(Sort.Direction.DESC, "end");
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sort);
        if (bookings.isEmpty()) {
            throw new UnsupportedStateException("User has not rented this item or the rental period has not ended yet");
        }
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText(commentDto.getText());
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.INSTANCE.toCommentDto(commentRepository.save(comment));
    }
}

