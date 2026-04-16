package com.se170395.orchid.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import service.EmailNotificationService;

@Component
public class OrchidActionEmailListener {

    private static final Logger log = LoggerFactory.getLogger(OrchidActionEmailListener.class);

    private final EmailNotificationService emailNotificationService;

    public OrchidActionEmailListener(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrchidActionEmailEvent event) {
        log.info("OrchidActionEmailListener received action={} actor={}", event.getAction(), event.getActorEmail());
        emailNotificationService.sendOrchidActionEmail(event);
    }
}

