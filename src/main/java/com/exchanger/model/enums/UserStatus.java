package com.exchanger.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {

    PENDING("User has not been activated yet"),
    ACTIVE("User is activated and can use the system");

    private final String description;

}
