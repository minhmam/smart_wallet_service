package com.minhpt.smart_wallet_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_notification_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_notification_token_seq")
    @SequenceGenerator(
            name = "user_notification_token_seq",
            sequenceName = "user_notification_token_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "fcm_token", nullable = false, columnDefinition = "TEXT")
    private String fcmToken;

    @Column(name = "platform")
    private String platform;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
}
