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
import com.exchanger.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Override
    public UUID createUser(UserRecord userRecord) {
        validateUserRecord(userRecord);
        User user = userMapper.fromRecord(userRecord);
        return userRepository.save(user).getId();
    }

    private void validateUserRecord(UserRecord userRecord) {
        if (userRecord == null) {
            throw new NullPointerException();
        }
        if (userRepository.existsByEmail(userRecord.email())) {
            throw new NotUniqueDataException();
        }
    }

    @Override
    public String activateUser(String email, long chatId) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            log.warn("User {} not found", email);
            return "Can not activate this user";
        }

        User user = optionalUser.get()
                .setTelegramChatId(chatId)
                .setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        createDefaultWalletsForUser(user);

        return String.format("User %s %s is activated", user.getFirstName(), user.getLastName());
    }

    @Override
    public void createDefaultWalletsForUser(User user) {
        Arrays.stream(Currency.values()).forEach(currency -> createWallet(currency, user));
    }

    private void createWallet(Currency currency, User user) {
        Wallet wallet = new Wallet()
                .setUser(user)
                .setCurrency(currency)
                .setBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Override
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found by id"));
        return userMapper.toDto(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found by email"));
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = findByEmail(email);
        return userMapper.toDto(user);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

}
