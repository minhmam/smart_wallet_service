package com.minhpt.smart_wallet_service.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.common.ValidationError;
import com.minhpt.smart_wallet_service.exception.CustomBusinessException;
import com.minhpt.smart_wallet_service.util.ExceptionUtil;
import com.minhpt.smart_wallet_service.util.MessageUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

@ControllerAdvice
@Slf4j
public class ExceptionHandlingController {


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(SecurityException.class)
    @ResponseBody
    protected ResponseEntity<Object> handleException(SecurityException ex) {
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ExceptionHandlingResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), ex));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    protected ResponseEntity<Object> handleBadRequestIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponseEntity(new ExceptionHandlingResponse(HttpStatus.BAD_REQUEST.value(), MessageUtils.getMessage(ex.getMessage()), ex));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalStateException.class, TimeoutException.class, LockAcquisitionException.class, SocketTimeoutException.class, InterruptedException.class})
    @ResponseBody
    protected ResponseEntity<Object> handleTimeOutException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ExceptionHandlingResponse(HttpStatus.BAD_REQUEST.value(), "ERROR", ex));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    @ResponseBody
    protected ResponseEntity<Object> handleSQLException(SQLException ex) {
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ExceptionHandlingResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), ex));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    protected ResponseEntity<Object> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ExceptionHandlingResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), ex));
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    protected ResponseEntity<Object> handleBadRequestIllegalArgumentException(NoSuchElementException ex) {
        return buildResponseEntity(new ExceptionHandlingResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), ex));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    protected ResponseEntity<Object> handleBadRequestEntityNotFoundException(EntityNotFoundException ex) {
        return buildResponseEntity(new ExceptionHandlingResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), ex));
    }

    private ResponseEntity<Object> buildResponseEntity(ExceptionHandlingResponse exceptionHandlingResponse) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(exceptionHandlingResponse.getCode())
                .message(exceptionHandlingResponse.getMessage())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomBusinessException.class)
    @ResponseBody
    protected ResponseEntity<ApiResponse> handleBadRequestEntityNotFoundException(CustomBusinessException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(ex.getCode())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected ResponseEntity<ApiResponse> onConstraintValidationException(
            ConstraintViolationException e) {

//        ValidationError error = new ValidationError();
//
//        for (ConstraintViolation<?> violation : e.get()) {
//            error.getValidateDetails()
//                    .put(violation.getPropertyPath().toString(), violation.getMessage());
//        }

        ApiResponse apiResponse = ApiResponse.builder()
                .status(ExceptionUtil.getCodeError(Constant.CODE.VALIDATION_ERROR))
                .message(ExceptionUtil.getMessageError(Constant.CODE.VALIDATION_ERROR))
                .data("pending")
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected ResponseEntity<ApiResponse> onConstraintValidationException(MethodArgumentNotValidException e) {

        BindingResult result = e.getBindingResult();
        ValidationError error = new ValidationError();
        for (FieldError violation : result.getFieldErrors()) {
            error.getValidateDetails().put(violation.getField(), violation.getDefaultMessage());
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .status(ExceptionUtil.getCodeError(Constant.CODE.VALIDATION_ERROR))
                .message(ExceptionUtil.getMessageError(Constant.CODE.VALIDATION_ERROR))
                .data(error)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }


    @Data
    public static class ExceptionHandlingResponse {
        private int code;
        private Object data;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        private LocalDateTime timestamp;
        private String message;
        private String debugMessage;

        private ExceptionHandlingResponse() {
            timestamp = LocalDateTime.now();
        }

        ExceptionHandlingResponse(int code) {
            this();
            this.code = code;
        }

        ExceptionHandlingResponse(int code, Throwable ex) {
            this();
            this.code = code;
            this.message = "Unexpected error";
            this.debugMessage = ex.toString();
        }

        public ExceptionHandlingResponse(int code, Object data, String message) {
            this();
            this.code = code;
            this.data = data;
            this.message = message;
        }

        ExceptionHandlingResponse(int code, String message, Throwable ex) {
            this();
            this.code = code;
            this.message = message;
            this.debugMessage = ex.toString();
        }
    }
}

