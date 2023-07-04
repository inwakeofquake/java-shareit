package shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;

@SpringBootTest
@ContextConfiguration(classes = ShareItApp.class)
class ShareItTests {

    @Test
    void contextLoads() {
    }

}
