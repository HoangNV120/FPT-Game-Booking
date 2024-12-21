package com.fptgamebookingbe.exception;

import com.fptgamebookingbe.dto.Response.ApiResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

  @ExceptionHandler(value = AppException.class)
  ResponseEntity<ApiResponse> appExceptionHandler(AppException e) {
    ErrorCode errorCode = e.getErrorCode();
    return ResponseEntity.badRequest().body(
        ApiResponse.builder()
            .message(errorCode.getMessage())
            .build()

    );
  }
}
