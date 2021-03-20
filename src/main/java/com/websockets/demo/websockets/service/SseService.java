package com.websockets.demo.websockets.service;

import com.websockets.demo.websockets.domain.Notification;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter registerClient(){
        SseEmitter emitter = new SseEmitter();

        emitters.add(emitter);

        emitter.onCompletion(() -> this.emitters.remove(emitter));

        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(emitter);
        });
        return emitter;
    }


    public void process(String message, String user) throws IOException {
        Notification notification = Notification.builder()
                .user(StringUtils.isBlank(user) ? "Guest" : user)
                .message(message)
                .build();
        sendEventToClients(notification);
    }

    public void sendEventToClients(Notification notification) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        emitters.forEach(emitter -> {
            try {
                emitter.send(notification);
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.remove(deadEmitters);
    }



}
