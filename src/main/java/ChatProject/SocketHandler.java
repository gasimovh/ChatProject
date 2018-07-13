package ChatProject;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SocketHandler extends TextWebSocketHandler {

    private final ChannelRepository channelRepository;

    private final MessageRepository messageRepository;

    private Map<String, List<WebSocketSession>> sessions = new HashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

        Map<String, String> value = new Gson().fromJson(message.getPayload(), Map.class);

        String channelName = (String) session.getAttributes().get("channel_name");

        new ChannelController(channelRepository, messageRepository)
                .addMessage(channelName, value.get("account_id"), value.get("content"));

		for(WebSocketSession webSocketSession : sessions.get(value.get("channel_name"))) {
		    webSocketSession.sendMessage(new TextMessage(value.get("content")));
		}
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception, IOException{

        if(sessions.containsKey(session.getAttributes().get("channel_name"))){
            sessions.get(session.getAttributes().get("channel_name")).add(session);
        }
        else{
            List<WebSocketSession> sessionList = new ArrayList<>();
            sessionList.add(session);
            sessions.put((String) session.getAttributes().get("channel_name"), sessionList);
        }
    }
}
