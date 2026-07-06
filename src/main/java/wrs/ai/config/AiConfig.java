package wrs.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(ChatModel.class)
public class AiConfig {

	@Bean
	ChatClient chatClient(ChatModel chatModel) {
		return ChatClient.builder(chatModel).build();
	}

	@Bean
	@ConditionalOnBean(VectorStore.class)
	ChatClient ragChatClient(ChatModel chatModel, VectorStore vectorStore) {
		return ChatClient.builder(chatModel)
				.defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
				.build();
	}

}
