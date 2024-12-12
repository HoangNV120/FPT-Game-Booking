package com.fptgamebookingbe.service;

public interface ScheduleService {
    /**
     * Cleans up expired tokens from the system.
     */
    void cleanExpiredTokens();

    /**
     * Cleans up expired tokens from the system.
     * This method might be used for additional cleanup logic.
     */
    void cleanUpExpiredTokens();
}
