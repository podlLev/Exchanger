package com.exchanger.service.impl;

import com.exchanger.dto.UserDto;
import com.exchanger.dto.record.UserRecord;
import com.exchanger.exception.NotUniqueDataException;
import com.exchanger.exception.notfound.UserNotFoundException;
import com.exchanger.mapper.UserMapper;
import com.exchanger.model.User;
import com.exchanger.model.Wallet;
import com.exchanger.model.enums.Currency;
import com.exchanger.model.enums.UserStatus;
import com.exchanger.repository.UserRepository;
import com.exchanger.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testCreateUser_Success() {
        UserRecord record = new UserRecord("John", "Doe", "john@example.com");
        UUID id = UUID.randomUUID();
        User user = (User) new User().setId(id);

        when(userRepository.existsByEmail(record.email())).thenReturn(false);
        when(userMapper.fromRecord(record)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        UUID result = userService.createUser(record);

        assertEquals(id, result);
        verify(userRepository).save(user);
    }

    @Test
    void testCreateUser_NullRecord_ShouldThrow() {
        assertThrows(NullPointerException.class, () -> userService.createUser(null));
    }

    @Test
    void testCreateUser_EmailNotUnique_ShouldThrow() {
        UserRecord record = new UserRecord("Jane", "Doe", "jane@example.com");
        when(userRepository.existsByEmail(record.email())).thenReturn(true);

        assertThrows(NotUniqueDataException.class, () -> userService.createUser(record));
    }

    @Test
    void testActivateUser_UserFound_ShouldActivateAndCreateWallets() {
        String email = "john@example.com";
        long chatId = 123456L;

        User user = new User()
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail(email)
                .setStatus(UserStatus.PENDING);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        String result = userService.activateUser(email, chatId);

        assertTrue(result.contains("John Doe is activated"));
        verify(userRepository).save(argThat(u ->
                u.getTelegramChatId() == chatId && u.getStatus() == UserStatus.ACTIVE
        ));
        verify(walletRepository, times(Currency.values().length)).save(any(Wallet.class));
    }

    @Test
    void testActivateUser_UserNotFound_ShouldReturnWarning() {
        String email = "missing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        String result = userService.activateUser(email, 999L);

        assertEquals("Can not activate this user", result);
        verify(userRepository, never()).save(any());
        verify(walletRepository, never()).save(any());
    }

    @Test
    void testGetUsers_ShouldReturnUserDtoList() {
        User user1 = new User().setFirstName("A");
        User user2 = new User().setFirstName("B");

        UserDto dto1 = new UserDto().setUsername("A");
        UserDto dto2 = new UserDto().setUsername("B");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toDto(user1)).thenReturn(dto1);
        when(userMapper.toDto(user2)).thenReturn(dto2);

        List<UserDto> result = userService.getUsers();

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getUsername());
    }

    @Test
    void testGetUserById_Found() {
        UUID id = UUID.randomUUID();
        User user = new User().setFirstName("John");
        UserDto dto = new UserDto().setUsername("John");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.getUserById(id);
        assertEquals("John", result.getUsername());
    }

    @Test
    void testGetUserById_NotFound_ShouldThrow() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    void testGetUserByEmail_Found() {
        String email = "john@example.com";
        User user = new User().setEmail(email);
        UserDto dto = new UserDto().setUsername("John");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.getUserByEmail(email);
        assertEquals("John", result.getUsername());
    }

    @Test
    void testGetUserByEmail_NotFound_ShouldThrow() {
        when(userRepository.findByEmail("none")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("none"));
    }

    @Test
    void testDeleteUser_ShouldCallRepository() {
        UUID id = UUID.randomUUID();
        userService.deleteUser(id);

        verify(userRepository).deleteById(id);
    }

}
