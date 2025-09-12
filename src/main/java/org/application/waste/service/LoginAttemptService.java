package org.application.waste.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final int LOCK_TIME_MINUTES = 10;

    private int failedAttempts = 0;
    private LocalDateTime lockTime;

    public void loginFailed() {
        failedAttempts++;
        if (failedAttempts >= MAX_ATTEMPTS) {
            lockTime = LocalDateTime.now().plusMinutes(LOCK_TIME_MINUTES);
        }
    }

    public boolean isBlocked() {
        if (lockTime == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(lockTime)) {
            resetAttempts();
            return false;
        }
        return true;
    }

    public void loginSucceeded() {
        resetAttempts();
    }

    private void resetAttempts() {
        failedAttempts = 0;
        lockTime = null;
    }

    public long getMillisLeft() {
        if (lockTime == null) return 0;
        long millisLeft = java.time.Duration.between(LocalDateTime.now(), lockTime).toMillis();
        return millisLeft > 0 ? millisLeft : 0;
    }
}
