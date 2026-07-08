package wrs.ai.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import wrs.ai.dto.SemanticSearchRequest;
import wrs.ai.dto.SemanticSearchResponse;
import wrs.ai.service.SearchService;

@RestController
@RequestMapping("/api/search")
@ConditionalOnBean(SearchService.class)
public class SearchController {

	private final SearchService searchService;

	public SearchController(SearchService searchService) {
		this.searchService = searchService;
	}

	@PostMapping
	public Mono<SemanticSearchResponse> search(@RequestBody SemanticSearchRequest request) {
		return searchService.search(request);
	}

}
