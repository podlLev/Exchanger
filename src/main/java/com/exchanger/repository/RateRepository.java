package com.exchanger.repository;

import com.exchanger.model.Rate;
import com.exchanger.model.enums.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RateRepository extends JpaRepository<Rate, UUID> {

    Optional<Rate> findByCurrency(Currency currency);

}
