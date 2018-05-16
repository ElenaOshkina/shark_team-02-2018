package park.sharkteam;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import park.sharkteam.websocket.GameSocketHandler;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(new Class[]{WebSocketConfig.class, Main.class}, args);
    }

    @Bean
    public WebSocketHandler gameSocketHandler() {
        return new PerConnectionWebSocketHandler(GameSocketHandler.class);
    }
}
