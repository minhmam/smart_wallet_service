package com.minhpt.smart_wallet_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "")
    @SequenceGenerator(
            name = "verification_tokens_id_seq",
            sequenceName = "verification_tokens_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "expired_time")
    private LocalDateTime expiredTime;

    @Column(name = "used")
    private Integer used;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
