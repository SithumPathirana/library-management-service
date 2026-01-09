package com.library.mapper;

import com.library.dto.request.BookRequest;
import com.library.dto.request.BorrowerRequest;
import com.library.dto.response.BookResponse;
import com.library.dto.response.BorrowerResponse;
import com.library.model.Book;
import com.library.model.Borrower;
import org.springframework.stereotype.Component;

@Component
public class LibraryMapper {

    public Book toBook(BookRequest request) {
        if (request == null) {
            return null;
        }
        return Book.builder()
                .isbn(request.getIsbn())
                .title(request.getTitle())
                .author(request.getAuthor())
                .status(com.library.model.BookStatus.AVAILABLE)
                .build();
    }

    public BookResponse toBookResponse(Book book) {
        if (book == null) {
            return null;
        }
        return BookResponse.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .status(book.getStatus())
                .build();
    }

    public Borrower toBorrower(BorrowerRequest request) {
        if (request == null) {
            return null;
        }
        return Borrower.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    public BorrowerResponse toBorrowerResponse(Borrower borrower) {
        if (borrower == null) {
            return null;
        }
        return BorrowerResponse.builder()
                .id(borrower.getId())
                .name(borrower.getName())
                .email(borrower.getEmail())
                .build();
    }
}

