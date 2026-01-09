package com.library.exception;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFoundException(BookNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ErrorCode.BOOK_NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(BookAlreadyBorrowedException.class)
    public ResponseEntity<ErrorResponse> handleBookAlreadyBorrowedException(BookAlreadyBorrowedException ex, WebRequest request) {
        return buildErrorResponse(ErrorCode.BOOK_ALREADY_BORROWED, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidBookDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBookDataException(InvalidBookDataException ex, WebRequest request) {
        return buildErrorResponse(ErrorCode.INVALID_BOOK_DATA, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        // Mapping generic IllegalArgumentException to INVALID_REQUEST
        return buildErrorResponse(ErrorCode.INVALID_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailureException(OptimisticLockingFailureException ex, WebRequest request) {
        return buildErrorResponse(ErrorCode.BOOK_CONCURRENCY_ERROR, ErrorCode.BOOK_CONCURRENCY_ERROR.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ErrorCode.INVALID_REQUEST.getHttpStatus().value())
                .errorCode(ErrorCode.INVALID_REQUEST.getCode())
                .error(ErrorCode.INVALID_REQUEST.getHttpStatus().getReasonPhrase())
                .message(errors.toString()) // Simplifying map to string for response consistency
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, ErrorCode.INVALID_REQUEST.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode, String message, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getHttpStatus().value())
                .errorCode(errorCode.getCode())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .message(message != null ? message : errorCode.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }
}
