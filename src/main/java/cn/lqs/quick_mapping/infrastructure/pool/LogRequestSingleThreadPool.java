package cn.lqs.quick_mapping.infrastructure.pool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author @lqs
 */
@Component
public class LogRequestSingleThreadPool extends ThreadPoolExecutor {

    @Autowired
    public LogRequestSingleThreadPool() {
        super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "log-thread-" + counter.getAndIncrement());
            }
        }, new DiscardOldestPolicy());
    }


}
