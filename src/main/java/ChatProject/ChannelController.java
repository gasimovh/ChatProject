package ChatProject;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RequiredArgsConstructor
@RestController
public class ChannelController {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    @PostMapping("/channel")
    public String createChannel(@RequestParam(value="name") String name,
                                @RequestParam(value="status") Status status){
        if(channelRepository.findByName(name) == null){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime dateOfCreation = LocalDateTime.now();
            channelRepository.save(new Channel(name, status, dtf.format(dateOfCreation)));
            return "Channel " + name + " created successfully!";
        }
        return "Channel already exists!";
    }

    @GetMapping("/channel")
    public List<Channel> listOfChannels(){
        return channelRepository.findAll();
    }

    @GetMapping("/channel/findbyname/{name}")
    public Channel findChannelByName(@PathVariable(value="name") String name){
        return channelRepository.findByName(name);
    }

    @GetMapping("/channel/findbyname/{name}/messages")
    public List<Message> listOfMessages(@PathVariable(value="name") String name){
        return channelRepository.findByName(name).getListOfMessages();
    }

    @PostMapping("/channel/{name}/message")
    public String addMessage(@PathVariable(value="name") String name,
                              @RequestParam(value="account_id") String account_id,
                              @RequestParam(value="content") String content){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime dateOfCreation = LocalDateTime.now();
        Message message = new Message(account_id, dtf.format(dateOfCreation), content, channelRepository.findByName(name));
        channelRepository.findByName(name).addMessage(message);
        messageRepository.save(message);
        return "Message saved!";
    }

    @PatchMapping("/channel/findbyname/{name}")
    public String updateChannelStatus(@PathVariable(value="name") String name, Status status){
        channelRepository.findByName(name).setStatus(status);
        channelRepository.flush();
        return "Channel status changed to " + status + "!";
    }
}
