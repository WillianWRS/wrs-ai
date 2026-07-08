package wrs.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import wrs.ai.dto.ChatRequest;
import wrs.ai.dto.ChatResponse;

@Service
@ConditionalOnBean(ChatClient.class)
public class ChatService {

	private final ChatClient chatClient;

	public ChatService(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	public Mono<ChatResponse> chat(ChatRequest request) {
		return Mono.fromCallable(() -> chatClient.prompt()
				.user(request.message())
				.call()
				.content())
				.map(ChatResponse::new)
				.subscribeOn(Schedulers.boundedElastic());
	}

}
