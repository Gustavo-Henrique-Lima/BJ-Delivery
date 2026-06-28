package com.bj_food.ms_api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // públicos
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/error").permitAll()

                        // usuários
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasAnyRole("ADMIN", "CLIENTE")

                        // restaurantes
                        .requestMatchers(HttpMethod.GET, "/api/restaurantes/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/restaurantes/**").hasAnyRole("ADMIN", "RESTAURANTE")
                        .requestMatchers(HttpMethod.PUT, "/api/restaurantes/**").hasAnyRole("ADMIN", "RESTAURANTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/restaurantes/**").hasAnyRole("ADMIN")

                        // catálogo
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/catalogo/**").hasAnyRole("ADMIN", "RESTAURANTE")
                        .requestMatchers(HttpMethod.PUT, "/api/catalogo/**").hasAnyRole("ADMIN", "RESTAURANTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/catalogo/**").hasAnyRole("ADMIN", "RESTAURANTE")

                        // pedidos
                        .requestMatchers(HttpMethod.POST, "/api/pedidos").hasAnyRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/**").hasAnyRole("ADMIN", "CLIENTE", "RESTAURANTE")
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/**").hasAnyRole("ADMIN", "RESTAURANTE")

                        // pagamentos
                        .requestMatchers("/api/pagamentos/**").hasAnyRole("ADMIN", "CLIENTE")

                        // entregas
                        .requestMatchers(HttpMethod.GET, "/api/entregas/**").hasAnyRole("ADMIN", "ENTREGADOR", "CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/entregas/**").hasAnyRole("ENTREGADOR")

                        // avaliações
                        .requestMatchers(HttpMethod.POST, "/api/avaliacoes/**").hasAnyRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/avaliacoes/**").authenticated()

                        // notificações — interno apenas
                        .requestMatchers("/api/notificacoes/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }
}