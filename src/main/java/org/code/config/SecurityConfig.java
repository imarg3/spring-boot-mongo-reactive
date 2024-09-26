package org.code.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges

                        .pathMatchers("/api/orders/all-todos").hasRole("ADMIN")
                        .pathMatchers("/api/orders/{id}").hasAnyRole("USER", "ADMIN")
                        .pathMatchers("/api/orders/todo/{id}").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/orders").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((exchange, e) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return Mono.empty();
                        })
                        .accessDeniedHandler((exchange, denied) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return Mono.empty();
                        })
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable); // Disable CSRF protection using the updated method

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var admin = User.withUsername("admin")
                .password("{noop}adminpassword")
                .roles("ADMIN")
                .build();

        var user = User.withUsername("user")
                .password("{noop}password")
                .roles("USER")
                .build();

        var testUser = User.withUsername("testUser")
                .password("{noop}testpassword")
                .roles("TEST")
                .build();

        return new InMemoryUserDetailsManager(admin, user, testUser);
    }
}
