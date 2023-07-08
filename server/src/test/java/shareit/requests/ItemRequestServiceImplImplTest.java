package shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utility.NoSuchIdException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplImplTest {

    @InjectMocks
    ItemRequestServiceImpl itemRequestServiceImpl;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    User user;
    ItemRequestDto requestDto;
    ItemRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        request = new ItemRequest();
        request.setId(1L);
        request.setRequestor(user);
        requestDto = ItemRequestMapper.toItemRequestDto(request);
    }

    @Test
    void testCreateRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto result = itemRequestServiceImpl.createRequest(requestDto, user.getId());

        assertEquals(requestDto.getId(), result.getId());
        assertEquals(requestDto.getDescription(), result.getDescription());
    }

    @Test
    void testCreateRequestUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchIdException.class, () -> {
            itemRequestServiceImpl.createRequest(requestDto, user.getId());
        });
    }

    @Test
    void testGetOwnRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorOrderByCreatedDesc(any(User.class))).thenReturn(Collections.singletonList(request));

        List<ItemRequestDto> results = itemRequestServiceImpl.getOwnRequests(user.getId());

        assertEquals(1, results.size());
        assertEquals(requestDto.getId(), results.get(0).getId());
        assertEquals(requestDto.getDescription(), results.get(0).getDescription());
    }

    @Test
    void testGetAllRequests() {
        User differentUser = User.builder().id(2L).name("Jane").email("jane@example.com").build();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L); // Assign an ID to itemRequest
        itemRequest.setRequestor(differentUser);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(Collections.singletonList(itemRequest));

        List<ItemRequestDto> results = itemRequestServiceImpl.getAllRequests(user.getId(), PageRequest.of(0, 10));

        assertEquals(1, results.size());
        assertEquals(itemRequest.getId(), results.get(0).getId());
        // Assuming requestDto is of type ItemRequestDto and has a description field
        assertEquals(requestDto.getDescription(), results.get(0).getDescription());
    }

    @Test
    void testGetRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));

        ItemRequestDto result = itemRequestServiceImpl.getRequest(request.getId(), user.getId());

        assertEquals(requestDto.getId(), result.getId());
        assertEquals(requestDto.getDescription(), result.getDescription());
    }

    @Test
    void testGetRequestUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchIdException.class, () -> {
            itemRequestServiceImpl.getRequest(request.getId(), user.getId());
        });
    }

    @Test
    void testGetRequestItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchIdException.class, () -> {
            itemRequestServiceImpl.getRequest(request.getId(), user.getId());
        });
    }

    @Test
    void testGetOwnRequestsUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchIdException.class, () -> {
            itemRequestServiceImpl.getOwnRequests(user.getId());
        });
    }

    @Test
    void testGetAllRequestsUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchIdException.class, () -> {
            itemRequestServiceImpl.getAllRequests(user.getId(), PageRequest.of(0, 10));
        });
    }

}

