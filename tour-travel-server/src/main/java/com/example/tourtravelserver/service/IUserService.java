package com.example.tourtravelserver.service;

import com.example.tourtravelserver.dto.*;
import com.example.tourtravelserver.entity.User;
import com.example.tourtravelserver.enums.CustomerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface IUserService {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);

    @Transactional
    void register(User user);

    void resendEmailVerification(User user);

    String updateAvatar(Long userId, String newAvatar) throws Exception;

    Page<UserDTO> getUsers(UserSearchRequest request, Pageable pageable);

    Page<CustomerResponse> getAllCustomers(Pageable pageable,
                                           String search,
                                           CustomerType customerType,
                                           Boolean status);

    CustomerResponse getCustomerById(Long id);

    CustomerResponse updateCustomerStatus(Long id, Boolean status);

    CustomerResponse updateCustomerType(Long id, CustomerType customerType);

    CustomerStats getCustomerStats();

    Optional<CustomerResponse> getCustomerByEmail(String email);

}
