package com.example.notes.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Contract skeleton for centralized REST error handling. Handlers returning
 * {@code ProblemDetail} are delivered by the implementer through a TDD cycle.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
}
