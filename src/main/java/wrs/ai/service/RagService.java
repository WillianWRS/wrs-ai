package wrs.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import wrs.ai.dto.ChatRequest;
import wrs.ai.dto.ChatResponse;

@Service
@ConditionalOnBean(name = "ragChatClient")
public class RagService {

	private final ChatClient ragChatClient;

	public RagService(@Qualifier("ragChatClient") ChatClient ragChatClient) {
		this.ragChatClient = ragChatClient;
	}

	public Mono<ChatResponse> chat(ChatRequest request) {
		return Mono.fromCallable(() -> ragChatClient.prompt()
				.user(request.message())
				.call()
				.content())
				.map(ChatResponse::new)
				.subscribeOn(Schedulers.boundedElastic());
	}

}
