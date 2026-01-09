package com.library.service;

import com.library.dto.response.BookResponse;
import com.library.exception.BookAlreadyBorrowedException;
import com.library.exception.BookNotFoundException;
import com.library.mapper.LibraryMapper;
import com.library.model.Book;
import com.library.model.BookStatus;
import com.library.repository.BookRepository;
import com.library.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BorrowingService {

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final LibraryMapper libraryMapper;

    @Transactional
    public BookResponse borrowBook(UUID bookId, UUID borrowerId) {
        borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new IllegalArgumentException("Borrower not found with ID: " + borrowerId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));

        if (book.getStatus() == BookStatus.BORROWED) {
            throw new BookAlreadyBorrowedException("Book with ID " + bookId + " is already borrowed.");
        }

        book.setStatus(BookStatus.BORROWED);
        Book savedBook = bookRepository.save(book);
        return libraryMapper.toBookResponse(savedBook);
    }

    @Transactional
    public BookResponse returnBook(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));

        if (book.getStatus() == BookStatus.AVAILABLE) {
             return libraryMapper.toBookResponse(book);
        }

        book.setStatus(BookStatus.AVAILABLE);
        Book savedBook = bookRepository.save(book);
        return libraryMapper.toBookResponse(savedBook);
    }
}

