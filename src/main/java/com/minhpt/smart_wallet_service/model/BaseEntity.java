package com.minhpt.smart_wallet_service.model;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.config.SpringContextHolder;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

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

        AuthenticationUtil authUtil = SpringContextHolder.getBean(AuthenticationUtil.class);
        String username = authUtil.getCurrentUser().getUsername();
        this.setCreatedBy(username != null ? username : Constant.USER_DEFAULT);
        this.setUpdatedBy(username != null ? username : Constant.USER_DEFAULT);

        this.status = 1;
    }

    @PreUpdate
    public void preUpdate() {

        this.updatedAt = LocalDateTime.now();

        AuthenticationUtil authUtil = SpringContextHolder.getBean(AuthenticationUtil.class);
        String username = authUtil.getCurrentUser().getUsername();
        this.setUpdatedBy(username != null ? username : Constant.USER_DEFAULT);
    }
}