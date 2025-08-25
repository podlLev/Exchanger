package com.exchanger.mapper;

import com.exchanger.dto.UserDto;
import com.exchanger.dto.record.UserRecord;
import com.exchanger.model.User;
import com.exchanger.model.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void fromRecord() {
        UserRecord userRecord = new UserRecord("John", "Doe", "john.doe@gmail.com");

        User user = userMapper.fromRecord(userRecord);

        assertNotNull(user);
        assertEquals(userRecord.firstName(), user.getFirstName());
        assertEquals(userRecord.lastName(), user.getLastName());
        assertEquals(userRecord.email(), user.getEmail());
        assertEquals(UserStatus.PENDING, user.getStatus());
    }

    @Test
    void fromRecord_empty() {
        User user = userMapper.fromRecord(null);
        assertNull(user);
    }

    @Test
    void toDto() {
        User user = new User()
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail("john.doe@gmail.com")
                .setStatus(UserStatus.PENDING);

        UserDto userDto = userMapper.toDto(user);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getFirstName() + " " + user.getLastName(), userDto.getUsername());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getStatus(), userDto.getStatus());
    }

    @Test
    void toDto_empty() {
        UserDto userDto = userMapper.toDto(null);
        assertNull(userDto);
    }

}
