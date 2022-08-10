package com.gri.template.api.rest.handler;


import com.gri.template.impl.exception.CustomRuntimeException;
import com.gri.template.impl.exception.CustomValidationException;
import com.gri.template.model.error.CustomExceptionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class CustomRestExceptionHandler {

    @ExceptionHandler({CustomValidationException.class})
    ResponseEntity<ErrorResponse> handleValidationException(CustomValidationException exception) {
        log.error("Process exception", exception);
        ErrorResponse em = new ErrorResponse(exception.getErrorCode(), exception.getMessage(), exception.getParams(), exception.getData());
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        return ResponseEntity.status(status).body(em);
    }

    @ExceptionHandler({CustomRuntimeException.class})
    ResponseEntity<ErrorResponse> handleRuntimeException(CustomValidationException exception) {
        log.error("Process exception", exception);
        ErrorResponse em = new ErrorResponse(exception.getErrorCode(), exception.getMessage(), exception.getParams(), exception.getData());
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        return ResponseEntity.status(status).body(em);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException exc, HttpServletRequest request, HttpServletResponse response) {
        ErrorResponse em = new ErrorResponse("FF", "File too large!");
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        return ResponseEntity.status(status).body(em);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    ResponseEntity<List<ErrorResponse>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<ErrorResponse> messages = ex.getConstraintViolations().stream()
                .map(c -> c.getMessage())
                .map(code -> {
                    CustomExceptionMessage cem = CustomExceptionMessage.findByCode(code);
                    return  (cem == null)
                            ? new ErrorResponse("C-0000", code)
                            : new ErrorResponse(code, cem.message());

                })
                .collect(Collectors.toList());

        log.info("Constraint Violation Exception {}", messages);
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(messages);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    ResponseEntity<List<ErrorResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ErrorResponse> messages = ex.getAllErrors().stream()
                .map(c -> c.getDefaultMessage())
                .map(code -> {
                    CustomExceptionMessage cem = CustomExceptionMessage.findByCode(code);
                    return  (cem == null)
                            ? new ErrorResponse("C-0000", code)
                            : new ErrorResponse(code, cem.message());

                })
                .collect(Collectors.toList());
        log.info("Constraint Violation Exception {}", messages);
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(messages);
    }


}
