package com.example.logging.logic;

import java.util.concurrent.CompletableFuture;

public interface LoggingService {

    CompletableFuture<Void> whoop();
}
