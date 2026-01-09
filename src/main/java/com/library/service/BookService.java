package com.library.service;

import com.library.dto.request.BookRequest;
import com.library.dto.response.BookResponse;
import com.library.exception.InvalidBookDataException;
import com.library.mapper.LibraryMapper;
import com.library.model.Book;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final LibraryMapper libraryMapper;

    @Transactional
    public BookResponse registerBook(BookRequest request) {
        if (bookRepository.findByIsbn(request.getIsbn()).isPresent()) {
            throw new InvalidBookDataException("Book with ISBN " + request.getIsbn() + " already exists.");
        }
        Book book = libraryMapper.toBook(request);
        Book savedBook = bookRepository.save(book);
        return libraryMapper.toBookResponse(savedBook);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(libraryMapper::toBookResponse)
                .toList();
    }
}

