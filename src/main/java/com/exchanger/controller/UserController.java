package com.exchanger.controller;

import com.exchanger.dto.UserDto;
import com.exchanger.dto.record.UserRecord;
import com.exchanger.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @version 0.0.1
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID createUser(@Valid @RequestBody UserRecord userRecord) {
        log.info("Run method UserController.createUser");
        return userService.createUser(userRecord);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Run method UserController.getUsers");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable UUID id) {
        log.info("Run method UserController.getUserById");
        return userService.getUserById(id);
    }

    @GetMapping("/email")
    public UserDto getUserByEmail(@RequestParam String email) {
        log.info("Run method UserController.getUserByEmail");
        return userService.getUserByEmail(email);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable UUID id) {
        log.info("Run method UserController.deleteUserById");
        userService.deleteUser(id);
    }

}
