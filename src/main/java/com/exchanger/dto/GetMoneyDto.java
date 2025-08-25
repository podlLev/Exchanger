package com.exchanger.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMoneyDto extends MoneyTransactionDto {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Receiver cannot be blank")
    private String receiver;

}
