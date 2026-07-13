package wrs.ai.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import wrs.ai.dto.ChatRequest;
import wrs.ai.dto.ChatResponse;

@Service
public class ChatService {

	private final ChatModel chatModel;

	public ChatService(ChatModel chatModel) {
		this.chatModel = chatModel;
	}

	public Mono<ChatResponse> chat(ChatRequest request) {
		return Mono.fromCallable(() -> new ChatResponse(
				chatModel.call(new Prompt(request.message()))
						.getResult()
						.getOutput()
						.getText()))
				.subscribeOn(Schedulers.boundedElastic());
	}

}
