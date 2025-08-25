package com.exchanger.model;

import com.exchanger.model.enums.Currency;
import com.exchanger.model.enums.TransactionStatus;
import com.exchanger.model.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Transaction extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "trans_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "trans_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "currency_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Currency currencyFrom;
    private BigDecimal amountFrom;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "currency_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Currency currencyTo;
    private BigDecimal amountTo;

    private String sender;
    private String receiver;
    private String comment;

    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

}
