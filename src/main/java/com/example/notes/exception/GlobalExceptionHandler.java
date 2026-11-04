/*
 * Notes service.
 */
package com.example.notes.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Translates exceptions thrown by the web layer into problem responses.
 *
 * @since 1.0
 */
@RestControllerAdvice
public final class GlobalExceptionHandler implements ExceptionHandlers {

    @Override
    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ProblemDetail> notFound(final NoteNotFoundException error) {
        final ProblemDetail problem = GlobalExceptionHandler.problem(
            HttpStatus.NOT_FOUND, "Note not found", error.getMessage()
        );
        problem.setProperty("noteId", error.identifier());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @Override
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> invalid(final MethodArgumentNotValidException error) {
        final ProblemDetail problem = GlobalExceptionHandler.problem(
            HttpStatus.BAD_REQUEST, "Validation failed",
            "The request body contains invalid fields"
        );
        final Map<String, String> errors = new LinkedHashMap<>();
        error.getBindingResult().getFieldErrors().forEach(
            item -> errors.putIfAbsent(item.getField(), item.getDefaultMessage())
        );
        problem.setProperty("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @Override
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> mismatch(final MethodArgumentTypeMismatchException error) {
        final ProblemDetail problem = GlobalExceptionHandler.problem(
            HttpStatus.BAD_REQUEST, "Invalid request parameter",
            String.format(
                "Parameter '%s' has an invalid value: %s",
                error.getName(), error.getValue()
            )
        );
        problem.setProperty("parameter", error.getName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @Override
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> malformed(final HttpMessageNotReadableException error) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            GlobalExceptionHandler.problem(
                HttpStatus.BAD_REQUEST,
                "Malformed request body",
                "The request body could not be read"
            )
        );
    }

    /**
     * Create a problem detail.
     * @param status HTTP status
     * @param title Problem title
     * @param detail Problem detail
     * @return Problem detail
     */
    private static ProblemDetail problem(final HttpStatus status, final String title,
        final String detail) {
        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }
}
