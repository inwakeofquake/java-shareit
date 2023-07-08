package shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemRequestMapperTest {

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto(1L, "Test User", "testuser@email.com");
        ItemDto itemDto = new ItemDto(1L, "item1", "description", true,
                userDto, null, null, null, null);
        List<ItemDto> itemDtos = List.of(itemDto);
        itemRequest = new ItemRequest(1L, "description",
                new User(1L, "Test User", "testuser@email.com"), LocalDateTime.now(),
                itemDtos.stream().map(ItemMapper::toItem).collect(Collectors.toList()));
        itemRequestDto = new ItemRequestDto(1L, "description",
                UserMapper.toUser(userDto), LocalDateTime.now(), itemDtos);
    }

    @Test
    void toItemRequestDtoReturnsExpectedDto() {
        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(itemRequest);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getCreated(), result.getCreated());
        assertEquals(itemRequest.getItems().size(), result.getItems().size());
        assertEquals(itemRequest.getItems().get(0).getId(), result.getItems().get(0).getId());
    }

    @Test
    void toItemRequestReturnsExpectedRequest() {
        ItemRequest result = ItemRequestMapper.toItemRequest(itemRequestDto);

        assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(itemRequestDto.getCreated(), result.getCreated());
        assertEquals(itemRequestDto.getItems().size(), result.getItems().size());
        assertEquals(itemRequestDto.getItems().get(0).getId(), result.getItems().get(0).getId());
    }
}