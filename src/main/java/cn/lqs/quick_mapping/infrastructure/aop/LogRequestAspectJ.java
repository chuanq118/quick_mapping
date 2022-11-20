package cn.lqs.quick_mapping.infrastructure.aop;

import cn.lqs.quick_mapping.infrastructure.annotations.LogRequest;
import cn.lqs.quick_mapping.infrastructure.pool.LogRequestSingleThreadPool;
import cn.lqs.quick_mapping.infrastructure.util.LogMarkers;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.lang.reflect.Method;

/**
 * 横向逻辑,将打印请求日志的操作交给我们单独的一个线程执行.
 * @author @lqs
 */
@Aspect
@Component
public class LogRequestAspectJ {

    private final static Logger log = LoggerFactory.getLogger("log-request");

    private final LogRequestSingleThreadPool pool;

    @Autowired
    public LogRequestAspectJ(LogRequestSingleThreadPool pool) {
        this.pool = pool;
    }

    @Pointcut("@annotation(cn.lqs.quick_mapping.infrastructure.annotations.LogRequest)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void doLog(JoinPoint jp) {
        pool.execute(()->{
            MethodSignature methodSignature = (MethodSignature) jp.getSignature();
            Method method = methodSignature.getMethod();

            log.warn("doLog need tests...");
            ServerRequest request = ServerRequest.class.cast(method.getParameters()[0]);
            log.info("{}", request == null);
            // InetSocketAddress clientAddr = request == null ? null : request.remoteAddress().orElse(null);
            LogRequest logRequestAnno = method.getAnnotation(LogRequest.class);
            log.info(LogMarkers.PLAIN, "request {}. comment: {}", logRequestAnno.path(), logRequestAnno.comment());
        });
    }
}
