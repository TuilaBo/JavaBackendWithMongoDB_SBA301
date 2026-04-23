package service;

import com.se170395.orchid.event.OrchidActionEmailEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${orchid.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${orchid.mail.from:}")
    private String mailFrom;

    @Value("${spring.mail.username:}")
    private String springMailUsername;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrchidActionEmail(OrchidActionEmailEvent event) {
        if (!mailEnabled) {
            log.debug("Email disabled (orchid.mail.enabled=false). action={}", event.getAction());
            return;
        }

        String to = event.getActorEmail();
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("actorEmail is blank");
        }

        String from = (mailFrom != null && !mailFrom.isBlank()) ? mailFrom : springMailUsername;
        if (from == null || from.isBlank()) {
            throw new IllegalStateException("Missing mail sender. Set orchid.mail.from or spring.mail.username");
        }

        String subject = buildSubject(event);
        String text = buildBody(event);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);

        log.info("Sent orchid action email to={} action={}", to, event.getAction());
    }

    private String buildSubject(OrchidActionEmailEvent event) {
        return switch (event.getAction()) {
            case "ORCHID_CREATE" -> "Orchid created: " + safe(event.getOrchidName());
            case "ORCHID_DELETE" -> "Orchid deleted: " + safe(event.getOrchidId());
            default -> "Orchid notification: " + event.getAction();
        };
    }

    private String buildBody(OrchidActionEmailEvent event) {
        return "Hello,\n\n" +
                "Your orchid action has been processed.\n" +
                "Action: " + event.getAction() + "\n" +
                "OrchidId: " + safe(event.getOrchidId()) + "\n" +
                "OrchidName: " + safe(event.getOrchidName()) + "\n" +
                "Time: " + event.getOccurredAt() + "\n\n" +
                "Regards,\nOrchid API";
    }

    private String safe(String v) {
        return (v == null) ? "-" : v;
    }
}

