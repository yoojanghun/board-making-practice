package com.example.demo.controller;

import com.example.demo.domain.UserAccount;
import com.example.demo.dto.UserAccountDto;
import com.example.demo.dto.request.UserAccountRequest;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignUpController {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpController(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/sign-up")
    public UserAccount signUp(@RequestBody UserAccountRequest userAccountRequest) {
        UserAccountDto userAccountDto = userAccountRequest.toDto(passwordEncoder);
        UserAccount userAccount = UserAccountDto.toEntity(userAccountDto);

        return userAccountRepository.save(userAccount);
    }
}
