package com.library.controller;

import com.library.dto.request.BookRequest;
import com.library.dto.request.BorrowerRequest;
import com.library.dto.response.BookResponse;
import com.library.dto.response.BorrowerResponse;
import com.library.service.BookService;
import com.library.service.BorrowerService;
import com.library.service.BorrowingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LibraryController {

    private final BookService bookService;
    private final BorrowerService borrowerService;
    private final BorrowingService borrowingService;

    // --- Book Endpoints ---

    @PostMapping("/books")
    public ResponseEntity<BookResponse> registerBook(@Valid @RequestBody BookRequest request) {
        BookResponse response = bookService.registerBook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> response = bookService.getAllBooks();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable UUID id) {
        BookResponse response = bookService.getBookById(id);
        return ResponseEntity.ok(response);
    }

    // --- Borrower Endpoints ---

    @PostMapping("/borrowers")
    public ResponseEntity<BorrowerResponse> registerBorrower(@Valid @RequestBody BorrowerRequest request) {
        BorrowerResponse response = borrowerService.registerBorrower(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/borrowers")
    public ResponseEntity<List<BorrowerResponse>> getAllBorrowers() {
        List<BorrowerResponse> response = borrowerService.getAllBorrowers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/borrowers/{id}")
    public ResponseEntity<BorrowerResponse> getBorrowerById(@PathVariable UUID id) {
        BorrowerResponse response = borrowerService.getBorrowerById(id);
        return ResponseEntity.ok(response);
    }

    // --- Borrowing Endpoints ---

    @PostMapping("/books/{bookId}/borrow")
    public ResponseEntity<BookResponse> borrowBook(
            @PathVariable UUID bookId,
            @RequestParam UUID borrowerId) {
        BookResponse response = borrowingService.borrowBook(bookId, borrowerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/books/{bookId}/return")
    public ResponseEntity<BookResponse> returnBook(@PathVariable UUID bookId) {
        BookResponse response = borrowingService.returnBook(bookId);
        return ResponseEntity.ok(response);
    }
}

