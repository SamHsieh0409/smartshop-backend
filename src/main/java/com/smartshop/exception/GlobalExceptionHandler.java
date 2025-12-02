package com.smartshop.exception;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.smartshop.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        
        if ("尚未登入，請先登入後再操作".equals(ex.getMessage())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(401, ex.getMessage(), null));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, ex.getMessage(), null));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(404, ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(500, "系統發生錯誤", null));
    }
}
