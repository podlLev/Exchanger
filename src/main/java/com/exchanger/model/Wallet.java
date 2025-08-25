package com.exchanger.model;

import com.exchanger.model.enums.Currency;
import jakarta.persistence.*;
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
public class Wallet extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "currency_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Currency currency;

    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
