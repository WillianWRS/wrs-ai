package wrs.ai.service;

import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import wrs.ai.dto.ChatRequest;
import wrs.ai.dto.ChatResponse;

@Service
public class ChatService {

	private final ChatClient chatClient;

	public ChatService(@Qualifier("chatClient") ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	public Mono<ChatResponse> chat(ChatRequest request) {
		return Mono.fromCallable(() -> chatClient.prompt()
				.advisors(AdvisorParams.toolCallingAdvisorAutoRegister(false))
				.user(request.message())
				.call()
				.content())
				.map(ChatResponse::new)
				.subscribeOn(Schedulers.boundedElastic());
	}

}
