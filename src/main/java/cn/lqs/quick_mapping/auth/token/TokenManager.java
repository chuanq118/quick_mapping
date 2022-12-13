package cn.lqs.quick_mapping.auth.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 2022/12/13
 */
@Slf4j
@Service
public class TokenManager {

    private final static long TWO_HOURS_TO_SECONDS = 2 * 3600;
    private final static long ONE_MONTH_TO_SECONDS = 30 * 24 * 3600;

    private volatile AuthToken inMemToken;

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public AuthToken createAuthToken(boolean isRemember) {
        rwLock.writeLock().lock();
        try {
            inMemToken = AuthToken.builder()
                    .key(UUID.randomUUID().toString().replaceAll("-", ""))
                    .expiredAt(isRemember ? System.currentTimeMillis() + ONE_MONTH_TO_SECONDS : System.currentTimeMillis() + TWO_HOURS_TO_SECONDS)
                    .createdAt(LocalDateTime.now())
                    .build();
            return inMemToken;
        }finally {
            rwLock.writeLock().unlock();
        }
    }

    public boolean validToken(String key) {
        if (inMemToken != null && StringUtils.hasText(key)) {
            if (inMemToken.getExpiredAt() > System.currentTimeMillis()) {
                return inMemToken.getKey().equals(key);
            }
        }
        return false;
    }

    public AuthToken currentToken() {
        return this.inMemToken;
    }

}
