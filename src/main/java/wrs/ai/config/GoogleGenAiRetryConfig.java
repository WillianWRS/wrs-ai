package wrs.ai.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.web.client.ResourceAccessException;

import org.springframework.ai.retry.TransientAiException;

@Configuration(proxyBeanMethods = false)
public class GoogleGenAiRetryConfig {

	@Bean
	RetryTemplate googleGenAiRetryTemplate() {
		RetryPolicy retryPolicy = RetryPolicy.builder()
				.maxRetries(0)
				.includes(TransientAiException.class)
				.includes(ResourceAccessException.class)
				.delay(Duration.ofMillis(1000))
				.build();
		return new RetryTemplate(retryPolicy);
	}

}
