package wrs.ai.controller;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import wrs.ai.dto.SemanticSearchRequest;
import wrs.ai.dto.SemanticSearchResponse;

@RestController
@RequestMapping("/api/search")
@ConditionalOnBean(VectorStore.class)
public class SearchController {

	private final VectorStore vectorStore;

	public SearchController(VectorStore vectorStore) {
		this.vectorStore = vectorStore;
	}

	@PostMapping
	public Mono<SemanticSearchResponse> search(@RequestBody SemanticSearchRequest request) {
		int topK = request.topK() != null ? request.topK() : SearchRequest.DEFAULT_TOP_K;

		return Mono.fromCallable(() -> {
			var searchRequest = SearchRequest.builder()
					.query(request.query())
					.topK(topK)
					.build();
			List<String> results = vectorStore.similaritySearch(searchRequest).stream()
					.map(Document::getText)
					.toList();
			return new SemanticSearchResponse(results);
		}).subscribeOn(Schedulers.boundedElastic());
	}

}
