package com.jonassavas.spring_task_api.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Profile("!test")
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();

        Bucket bucket = resolveBucket(ip, path);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests. Try again later.\"}");
        }
    }

    private Bucket resolveBucket(String ip, String path) {
        String endpointGroup = resolveEndpointGroup(path);
        String key = ip + ":" + endpointGroup;

        return buckets.computeIfAbsent(key, k -> newBucket(endpointGroup));
    }

    private String resolveEndpointGroup(String path) {
        if (path.startsWith("/auth/login") || path.startsWith("/auth/register")) {
            return "auth";
        }
        return "api";
    }

    private Bucket newBucket(String endpointGroup) {
        Bandwidth limit;

        if (endpointGroup.equals("auth")) {
            // Burst-friendly login/register
            limit = Bandwidth.builder().capacity(5).refillGreedy(5, Duration.ofSeconds(10)).build();
        } else {
            // General API protection
            limit =
                    Bandwidth.builder()
                            .capacity(300)
                            .refillIntervally(300, Duration.ofMinutes(1))
                            .build();
        }

        return Bucket.builder().addLimit(limit).build();
    }
}
