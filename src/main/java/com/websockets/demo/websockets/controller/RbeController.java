package com.websockets.demo.websockets.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/rbe")
public class RbeController {

    private final ExecutorService executor
            = Executors.newSingleThreadExecutor();

    @GetMapping("/response")
    public ResponseEntity<ResponseBodyEmitter> getRbeResponse() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        executor.execute(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    String message = " Message object. ";
                    randomDelay();
                    emitter.send(i + message);
                }
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return new ResponseEntity<ResponseBodyEmitter>(emitter, HttpStatus.OK);
    }

    private void randomDelay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}