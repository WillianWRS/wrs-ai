package wrs.ai.service;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import wrs.ai.dto.SemanticSearchRequest;
import wrs.ai.dto.SemanticSearchResponse;

@Service
@ConditionalOnBean(VectorStore.class)
public class SearchService {

	private final VectorStore vectorStore;

	public SearchService(VectorStore vectorStore) {
		this.vectorStore = vectorStore;
	}

	public Mono<SemanticSearchResponse> search(SemanticSearchRequest request) {
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
