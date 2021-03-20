package com.websockets.demo.websockets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebSocketsApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebSocketsApplication.class, args);
	}

}
