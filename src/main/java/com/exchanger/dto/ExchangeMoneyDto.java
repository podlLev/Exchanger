package com.exchanger.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeMoneyDto extends MoneyTransactionDto {

    @Email(message = "Invalid email format")
    @NotBlank(message = "User cannot be blank")
    private String user;

    @NotBlank(message = "Source currency cannot be blank")
    private String sourceCurrency;

}
