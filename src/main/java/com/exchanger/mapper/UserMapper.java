package com.exchanger.mapper;

import com.exchanger.dto.UserDto;
import com.exchanger.dto.record.UserRecord;
import com.exchanger.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "status", expression = "java(com.exchanger.model.enums.UserStatus.PENDING)")
    User fromRecord(UserRecord userRecord);

    @Mapping(target = "username", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    UserDto toDto(User user);

}
