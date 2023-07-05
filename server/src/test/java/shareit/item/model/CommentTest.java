package shareit.item.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.model.Comment;

@ContextConfiguration(classes = ShareItServer.class)
class CommentTest {

    private Comment comment1;
    private Comment comment2;
    private Comment comment3;

    @BeforeEach
    void setUp() {
        comment1 = Comment.builder()
                .id(1L)
                .build();

        comment2 = Comment.builder()
                .id(1L)
                .build();

        comment3 = Comment.builder()
                .id(null)
                .build();
    }

    @Test
    void testEquals() {
        Assertions.assertEquals(comment1, comment2);
        Assertions.assertEquals(comment1, comment1);
        Assertions.assertNotEquals(comment1, comment3);
        Assertions.assertNotEquals(comment3, comment1);
    }

    @Test
    void testHashCode() {
        Assertions.assertEquals(comment1.hashCode(), comment2.hashCode());
    }
}
