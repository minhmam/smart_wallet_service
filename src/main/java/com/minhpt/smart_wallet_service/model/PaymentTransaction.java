package com.minhpt.smart_wallet_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction extends BaseEntity {

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
    private SubscriptionPlan subscriptionPlan;

    @Column(name = "provider")
    private String provider;

    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "provider_ref")
    private String providerRef;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "state")
    private String state;

    @Column(name = "pay_url", columnDefinition = "TEXT")
    private String payUrl;

    @Column(name = "raw_request", columnDefinition = "TEXT")
    private String rawRequest;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "deeplink")
    private String deeplink;

    @Column(name = "qr_code_url")
    private String qrCodeUrl;
}
