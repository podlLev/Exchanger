package com.exchanger.model;

import com.exchanger.model.enums.Currency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Rate extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(unique = true, columnDefinition = "currency_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Currency currency;

    private BigDecimal sale;
    private BigDecimal buy;
    private Timestamp receive;

}
