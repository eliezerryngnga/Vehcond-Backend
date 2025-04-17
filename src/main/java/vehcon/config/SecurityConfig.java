package vehcon.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static vehcon.models.auth.Role.ADMIN;
import static vehcon.models.auth.Role.DA;
import static vehcon.models.auth.Role.TD;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XContentTypeOptionsHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import vehcon.services.auth.LogoutService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final AuthenticationProvider authenticationProvider;
	// private final LogoutHandler logoutHandler;
	@Autowired
	private final LogoutService logoutService;
	@Autowired
	private Environment env;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeHttpRequests().requestMatchers("/auth/**").permitAll()
				.requestMatchers(GET, "/users/get-user-info").hasAnyAuthority(ADMIN.name())
				
				.requestMatchers(GET, "/users/profile","/menu","/districts","/financial-year","/district-rto","/vehicle-type","/vehicle-manufacturer","/department","/vehicle-parts","/draft/list")
				.hasAnyAuthority( DA.name(), ADMIN.name(),TD.name())
				
				.requestMatchers(POST, "/users/change-password","/users/update","/draft","/final-submit")
				.hasAnyAuthority(DA.name(), ADMIN.name(), TD.name())

				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.logout(logout -> logout.logoutUrl("/auth/logout")
						// .addLogoutHandler(logoutHandler)
						.logoutSuccessHandler(logoutService).invalidateHttpSession(true).deleteCookies("JSESSIONID"));
		// .logoutUrl("/auth/logout").addLogoutHandler(logoutHandler)
		// .logoutSuccessHandler((request, response, authentication) ->
		// SecurityContextHolder.clearContext());

		String urls = env.getProperty("cors.urls");

		http.cors(cors -> {
			CorsConfigurationSource cs = resources -> {
				CorsConfiguration corsConfiguration = new CorsConfiguration();
				corsConfiguration.setAllowedOrigins(List.of(urls));
				// corsConfiguration.setAllowedOrigins(List.of("*"));
				corsConfiguration.setAllowedMethods(List.of("POST", "GET", "DELETE", "PUT"));
				corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With",
						"Accept", "X-XSRF-TOKEN", "API-Key", "Referrer-Policy", "Referer", "Content-Length",
						"Cache-Control", "Upgrade-Insecure-Requests"
				// Add forwarding-related headers here
				// "X-Forwarded-For",
				// "X-Forwarded-Host",
				// "X-Forwarded-Proto",
				// "X-Forwarded-Port"
				));
				corsConfiguration.setAllowCredentials(true);
				return corsConfiguration;
			};
			cors.configurationSource(cs);
		});
		http.headers(x -> {
			x.xssProtection(y -> y.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK));
			x.addHeaderWriter(new XContentTypeOptionsHeaderWriter());
			// x.frameOptions(y -> y.sameOrigin());
//	    	//x.contentTypeOptions().disable();
//	    	x.referrerPolicy(y -> y.policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN));
			// x.contentSecurityPolicy(y-> y.policyDirectives("default-src 'self';"
//			+"script-src 'self' frame-src 'self' connect-src 'self' https://192.168.11.2 https://192.168.11.2:8443 http://192.168.11.2 http://192.168.11.2:8080 https://gad.meghalaya.gov.in https://gad.meghalaya.gov.in:8443 http://gad.meghalaya.gov.in http://gad.meghalaya.gov.in:8080 https://localhost https://localhost:8443 http://localhost http://localhost:8080"));
//	x.contentSecurityPolicy(y-> y.policyDirectives("default-src 'self';"
//+"script-src 'self' frame-src 'self' connect-src 'self' https://gad.meghalaya.gov.in https://gad.meghalaya.gov.in:8443"));
			x.contentSecurityPolicy(
					y -> y.policyDirectives("default-src 'self'; object-src https://megepayment.gov.in; "
							+ "script-src 'self'; frame-src 'self'; connect-src 'self' http://10.179.2.111 http://10.179.13.183:8083;"));
//	    	 // Add CORS headers manually
//	        x.addHeaderWriter((request, response) -> {
//	            response.setHeader("Access-Control-Allow-Origin", "http://10.179.2.178/");
//	            response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
//	            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept,X-XSRF-TOKEN, API-Key, Referrer-Policy, Referer,Content-Length, Cache-Control,Upgrade-Insecure-Requests");
//	            response.setHeader("Access-Control-Allow-Credentials", "true");
//	        });
		});

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/oras/auth/**")
		 .requestMatchers("/v3/api-docs/**")
		 .requestMatchers("configuration/**")
		 .requestMatchers("/swagger*/**")
		 .requestMatchers("/webjars/**")
		 .requestMatchers("/swagger-ui/**");
	}

//	@Bean
//    public LogoutSuccessHandler logoutSuccessHandler() {
//		return new LogoutService();
//        //SimpleUrlLogoutSuccessHandler successHandler = new SimpleUrlLogoutSuccessHandler();
//        //successHandler.setDefaultTargetUrl("/login?logout");
//        //return successHandler;
//    }

}
