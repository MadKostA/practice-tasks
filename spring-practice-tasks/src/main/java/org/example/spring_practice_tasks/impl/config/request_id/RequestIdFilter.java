package org.example.spring_practice_tasks.impl.config.request_id;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String X_REQUEST_ID = "X-Request-ID";
    private static final String REQUEST_ID = "requestId";

    private final RequestIdHolder requestIdHolder;

    public RequestIdFilter(RequestIdHolder requestIdHolder) {
        this.requestIdHolder = requestIdHolder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestId = request.getHeader(X_REQUEST_ID);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        requestIdHolder.setRequestId(requestId);

        MDC.put(REQUEST_ID, requestId);

        response.addHeader(X_REQUEST_ID, requestId);

        filterChain.doFilter(request, response);

        MDC.remove(REQUEST_ID);
    }
}
