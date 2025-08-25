package com.exchanger.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferMoneyDto extends MoneyTransactionDto {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String sender;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String receiver;

}
