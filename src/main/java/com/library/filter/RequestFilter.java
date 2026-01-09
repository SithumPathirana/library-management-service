package com.library.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.exception.ErrorCode;
import com.library.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String MDC_KEY = "X-Correlation-Id";
    
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Skip validation for Swagger UI and API Docs
        String path = request.getRequestURI();
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String correlationId = request.getHeader(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.trim().isEmpty()) {
            handleMissingCorrelationId(request, response);
            return;
        }

        try {
            MDC.put(MDC_KEY, correlationId);
            log.info("Incoming Request: [Method: {}, URI: {}]", request.getMethod(), request.getRequestURI());
            
            filterChain.doFilter(request, response);
            
            log.info("Outgoing Response: [Status: {}]", response.getStatus());
        } finally {
            MDC.remove(MDC_KEY);
        }
    }

    private void handleMissingCorrelationId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ErrorCode errorCode = ErrorCode.CORRELATION_ID_NOT_FOUND;
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getHttpStatus().value())
                .errorCode(errorCode.getCode())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .message(errorCode.getMessage())
                .path(request.getRequestURI())
                .build();

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}

