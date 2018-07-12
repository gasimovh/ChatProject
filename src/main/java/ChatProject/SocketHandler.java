package ChatProject;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class SocketHandler extends TextWebSocketHandler {

    private final ChannelRepository channelRepository;

    private final MessageRepository messageRepository;

    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

        Map<String, String> value = new Gson().fromJson(message.getPayload(), Map.class);

        new ChannelController(channelRepository, messageRepository)
                .addMessage(value.get("channel_name"), value.get("account_id"), value.get("content"));

		for(WebSocketSession webSocketSession : sessions) {
			webSocketSession.sendMessage(new TextMessage(value.get("content")));
		}
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }
}
