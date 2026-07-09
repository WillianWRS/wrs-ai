package wrs.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.genai.Client;

@Configuration(proxyBeanMethods = false)
public class GoogleGenAiClientConfig {

	@Bean
	@ConditionalOnMissingBean
	Client googleGenAiClient(@Value("${GOOGLE_API_KEY}") String apiKey) {
		return Client.builder().apiKey(apiKey).build();
	}

}
