package com.fptgamebookingbe.service.impl;

import com.fptgamebookingbe.entity.PasswordResetToken;
import com.fptgamebookingbe.repository.PasswordResetTokenRepository;
import com.fptgamebookingbe.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {


    private final PasswordResetTokenRepository passwordResetTokenRepository;

    // This method will run every 5 minutes (300000 ms)
    @Scheduled(fixedRate = 300000)
    public void cleanExpiredTokens() {
        // Get the current time
        LocalDateTime now = LocalDateTime.now();

        // Find tokens that are expired (older than 24 hours) or already used
        List<PasswordResetToken> expiredTokens = passwordResetTokenRepository
                .findByCreatedAtBeforeAndIsUsedIsTrue(now.minusHours(24));

        // Delete the expired or used tokens
        if (!expiredTokens.isEmpty()) {
            passwordResetTokenRepository.deleteAll(expiredTokens);
            System.out.println("Deleted expired or used tokens.");
        } else {
            System.out.println("No tokens to delete.");
        }
    }
}
