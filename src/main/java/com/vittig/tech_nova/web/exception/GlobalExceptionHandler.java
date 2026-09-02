package com.vittig.tech_nova.web.exception;

import com.vittig.tech_nova.data.dto.error.ErrorResponseDto;
import com.vittig.tech_nova.service.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleObjectNotFound(
            ObjectNotFoundException ex,
            HttpServletRequest request) {

        return buildResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidInput(
            InvalidInputException ex,
            HttpServletRequest request) {

        return buildResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidQuantity(
            InvalidQuantityException ex,
            HttpServletRequest request) {

        return buildResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request) {

        return buildResponse(ex, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorResponseDto> handleForbiddenOperation(
            ForbiddenOperationException ex,
            HttpServletRequest request) {

        return buildResponse(ex, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDto> handleConflict(
            ConflictException ex,
            HttpServletRequest request) {

        return buildResponse(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidStatus(
            InvalidStatusException ex,
            HttpServletRequest request) {

        return buildResponse(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponseDto> handleInsufficientBalance(
            InsufficientBalanceException ex,
            HttpServletRequest request) {

        return buildResponse(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> validationErrors = new HashMap<>();

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        validationErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setTimeStamp(LocalDateTime.now());
        errorResponseDto.setStatus(status.value());
        errorResponseDto.setError(status.getReasonPhrase());
        errorResponseDto.setMessage("Validation failed.");
        errorResponseDto.setPath(request.getRequestURI());
        errorResponseDto.setValidationErrors(validationErrors);

        return ResponseEntity
                .status(status)
                .body(errorResponseDto);
    }

    private ResponseEntity<ErrorResponseDto> buildResponse(
            RuntimeException ex,
            HttpServletRequest request,
            HttpStatus status) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setTimeStamp(LocalDateTime.now());
        errorResponseDto.setStatus(status.value());
        errorResponseDto.setError(status.getReasonPhrase());
        errorResponseDto.setMessage(ex.getMessage());
        errorResponseDto.setPath(request.getRequestURI());

        return ResponseEntity
                .status(status)
                .body(errorResponseDto);
    }
}