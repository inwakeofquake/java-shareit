package shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;

@SpringBootTest
@ContextConfiguration(classes = ShareItServer.class)
class ShareItTests {

    @Test
    void contextLoads() {
    }

}
