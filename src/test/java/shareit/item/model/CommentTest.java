package shareit.item.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.model.Comment;

@ContextConfiguration(classes = ShareItApp.class)
class CommentTest {

    @Test
    void testEquals() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .build();

        Comment comment2 = Comment.builder()
                .id(1L)
                .build();

        Comment comment3 = Comment.builder()
                .id(null)
                .build();

        Assertions.assertEquals(comment1, comment2);
        Assertions.assertEquals(comment1, comment1);
        Assertions.assertNotEquals(1L, comment1);
        Assertions.assertNotEquals(comment1, comment3);
        Assertions.assertNotEquals(comment3, comment1);
    }

    @Test
    void testHashCode() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .build();

        Comment comment2 = Comment.builder()
                .id(1L)
                .build();

        Assertions.assertEquals(comment1.hashCode(), comment2.hashCode());
    }


}