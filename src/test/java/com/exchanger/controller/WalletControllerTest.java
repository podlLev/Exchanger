package com.exchanger.controller;

import com.exchanger.dto.*;
import com.exchanger.service.WalletService;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    private UUID transactionId;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
    }

    @Test
    void putMoney() throws Exception {
        when(walletService.putMoney(any(PutMoneyDto.class))).thenReturn(transactionId);

        String json = """
        {
            "amount": 100.0,
            "currency": "USD",
            "sender": "sender@example.com"
        }
        """;

        mockMvc.perform(post("/api/v1/wallets/put")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + transactionId.toString() + "\""));
    }

    @Test
    void getMoney() throws Exception {
        when(walletService.getMoney(any(GetMoneyDto.class))).thenReturn(transactionId);

        String json = """
        {
            "amount": 50.0,
            "currency": "USD",
            "receiver": "receiver@example.com"
        }
        """;

        mockMvc.perform(post("/api/v1/wallets/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + transactionId.toString() + "\""));
    }

    @Test
    void exchangeMoney() throws Exception {
        when(walletService.exchange(any(ExchangeMoneyDto.class))).thenReturn(transactionId);

        String json = """
        {
            "amount": 20.0,
            "currency": "EUR",
            "user": "user@example.com",
            "sourceCurrency": "USD"
        }
        """;

        mockMvc.perform(post("/api/v1/wallets/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + transactionId.toString() + "\""));
    }

    @Test
    void transferMoney() throws Exception {
        when(walletService.transfer(any(TransferMoneyDto.class))).thenReturn(transactionId);

        String json = """
        {
            "amount": 30.0,
            "currency": "USD",
            "sender": "sender@example.com",
            "receiver": "receiver@example.com"
        }
        """;

        mockMvc.perform(post("/api/v1/wallets/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + transactionId.toString() + "\""));
    }

    @Test
    void approveTransfer() throws Exception {
        when(walletService.transferApprove(any(TransferApproveDto.class))).thenReturn(transactionId);

        String json = """
        {
            "transactionId": "33333333-3333-3333-3333-333333333333",
            "email": "user@example.com",
            "code": "123456"
        }
        """;

        mockMvc.perform(post("/api/v1/wallets/transfer/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + transactionId.toString() + "\""));
    }

}
