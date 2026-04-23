package com.se170395.orchid.aop;

import com.se170395.orchid.event.OrchidActionEmailEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    private final ApplicationEventPublisher eventPublisher;

    public AuditAspect(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Around("@annotation(audit)")
    public Object audit(ProceedingJoinPoint pjp, Audit audit) throws Throwable {
        String actor = currentActor();
        String signature = pjp.getSignature().toShortString();

        long start = System.currentTimeMillis();
        try {
            Object res = pjp.proceed();
            long ms = System.currentTimeMillis() - start;
            log.info("actor={} action={} method={} -> OK ({}ms)", actor, audit.action(), signature, ms);

            // Only after success: publish email notification event.
            if (isOrchidCreateOrDelete(audit.action()) && !"anonymousUser".equals(actor)) {
                eventPublisher.publishEvent(buildOrchidEmailEvent(pjp, actor, audit.action()));
            }

            return res;
        } catch (Throwable ex) {
            long ms = System.currentTimeMillis() - start;
            log.info("actor={} action={} method={} -> FAIL ({}ms) ex={}", actor, audit.action(), signature, ms,
                    ex.toString());
            throw ex;
        }
    }

    private boolean isOrchidCreateOrDelete(String action) {
        return "ORCHID_CREATE".equals(action) || "ORCHID_DELETE".equals(action);
    }

    private OrchidActionEmailEvent buildOrchidEmailEvent(ProceedingJoinPoint pjp, String actor, String action) {
        Object[] args = pjp.getArgs();

        String orchidId = null;
        String orchidName = null;

        if ("ORCHID_CREATE".equals(action) && args.length > 0 && args[0] instanceof pojo.Orchid o) {
            orchidId = o.getId();
            orchidName = o.getOrchidName();
        }

        if ("ORCHID_DELETE".equals(action) && args.length > 0 && args[0] instanceof String id) {
            orchidId = id;
        }

        return new OrchidActionEmailEvent(actor, action, orchidId, orchidName);
    }

    private String currentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth == null) ? "anonymous" : auth.getName();
    }
}

