/*
 * Notes service.
 */
package com.example.notes.exception;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Translation of web-layer exceptions into problem responses.
 *
 * @since 1.0
 */
public interface ExceptionHandlers {

    /**
     * Translate a missing note.
     * @param error Error to translate
     * @return Problem response
     */
    ResponseEntity<ProblemDetail> notFound(NoteNotFoundException error);

    /**
     * Translate a validation failure.
     * @param error Error to translate
     * @return Problem response
     */
    ResponseEntity<ProblemDetail> invalid(MethodArgumentNotValidException error);

    /**
     * Translate a parameter type mismatch.
     * @param error Error to translate
     * @return Problem response
     */
    ResponseEntity<ProblemDetail> mismatch(MethodArgumentTypeMismatchException error);

    /**
     * Translate an unreadable request body.
     * @param error Error to translate
     * @return Problem response
     */
    ResponseEntity<ProblemDetail> malformed(HttpMessageNotReadableException error);
}
