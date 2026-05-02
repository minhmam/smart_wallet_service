package com.minhpt.smart_wallet_service.model;

import com.minhpt.smart_wallet_service.constant.Constant;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "status")
    private int status;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = Constant.NOT_DELETE;
        if (this.createdBy == null) {
            this.createdBy = resolveCurrentUsername();
        }
        if (this.updatedBy == null) {
            this.updatedBy = resolveCurrentUsername();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = resolveCurrentUsername();
    }

    private String resolveCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                Object principal = authentication.getPrincipal();

                if (principal instanceof User) {
                    User userDetails = (User) principal;
                    return userDetails.getUsername();
                }
            }
        } catch (Exception ignored) {
        }
        return Constant.USER_DEFAULT;
    }
}
