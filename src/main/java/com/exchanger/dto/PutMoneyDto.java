package com.exchanger.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PutMoneyDto extends MoneyTransactionDto {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Sender cannot be blank")
    private String sender;

}
