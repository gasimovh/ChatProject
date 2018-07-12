package ChatProject;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class SocketHandler extends TextWebSocketHandler {

    private final ChannelRepository channelRepository;

    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

        Map<String, String> value = new Gson().fromJson(message.getPayload(), Map.class);
        Message m = new Message();
        m.setAcountId(value.get("account_id"));
        m.setContent(value.get("content"));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime dateOfCreation = LocalDateTime.now();
        m.setDateOfCreation(dtf.format(dateOfCreation));
        m.setParent(channelRepository.findByName(value.get("channel_name")));
        //channelRepository.findByName(value.get("channel_name")).addMessage(m);
        //channelRepository.flush();

        System.out.println(message.getPayload());

		for(WebSocketSession webSocketSession : sessions) {
			webSocketSession.sendMessage(new TextMessage(value.get("content")));
		}
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }
}
