package com.websockets.demo.websockets.controller;

import com.websockets.demo.websockets.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/sse")
public class SseController {


    @Autowired
    SseService sseService;

    private final ExecutorService executor
            = Executors.newSingleThreadExecutor();

    @GetMapping("/response")
    public ResponseEntity<SseEmitter> getSseResponse() {
        SseEmitter emitter = new SseEmitter();
        executor.execute(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    randomDelay();
                    emitter.send("/sse" + " @ " + new Date()
                            , MediaType.APPLICATION_JSON);
                }
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return new ResponseEntity(emitter, HttpStatus.OK);
    }

    private void randomDelay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }



    @Scheduled(cron = "*/5 * * ? * *")
    public void scheduleTask() throws IOException {
        sseService.process("Scheduled job run", "Job");
    }


    @GetMapping("/message")
    @ResponseBody
    public void sendMessages(@RequestParam String message,
                      @RequestParam(required = false) String user) throws IOException {
        sseService.process(message, user);
    }


    @GetMapping("/receive")
    public @ResponseBody
    SseEmitter getEmitter() {
        return sseService.registerClient();
    }


    @GetMapping("/client")
    public ModelAndView getClient(ModelAndView modelAndView) {
        modelAndView.setViewName("sseClient.html");
        return modelAndView;
    }
}
