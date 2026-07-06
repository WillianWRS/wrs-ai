package wrs.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import wrs.ai.dto.ChatRequest;
import wrs.ai.dto.ChatResponse;

@RestController
@RequestMapping("/api/rag")
@ConditionalOnBean(name = "ragChatClient")
public class RagController {

	private final ChatClient ragChatClient;

	public RagController(@Qualifier("ragChatClient") ChatClient ragChatClient) {
		this.ragChatClient = ragChatClient;
	}

	@PostMapping("/chat")
	public Mono<ChatResponse> chat(@RequestBody ChatRequest request) {
		return Mono.fromCallable(() -> ragChatClient.prompt()
				.user(request.message())
				.call()
				.content())
				.map(ChatResponse::new)
				.subscribeOn(Schedulers.boundedElastic());
	}

}
