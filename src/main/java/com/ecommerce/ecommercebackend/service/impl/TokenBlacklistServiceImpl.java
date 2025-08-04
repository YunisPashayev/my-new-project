package com.ecommerce.ecommercebackend.service.impl;

import com.ecommerce.ecommercebackend.service.TokenBlacklistService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final ConcurrentMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    @Override
    public void blacklistToken(String token) {
        long expirationTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
        blacklistedTokens.put(token, expirationTime);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        Long expirationTime = blacklistedTokens.get(token);
        if (expirationTime == null) {
            return false;
        }
        
        if (System.currentTimeMillis() > expirationTime) {
            blacklistedTokens.remove(token);
            return false;
        }
        
        return true;
    }

    @Override
    public void cleanupExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        blacklistedTokens.entrySet().removeIf(entry -> currentTime > entry.getValue());
    }
}