package com.library.service;

import com.library.dto.request.BorrowerRequest;
import com.library.dto.response.BorrowerResponse;
import com.library.mapper.LibraryMapper;
import com.library.model.Borrower;
import com.library.repository.BorrowerRepository;
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
class BorrowerServiceTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private LibraryMapper libraryMapper;

    @InjectMocks
    private BorrowerService borrowerService;

    @Test
    @DisplayName("Register Borrower - Success")
    void registerBorrower_Success() {
        // Given
        BorrowerRequest request = new BorrowerRequest();
        request.setEmail("test@example.com");
        request.setName("Test User");

        Borrower borrower = new Borrower();
        borrower.setEmail("test@example.com");

        Borrower savedBorrower = new Borrower();
        savedBorrower.setId(UUID.randomUUID());
        savedBorrower.setEmail("test@example.com");

        BorrowerResponse response = BorrowerResponse.builder()
                .id(savedBorrower.getId())
                .email("test@example.com")
                .build();

        when(borrowerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(libraryMapper.toBorrower(request)).thenReturn(borrower);
        when(borrowerRepository.save(borrower)).thenReturn(savedBorrower);
        when(libraryMapper.toBorrowerResponse(savedBorrower)).thenReturn(response);

        // When
        BorrowerResponse result = borrowerService.registerBorrower(request);

        // Then
        assertNotNull(result);
        assertEquals(savedBorrower.getId(), result.getId());
        verify(borrowerRepository).findByEmail("test@example.com");
        verify(borrowerRepository).save(borrower);
    }

    @Test
    @DisplayName("Register Borrower - Already Exists")
    void registerBorrower_AlreadyExists() {
        // Given
        BorrowerRequest request = new BorrowerRequest();
        request.setEmail("test@example.com");

        when(borrowerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new Borrower()));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> borrowerService.registerBorrower(request));
        verify(borrowerRepository, never()).save(any(Borrower.class));
    }

    @Test
    @DisplayName("Get All Borrowers - Success")
    void getAllBorrowers_Success() {
        // Given
        Borrower borrower = new Borrower();
        BorrowerResponse response = BorrowerResponse.builder().build();

        when(borrowerRepository.findAll()).thenReturn(List.of(borrower));
        when(libraryMapper.toBorrowerResponse(borrower)).thenReturn(response);

        // When
        List<BorrowerResponse> result = borrowerService.getAllBorrowers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(borrowerRepository).findAll();
    }

    @Test
    @DisplayName("Get All Borrowers - Empty")
    void getAllBorrowers_Empty() {
        // Given
        when(borrowerRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<BorrowerResponse> result = borrowerService.getAllBorrowers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Get Borrower By ID - Success")
    void getBorrowerById_Success() {
        // Given
        UUID id = UUID.randomUUID();
        Borrower borrower = new Borrower();
        borrower.setId(id);
        BorrowerResponse response = BorrowerResponse.builder().id(id).build();

        when(borrowerRepository.findById(id)).thenReturn(Optional.of(borrower));
        when(libraryMapper.toBorrowerResponse(borrower)).thenReturn(response);

        // When
        BorrowerResponse result = borrowerService.getBorrowerById(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(borrowerRepository).findById(id);
    }

    @Test
    @DisplayName("Get Borrower By ID - Not Found")
    void getBorrowerById_NotFound() {
        // Given
        UUID id = UUID.randomUUID();
        when(borrowerRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> borrowerService.getBorrowerById(id));
    }
}

