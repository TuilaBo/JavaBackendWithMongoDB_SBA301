package com.se170395.orchid.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logRestController(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = currentRequest();
        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            long ms = System.currentTimeMillis() - start;
            log.info("actor={} {} {} -> OK ({}ms) sig={}",
                    currentActor(),
                    requestMethod(request),
                    requestUri(request),
                    ms,
                    pjp.getSignature().toShortString());
            return result;
        } catch (Throwable ex) {
            long ms = System.currentTimeMillis() - start;
            log.info("actor={} {} {} -> FAIL ({}ms) sig={} ex={}",
                    currentActor(),
                    requestMethod(request),
                    requestUri(request),
                    ms,
                    pjp.getSignature().toShortString(),
                    ex.toString());
            throw ex;
        }
    }

    private HttpServletRequest currentRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            return sra.getRequest();
        }
        return null;
    }

    private String currentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth == null) ? "anonymous" : auth.getName();
    }

    private String requestMethod(HttpServletRequest request) {
        return (request == null) ? "n/a" : request.getMethod();
    }

    private String requestUri(HttpServletRequest request) {
        return (request == null) ? "n/a" : request.getRequestURI();
    }
}

