package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account newAccount) {
        if (newAccount.getUsername() == null || newAccount.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank.");
        }
        if (newAccount.getPassword() == null || newAccount.getPassword().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
        try {
            return accountRepository.save(newAccount);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Username already exists.");
        }
    }

    public Account authenticate(String username, String password) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Invalid username or password."));
        
        if (!account.getPassword().equals(password)) {
            throw new EntityNotFoundException("Invalid username or password.");
        }
        
        return account;
    }

    public Optional<Account> findById(Integer accountId) {
        return accountRepository.findById(accountId);
    }

    public boolean checkUsernameExists(String username) {
        return accountRepository.findByUsername(username).isPresent();
    }
}
