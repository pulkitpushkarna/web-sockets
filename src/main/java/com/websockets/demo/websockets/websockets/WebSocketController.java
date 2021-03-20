package com.websockets.demo.websockets.websockets;

import com.websockets.demo.websockets.domain.Message;
import com.websockets.demo.websockets.service.SessionsStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.SortedSet;
import java.util.TreeSet;

@Controller
public class WebSocketController {
    @Autowired
    private SessionsStorage sessionsStorage;
    @Autowired
    private SimpMessagingTemplate template;

    @GetMapping("websocket/user")
    public ModelAndView getClientUser(ModelAndView modelAndView){
        modelAndView.addObject("defaultUser", sessionsStorage.getRandomUserName());
        modelAndView.setViewName("websocketClientSpecificUser");
        return modelAndView;
    }

    @MessageMapping("/websocket/message")
    public void message(Message message) {
        String sessionId=sessionsStorage.getSessionId(message.getToUser());
        message.setLocalDateTime(LocalDateTime.now());
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        template.convertAndSendToUser(sessionId,"/topic/message", message ,headerAccessor.getMessageHeaders());
    }

    @GetMapping("/websocket/client")
    public ModelAndView getClient(ModelAndView modelAndView){
        modelAndView.setViewName("websocketClient");
        return modelAndView;
    }

    @MessageMapping("/news")
    @SendTo("/topic/news")
    public @ResponseBody
    String broadcastNews(@Payload String message) {
        return message;
    }

    @GetMapping("websocket/users")
    public @ResponseBody
    ResponseEntity<SortedSet<String>> getOnlineUsers(){
        SortedSet<String> users = new TreeSet(sessionsStorage.getAllSessionIds().keySet());
        return ResponseEntity.ok()
                .body(users);
    }
}
