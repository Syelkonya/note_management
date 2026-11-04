/*
 * Notes service.
 */
package com.example.notes;

import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Notes service application.
 *
 * @since 1.0
 */
@SpringBootApplication(proxyBeanMethods = false)
public final class NotesServiceApplication {

    /** Timestamp source. */
    private final Clock time = Clock.systemUTC();

    /**
     * Application entry point.
     * @param args Command-line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(NotesServiceApplication.class, args);
    }

    /**
     * Clock used for server-owned timestamps.
     * @return UTC clock
     */
    @Bean
    Clock clock() {
        return this.time;
    }
}
