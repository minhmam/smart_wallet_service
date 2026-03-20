package com.minhpt.smart_wallet_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seb_payment_transactionsalter")
    @SequenceGenerator(
            name = "seb_payment_transactionsalter",
            sequenceName = "seb_payment_transactionsalter",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private SubcriptionPlan plan;

    @Column(name = "provider")
    private String provider;

    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "provider_ref")
    private String providerRef;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "state")
    private String state;

    @Column(name = "pay_url")
    private String payUrl;

    @Column(name = "raw_request")
    private String rawRequest;

    @Column(name = "raw_response")
    private String rawResponse;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
