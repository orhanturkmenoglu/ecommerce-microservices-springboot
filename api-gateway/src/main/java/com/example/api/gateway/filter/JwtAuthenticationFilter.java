package com.example.api.gateway.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/*
Gateway tarafında bu JWT’yi kontrol etmek istiyorsanız bir JWT filter yazabilirsiniz
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final static Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            if (routeValidator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    log.error("Authorization header is missing at {}", exchange.getRequest().getURI());
                    return onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
                }

                String authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

                if (authHeaders == null || !authHeaders.startsWith("Bearer ")) {
                    log.error("Authorization header is invalid at {}", exchange.getRequest().getURI());
                    return onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);
                }

                authHeaders = authHeaders.substring(7);


                return jwtTokenUtil.validateToken(authHeaders)
                        .flatMap(isValid -> {
                            if (!isValid) {
                                log.error("Authorization token is invalid at {}", exchange.getRequest().getURI());
                                return onError(exchange, "Authorization token is invalid", HttpStatus.UNAUTHORIZED);
                            }
                            return chain.filter(exchange);
                        })
                        .onErrorResume(e -> {
                            log.error("Token validation failed : {}", e.getMessage());
                            return onError(exchange, e.getMessage(), HttpStatus.UNAUTHORIZED);
                        });
            }

            return chain.filter(exchange); // Eğer doğrulama gerekmiyorsa, filtreyi geç
        });
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus) {

        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        String body = String.format("{\"timestamp\":\"%s\",\"status\":%d,\"message\":\"%s\",\"path\":\"%s\"}",
                LocalDateTime.now(), httpStatus.value(), error, exchange.getRequest().getPath());

        DataBuffer dataBuffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }

    public static class Config {

    }
}
