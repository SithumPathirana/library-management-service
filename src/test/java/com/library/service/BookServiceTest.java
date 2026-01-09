package com.library.service;

import com.library.dto.request.BookRequest;
import com.library.dto.response.BookResponse;
import com.library.exception.BookNotFoundException;
import com.library.exception.InvalidBookDataException;
import com.library.mapper.LibraryMapper;
import com.library.model.Book;
import com.library.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LibraryMapper libraryMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    @DisplayName("Register Book - Success")
    void registerBook_Success() {
        // Given
        BookRequest request = new BookRequest();
        request.setIsbn("1234567890");
        request.setTitle("Test Book");
        request.setAuthor("Test Author");

        Book book = new Book();
        book.setIsbn("1234567890");

        Book savedBook = new Book();
        savedBook.setId(UUID.randomUUID());
        savedBook.setIsbn("1234567890");

        BookResponse response = BookResponse.builder()
                .id(savedBook.getId())
                .isbn("1234567890")
                .build();

        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.empty());
        when(libraryMapper.toBook(request)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(libraryMapper.toBookResponse(savedBook)).thenReturn(response);

        // When
        BookResponse result = bookService.registerBook(request);

        // Then
        assertNotNull(result);
        assertEquals(savedBook.getId(), result.getId());
        verify(bookRepository).findByIsbn("1234567890");
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Register Book - Already Exists")
    void registerBook_AlreadyExists() {
        // Given
        BookRequest request = new BookRequest();
        request.setIsbn("1234567890");

        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.of(new Book()));

        // When & Then
        assertThrows(InvalidBookDataException.class, () -> bookService.registerBook(request));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Get All Books - Success")
    void getAllBooks_Success() {
        // Given
        Book book = new Book();
        BookResponse response = BookResponse.builder().build();

        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(libraryMapper.toBookResponse(book)).thenReturn(response);

        // When
        List<BookResponse> result = bookService.getAllBooks();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookRepository).findAll();
    }

    @Test
    @DisplayName("Get All Books - Empty")
    void getAllBooks_Empty() {
        // Given
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<BookResponse> result = bookService.getAllBooks();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Get Book By ID - Success")
    void getBookById_Success() {
        // Given
        UUID id = UUID.randomUUID();
        Book book = new Book();
        book.setId(id);
        BookResponse response = BookResponse.builder().id(id).build();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(libraryMapper.toBookResponse(book)).thenReturn(response);

        // When
        BookResponse result = bookService.getBookById(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(bookRepository).findById(id);
    }

    @Test
    @DisplayName("Get Book By ID - Not Found")
    void getBookById_NotFound() {
        // Given
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(id));
    }
}

