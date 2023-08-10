package shareit.user.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.model.User;

@ContextConfiguration(classes = ShareItServer.class)
class UserTest {

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .build();

        user2 = User.builder()
                .id(1L)
                .build();

        user3 = User.builder()
                .id(null)
                .build();
    }

    @Test
    void testEquals() {
        Assertions.assertEquals(user1, user2);
        Assertions.assertEquals(user1, user1);
        Assertions.assertNotEquals(user1, user3);
        Assertions.assertNotEquals(user3, user1);
    }

    @Test
    void testHashCode() {
        Assertions.assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEqualsNameAndEmail() {
        Assertions.assertEquals(user1, user2);
    }

    @Test
    void testToString() {
        User user = User.builder().id(1L).name("John").email("john@test.com").build();
        String expectedString = "User(id=1, name=John, email=john@test.com)";
        Assertions.assertEquals(expectedString, user.toString());
    }

    @Test
    void testNotEqualsDifferentFieldValues() {
        User user1 = User.builder()
                .id(1L)
                .name("John")
                .email("john@test.com")
                .build();

        User user2 = User.builder()
                .id(1L)
                .name("Jane")
                .email("jane@test.com")
                .build();

        Assertions.assertNotEquals(user1, user2);
    }

    @Test
    void testHashCodeNameAndEmail() {
        Assertions.assertEquals(user1.hashCode(), user2.hashCode());
    }
}