package com.minhpt.smart_wallet_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "account_balance")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AccountBalance extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_balance_sequence")
    @SequenceGenerator(
            name = "account_balance_sequence",
            sequenceName = "account_balance_sequence",
            allocationSize = 1)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "balance")
    private Long balance;
}
