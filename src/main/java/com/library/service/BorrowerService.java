package com.library.service;

import com.library.dto.request.BorrowerRequest;
import com.library.dto.response.BorrowerResponse;
import com.library.mapper.LibraryMapper;
import com.library.model.Borrower;
import com.library.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;
    private final LibraryMapper libraryMapper;

    @Transactional
    public BorrowerResponse registerBorrower(BorrowerRequest request) {
        if (borrowerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Borrower with email " + request.getEmail() + " already exists.");
        }
        Borrower borrower = libraryMapper.toBorrower(request);
        Borrower savedBorrower = borrowerRepository.save(borrower);
        return libraryMapper.toBorrowerResponse(savedBorrower);
    }

    @Transactional(readOnly = true)
    public List<BorrowerResponse> getAllBorrowers() {
        return borrowerRepository.findAll().stream()
            .map(libraryMapper::toBorrowerResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public BorrowerResponse getBorrowerById(java.util.UUID id) {
        return borrowerRepository.findById(id)
                .map(libraryMapper::toBorrowerResponse)
                .orElseThrow(() -> new IllegalArgumentException("Borrower not found with id: " + id));
    }
}

