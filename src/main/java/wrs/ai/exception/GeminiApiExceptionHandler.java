package wrs.ai.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.google.genai.errors.ClientException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GeminiApiExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleRuntimeException(RuntimeException exception) {
		ClientException clientException = findCause(exception, ClientException.class);
		if (clientException == null) {
			return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
					"error", "Failed to generate content",
					"message", exception.getMessage() != null ? exception.getMessage() : "Erro desconhecido")));
		}

		String message = clientException.getMessage();
		if (message != null && message.contains("429")) {
			return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
					"error", "Gemini API quota exceeded",
					"message", "A cota gratuita do projeto Google Cloud esgotou. Crie uma API key em outro projeto ou aguarde o reset da quota.",
					"details", message)));
		}

		if (message != null && (message.contains("API key not valid") || message.contains("API_KEY_INVALID"))) {
			return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
					"error", "Invalid Gemini API key",
					"message", "A chave GOOGLE_API_KEY é inválida ou não está associada ao projeto correto.")));
		}

		return Mono.just(ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
				"error", "Gemini API error",
				"message", message != null ? message : "Erro na API do Gemini")));
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
