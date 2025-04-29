package com.example.api.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    // Kimlik doğrulama gerektirmeyen uç noktalar
    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/login",
            "/api/auth/register");

    // Güvenli uç noktalar için predikat
    public Predicate<ServerHttpRequest> isSecured = serverHttpRequest ->
            openApiEndpoints
                    .stream()
                    .noneMatch(uri -> serverHttpRequest.getURI().getPath().contains(uri));


}
