package com.app.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@ControllerAdvice
public class GlobalExceptionHandler implements ProblemHandling {
    // Kosong! Interface ProblemHandling sudah berisi puluhan 
    // default exception handler (seperti @ExceptionHandler(MethodArgumentNotValidException.class))
    // Anda bisa menambahkan custom exception mapping di sini jika diperlukan.
}
