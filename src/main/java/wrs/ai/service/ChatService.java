package wrs.ai.service;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.genai.errors.ClientException;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import wrs.ai.dto.ChatRequest;
import wrs.ai.dto.ChatStreamEvent;

@Service
public class ChatService {

	private final ChatModel chatModel;

	public ChatService(ChatModel chatModel) {
		this.chatModel = chatModel;
	}

	public Flux<ServerSentEvent<ChatStreamEvent>> stream(ChatRequest request) {
		AtomicReference<String> previousText = new AtomicReference<>("");

		return Flux.defer(() -> chatModel.stream(new Prompt(request.message()))
				.mapNotNull(response -> extractDelta(previousText, response))
				.filter(StringUtils::hasText)
				.map(ChatStreamEvent::chunk)
				.concatWith(Flux.just(ChatStreamEvent.done()))
				.onErrorResume(RuntimeException.class, ex -> Flux.just(toErrorEvent(ex)))
				.map(event -> ServerSentEvent.builder(event)
						.event(event.type())
						.build()))
				.subscribeOn(Schedulers.boundedElastic());
	}

	private static String extractDelta(AtomicReference<String> previousText, ChatResponse response) {
		if (response.getResult() == null || response.getResult().getOutput() == null) {
			return null;
		}

		String current = response.getResult().getOutput().getText();
		if (!StringUtils.hasText(current)) {
			return null;
		}

		String previous = previousText.get();
		if (!StringUtils.hasText(previous) || !current.startsWith(previous)) {
			previousText.set(current);
			return current;
		}

		String delta = current.substring(previous.length());
		previousText.set(current);
		return StringUtils.hasText(delta) ? delta : null;
	}

	private static ChatStreamEvent toErrorEvent(RuntimeException exception) {
		ClientException clientException = findCause(exception, ClientException.class);
		if (clientException != null) {
			String message = clientException.getMessage();
			if (message != null && message.contains("429")) {
				return ChatStreamEvent.error(
						"Cota do Gemini esgotada para este projeto. Crie uma API key em outro projeto ou aguarde o reset.");
			}
			if (message != null && (message.contains("API key not valid") || message.contains("API_KEY_INVALID"))) {
				return ChatStreamEvent.error("Chave GOOGLE_API_KEY inválida.");
			}
			return ChatStreamEvent.error(message != null ? message : "Erro na API do Gemini.");
		}

		return ChatStreamEvent.error(
				exception.getMessage() != null ? exception.getMessage() : "Falha ao gerar resposta.");
	}

	private static <T extends Throwable> T findCause(Throwable throwable, Class<T> type) {
		Throwable current = throwable;
		while (current != null) {
			if (type.isInstance(current)) {
				return type.cast(current);
			}
			current = current.getCause();
		}
		return null;
	}

}
