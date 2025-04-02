package dev.rayan.services;


import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.apache.commons.text.StringSubstitutor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Map;

@RequestScoped
public final class MailerService {

    private static final String FORGOT_HTML_TEMPLATE = """
                <h2>This email should contains the link for a front-end ForgotPassword page. Request expired in ${expiredTime} ${timeUnit}s!</h2>
            <br>
                <h3> Link below: if not works, copy this link and past in your navigator: http://localhost:8080${resourcePath}/update-forgot-password?code=${code}&email=${email}</h3>
            <br>
                <p>
                    Example: <a href="http://localhost:8080${resourcePath}/update-forgot-password?code=${code}&email=${email}">update forgot password here!</a>
                </p>
            """;

    private static final String FORGOT_SUBJECT = "Forgot Quarkus Bitcoin Password";

    @ConfigProperty(name = "expired-time")
    String expiredTime;

    @ConfigProperty(name = "time-unit")
    String timeUnit;

    @Inject
    Mailer mailer;

    @Inject
    Logger log;

    public void sendForgotPasswordEmail(final String resourcePath, final String email, final String code) {

        final Map<String, Object> params = Map.of(
                "resourcePath", resourcePath,
                "code", code,
                "email", email,
                "expiredTime", expiredTime,
                "timeUnit", timeUnit
        );

        final String html = StringSubstitutor.replace(FORGOT_HTML_TEMPLATE, params);
        final Mail mail = Mail.withHtml(email, FORGOT_SUBJECT, html);

        mailer.send(mail);
    }

    public void sendDeletedEmail(final String email) {

        final String subject = "Deleted Quarkus Bitcoin Account";
        final String html = """
                <h1>It was a pleasure to have you with us, give a star in Github!</h1>
                <h2 style="color: red; font-family: Arial, sans-serif;">
                     Quarkus-Bitcoin by Rayan Argolo
                </h2>
                 """;


        final Mail mail = Mail.withHtml(email, subject, html);

        mailer.send(mail);
    }
}
