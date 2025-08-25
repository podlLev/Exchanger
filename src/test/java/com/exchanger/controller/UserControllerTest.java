package com.exchanger.controller;

import com.exchanger.dto.UserDto;
import com.exchanger.dto.record.UserRecord;
import com.exchanger.service.UserService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UUID userId;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        userDto = new UserDto()
                .setId(userId)
                .setUsername("John")
                .setEmail("john.doe@example.com");

        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(any(UserRecord.class))).thenReturn(userId);

        String userJson = """
            {
                "email": "john.doe@example.com",
                "firstName": "John",
                "lastName": "Doe"
            }
            """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("\"" + userId.toString() + "\""));

        verify(userService).createUser(any(UserRecord.class));
    }

    @Test
    void getUsers() throws Exception {
        List<UserDto> users = List.of(userDto);
        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(userId.toString()))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(userId)).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void getUserByEmail() throws Exception {
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/users/email")
                        .param("email", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void deleteUserById() throws Exception {
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/v1/users/" + userId))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(userId);
    }

}
