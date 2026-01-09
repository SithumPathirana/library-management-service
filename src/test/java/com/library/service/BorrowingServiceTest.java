package com.library.service;

import com.library.dto.response.BookResponse;
import com.library.exception.BookAlreadyBorrowedException;
import com.library.exception.BookNotFoundException;
import com.library.mapper.LibraryMapper;
import com.library.model.Book;
import com.library.model.BookStatus;
import com.library.model.Borrower;
import com.library.repository.BookRepository;
import com.library.repository.BorrowerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private LibraryMapper libraryMapper;

    @InjectMocks
    private BorrowingService borrowingService;

    @Test
    @DisplayName("Borrow Book - Success")
    void borrowBook_Success() {
        // Given
        UUID bookId = UUID.randomUUID();
        UUID borrowerId = UUID.randomUUID();

        Borrower borrower = new Borrower();
        borrower.setId(borrowerId);

        Book book = new Book();
        book.setId(bookId);
        book.setStatus(BookStatus.AVAILABLE);

        Book borrowedBook = new Book();
        borrowedBook.setId(bookId);
        borrowedBook.setStatus(BookStatus.BORROWED);

        BookResponse response = BookResponse.builder()
                .id(bookId)
                .status(BookStatus.BORROWED)
                .build();

        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(borrowedBook);
        when(libraryMapper.toBookResponse(borrowedBook)).thenReturn(response);

        // When
        BookResponse result = borrowingService.borrowBook(bookId, borrowerId);

        // Then
        assertNotNull(result);
        assertEquals(BookStatus.BORROWED, result.getStatus());
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Borrow Book - Borrower Not Found")
    void borrowBook_BorrowerNotFound() {
        // Given
        UUID bookId = UUID.randomUUID();
        UUID borrowerId = UUID.randomUUID();

        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> borrowingService.borrowBook(bookId, borrowerId));
        verify(bookRepository, never()).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Borrow Book - Book Not Found")
    void borrowBook_BookNotFound() {
        // Given
        UUID bookId = UUID.randomUUID();
        UUID borrowerId = UUID.randomUUID();

        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(new Borrower()));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> borrowingService.borrowBook(bookId, borrowerId));
    }

    @Test
    @DisplayName("Borrow Book - Already Borrowed")
    void borrowBook_AlreadyBorrowed() {
        // Given
        UUID bookId = UUID.randomUUID();
        UUID borrowerId = UUID.randomUUID();
        Book book = new Book();
        book.setId(bookId);
        book.setStatus(BookStatus.BORROWED);

        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(new Borrower()));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When & Then
        assertThrows(BookAlreadyBorrowedException.class, () -> borrowingService.borrowBook(bookId, borrowerId));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Return Book - Success")
    void returnBook_Success() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book book = new Book();
        book.setId(bookId);
        book.setStatus(BookStatus.BORROWED);

        Book returnedBook = new Book();
        returnedBook.setId(bookId);
        returnedBook.setStatus(BookStatus.AVAILABLE);

        BookResponse response = BookResponse.builder()
                .id(bookId)
                .status(BookStatus.AVAILABLE)
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(returnedBook);
        when(libraryMapper.toBookResponse(returnedBook)).thenReturn(response);

        // When
        BookResponse result = borrowingService.returnBook(bookId);

        // Then
        assertNotNull(result);
        assertEquals(BookStatus.AVAILABLE, result.getStatus());
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Return Book - Already Available")
    void returnBook_AlreadyAvailable() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book book = new Book();
        book.setId(bookId);
        book.setStatus(BookStatus.AVAILABLE);

        BookResponse response = BookResponse.builder()
                .id(bookId)
                .status(BookStatus.AVAILABLE)
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(libraryMapper.toBookResponse(book)).thenReturn(response);

        // When
        BookResponse result = borrowingService.returnBook(bookId);

        // Then
        assertNotNull(result);
        assertEquals(BookStatus.AVAILABLE, result.getStatus());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Return Book - Not Found")
    void returnBook_NotFound() {
        // Given
        UUID bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> borrowingService.returnBook(bookId));
    }
}

