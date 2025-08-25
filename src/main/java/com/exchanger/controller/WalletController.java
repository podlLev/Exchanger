package com.exchanger.controller;

import com.exchanger.dto.*;
import com.exchanger.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @version 0.0.1
 */
@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/put")
    public UUID put(@Valid @RequestBody PutMoneyDto putMoneyDto) {
        log.info("Run method WalletController.put");
        return walletService.putMoney(putMoneyDto);
    }

    @PostMapping("/get")
    public UUID getMoney(@Valid @RequestBody GetMoneyDto getMoneyDto) {
        log.info("Run method WalletController.getMoney");
        return walletService.getMoney(getMoneyDto);
    }

    @PostMapping("/exchange")
    public UUID exchange(@Valid @RequestBody ExchangeMoneyDto exchangeDto) {
        log.info("Run method WalletController.exchange");
        return walletService.exchange(exchangeDto);
    }

    @PostMapping("/transfer")
    public UUID transfer(@Valid @RequestBody TransferMoneyDto transferDto) {
        log.info("Run method WalletController.transfer");
        return walletService.transfer(transferDto);
    }

    @PostMapping("/transfer/approve")
    public UUID transferApprove(@Valid @RequestBody TransferApproveDto transferDto) {
        log.info("Run method WalletController.transferApprove");
        return walletService.transferApprove(transferDto);
    }

}
