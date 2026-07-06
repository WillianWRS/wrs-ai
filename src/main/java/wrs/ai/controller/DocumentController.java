package wrs.ai.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import wrs.ai.dto.DocumentRequest;
import wrs.ai.dto.DocumentResponse;

@RestController
@RequestMapping("/api/documents")
@ConditionalOnBean(VectorStore.class)
public class DocumentController {

	private final VectorStore vectorStore;

	public DocumentController(VectorStore vectorStore) {
		this.vectorStore = vectorStore;
	}

	@PostMapping
	public Mono<DocumentResponse> ingest(@RequestBody DocumentRequest request) {
		return Mono.fromCallable(() -> {
			String filename = StringUtils.hasText(request.filename()) ? request.filename() : "document.md";
			var resource = new ByteArrayResource(request.content().getBytes(StandardCharsets.UTF_8)) {
				@Override
				public String getFilename() {
					return filename;
				}
			};
			var config = MarkdownDocumentReaderConfig.builder()
					.withAdditionalMetadata("filename", filename)
					.build();
			List<Document> documents = new MarkdownDocumentReader(resource, config).get();
			vectorStore.add(documents);
			return new DocumentResponse(documents.size());
		}).subscribeOn(Schedulers.boundedElastic());
	}

}
