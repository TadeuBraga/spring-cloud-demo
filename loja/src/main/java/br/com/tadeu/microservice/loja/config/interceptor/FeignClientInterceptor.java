package br.com.tadeu.microservice.loja.config.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Configuration
public class FeignClientInterceptor {

	@Bean
	public RequestInterceptor getAuthenticationInterceptor() {
		return  (RequestTemplate rt) -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if(authentication == null) {
				return;
			} else {
				OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
				rt.header("Authorization", "Bearer "+details.getTokenValue());
				
			}
		};
	}
}
