package wrs.ai.controller;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import wrs.ai.dto.ChatRequest;
import wrs.ai.dto.ChatResponse;
import wrs.ai.service.RagService;

@RestController
@RequestMapping("/api/rag")
@ConditionalOnBean(VectorStore.class)
public class RagController {

	private final RagService ragService;

	public RagController(RagService ragService) {
		this.ragService = ragService;
	}

	@PostMapping("/chat")
	public Mono<ChatResponse> chat(@RequestBody ChatRequest request) {
		return ragService.chat(request);
	}

}
