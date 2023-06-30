package shareit.user.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.user.model.User;

@ContextConfiguration(classes = ShareItApp.class)
class UserTest {

    @Test
    void testEquals() {
        User user1 = User.builder()
                .id(1L)
                .build();

        User user2 = User.builder()
                .id(1L)
                .build();

        User user3 = User.builder()
                .id(null)
                .build();

        Assertions.assertEquals(user1, user2);
        Assertions.assertEquals(user1, user1);
        Assertions.assertNotEquals(user1, user3);
        Assertions.assertNotEquals(user3, user1);
    }

    @Test
    void testHashCode() {
        User user1 = User.builder()
                .id(1L)
                .build();

        User user2 = User.builder()
                .id(1L)
                .build();

        Assertions.assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEquals_NameAndEmail() {
        User user1 = User.builder()
                .id(1L)
                .name("John")
                .email("john@test.com")
                .build();

        User user2 = User.builder()
                .id(1L)
                .name("John")
                .email("john@test.com")
                .build();

        Assertions.assertEquals(user1, user2);
    }


    @Test
    void testToString() {
        User user = User.builder().id(1L).name("John").email("john@test.com").build();
        String expectedString = "User(id=1, name=John, email=john@test.com)";
        Assertions.assertEquals(expectedString, user.toString());
    }


    @Test
    void testNotEquals_DifferentFieldValues() {
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
    void testHashCode_NameAndEmail() {
        User user1 = User.builder()
                .id(1L)
                .name("John")
                .email("john@test.com")
                .build();

        User user2 = User.builder()
                .id(1L)
                .name("John")
                .email("john@test.com")
                .build();

        Assertions.assertEquals(user1.hashCode(), user2.hashCode());
    }
}