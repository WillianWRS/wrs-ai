package wrs.ai.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import wrs.ai.dto.DocumentRequest;
import wrs.ai.dto.DocumentResponse;
import wrs.ai.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
@ConditionalOnBean(DocumentService.class)
public class DocumentController {

	private final DocumentService documentService;

	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}

	@PostMapping
	public Mono<DocumentResponse> ingest(@RequestBody DocumentRequest request) {
		return documentService.ingest(request);
	}

}
