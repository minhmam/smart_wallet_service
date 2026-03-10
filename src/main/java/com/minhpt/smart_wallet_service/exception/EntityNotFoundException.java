package com.minhpt.smart_wallet_service.exception;

import com.minhpt.smart_wallet_service.util.MessageUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.function.Supplier;

@Getter
@Setter
public class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String message;

    public EntityNotFoundException(Class<?> entity, Serializable id) {
        this.message = MessageUtils.getMessage("not.found.entity.by.id", entity.getSimpleName(), id);
    }

    public static Supplier<EntityNotFoundException> throwException(Class<?> clazz, Serializable id) {
        return () -> new EntityNotFoundException(clazz, id);
    }

}