package com.exchanger.repository;

import com.exchanger.model.User;
import com.exchanger.model.Wallet;
import com.exchanger.model.enums.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByUserAndCurrency(User user, Currency currency);

}
