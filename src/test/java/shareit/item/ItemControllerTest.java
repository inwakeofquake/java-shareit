package shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utility.Constants.HEADER_USER_ID;

@WebMvcTest(controllers = ItemController.class)
@ContextConfiguration(classes = ShareItApp.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemServiceImpl itemService;

    @MockBean
    private CommentRepository commentRepository;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDtoCreateTest;

    private ItemDto itemDtoCreated;

    private ItemDto itemDtoUpdateTest;

    private ItemDto itemDtoUpdated;

    private CommentDto commentDtoCreateTest;

    private CommentDto commentDtoCreated;

    @BeforeEach
    void setUp() {
        itemDtoCreateTest = ItemDto.builder()
                .name("nameCreate")
                .description("create description")
                .available(true)
                .owner(UserDto.builder().build())
                .build();

        itemDtoCreated = ItemDto.builder()
                .id(1L)
                .name("nameCreate")
                .description("create description")
                .available(true)
                .owner(UserDto.builder().build())
                .build();

        itemDtoUpdateTest = ItemDto.builder()
                .description("update description")
                .build();

        itemDtoUpdated = ItemDto.builder()
                .id(1L)
                .name("nameCreate")
                .description("update description")
                .available(true)
                .owner(UserDto.builder().build())
                .build();

        commentDtoCreateTest = CommentDto.builder()
                .text("comment")
                .build();

        commentDtoCreated = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("nameCreate")
                .created(LocalDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
        itemDtoCreateTest = null;
        itemDtoCreated = null;
        itemDtoUpdateTest = null;
        itemDtoUpdated = null;
    }

    @Test
    void add() throws Exception {

        Item itemMock = new Item();
        itemMock.setId(1L);
        itemMock.setName("Test item");
        itemMock.setDescription("Test description");
        itemMock.setAvailable(true);
        itemMock.setOwner(new User());

        when(itemService.add(any(ItemDto.class), anyLong())).thenReturn(itemMock);

        ItemDto itemDtoMock = ItemMapper.toItemDto(itemMock);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoMock))
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDtoMock.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemDtoMock.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoMock.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoMock.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(itemDtoMock.getOwner().getId())))
                .andExpect(jsonPath("$.owner.name", is(itemDtoMock.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(itemDtoMock.getOwner().getEmail())));
    }

    @Test
    void update() throws Exception {

        Item itemMock = new Item();
        itemMock.setId(1L);
        itemMock.setName("Updated test item");
        itemMock.setDescription("Updated test description");
        itemMock.setAvailable(true);
        itemMock.setOwner(new User());

        when(itemService.update(anyLong(), any(ItemDto.class), anyLong())).thenReturn(itemMock);

        ItemDto itemDtoMock = ItemMapper.toItemDto(itemMock);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDtoMock))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoMock.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemDtoMock.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoMock.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoMock.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(itemDtoMock.getOwner().getId())))
                .andExpect(jsonPath("$.owner.name", is(itemDtoMock.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(itemDtoMock.getOwner().getEmail())));
    }

    @Test
    void getItemDto() throws Exception {
        when(itemService.get(1L, 1L))
                .thenReturn(itemDtoUpdated);

        mvc.perform(get("/items/1")
                        .header(HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoUpdated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoUpdated.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoUpdated.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoUpdated.getAvailable())));
    }

    @Test
    void getAll() throws Exception {
        when(itemService.getAll(1L))
                .thenReturn(List.of(itemDtoUpdated));

        mvc.perform(get("/items/")
                        .header(HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoUpdated.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoUpdated.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoUpdated.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoUpdated.getAvailable())));
    }

    @Test
    void search() throws Exception {

        Item itemMock = new Item();
        itemMock.setId(1L);
        itemMock.setName("name");
        itemMock.setDescription("description");
        itemMock.setAvailable(true);

        when(itemService.search("update")).thenReturn(Collections.singletonList(itemMock));

        ItemDto itemDtoMock = ItemMapper.toItemDto(itemMock);

        mvc.perform(get("/items/search?text=update")
                        .header(HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoMock.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(itemDtoMock.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoMock.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoMock.getAvailable())))
                .andExpect(jsonPath("$[0].owner", is(itemDtoMock.getOwner())));
    }

    @Test
    void deleteItem() throws Exception {
        doNothing().when(itemService).delete(1L, 1L);

        mvc.perform(delete("/items/1")
                        .header(HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void addCommentToItem() throws Exception {
        when(itemService.addComment(1L, commentDtoCreateTest, 1L))
                .thenReturn(commentDtoCreated);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDtoCreateTest))
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoCreated.getId().intValue())))
                .andExpect(jsonPath("$.text", is(commentDtoCreated.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoCreated.getAuthorName())))
                .andExpect(jsonPath("$.created",
                        is(commentDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }

    @Test
    void getCommentsForItem() throws Exception {
        Comment comment = Comment.builder()
                .id(1L)
                .text("comment")
                .author(User.builder().id(1L).name("nameCreate").build())
                .created(LocalDateTime.now())
                .build();

        when(commentRepository.findByItem_Id(1L))
                .thenReturn(List.of(comment));

        mvc.perform(get("/items/1/comments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(comment.getId().intValue())))
                .andExpect(jsonPath("$[0].text", is(comment.getText())))
                .andExpect(jsonPath("$[0].author.name", is(comment.getAuthor().getName())));
    }
}