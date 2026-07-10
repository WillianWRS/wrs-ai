package wrs.ai.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.genai.Client;

@Configuration(proxyBeanMethods = false)
public class GoogleGenAiClientConfig {

	@Bean
	@ConditionalOnMissingBean
	Client googleGenAiClient() {
		return Client.builder().build();
	}

}
