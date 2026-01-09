package com.library.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // General Errors
    INTERNAL_SERVER_ERROR("LIB_001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST("LIB_002", "Invalid request parameters", HttpStatus.BAD_REQUEST),
    CORRELATION_ID_NOT_FOUND("LIB_003", "X-Correlation-Id header is missing", HttpStatus.BAD_REQUEST),
    
    // Book Errors
    BOOK_NOT_FOUND("LIB_101", "Book not found", HttpStatus.NOT_FOUND),
    BOOK_ALREADY_BORROWED("LIB_102", "Book is already borrowed", HttpStatus.CONFLICT),
    INVALID_BOOK_DATA("LIB_103", "Invalid book data provided", HttpStatus.BAD_REQUEST),
    BOOK_CONCURRENCY_ERROR("LIB_104", "The book was updated by another transaction. Please try again.", HttpStatus.CONFLICT),

    // Borrower Errors
    BORROWER_NOT_FOUND("LIB_201", "Borrower not found", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}

