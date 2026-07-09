package wrs.ai.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import wrs.ai.dto.ChatRequest;
import wrs.ai.dto.ChatResponse;
import wrs.ai.service.ChatService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

	private final ChatService chatService;

	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	@PostMapping
	public Mono<ChatResponse> chat(@RequestBody ChatRequest request) {
		return chatService.chat(request);
	}

}
