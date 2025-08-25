package com.exchanger.model;

import com.exchanger.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true)
    private String email;

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "user_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private UserStatus status;

    private Long telegramChatId;

}
