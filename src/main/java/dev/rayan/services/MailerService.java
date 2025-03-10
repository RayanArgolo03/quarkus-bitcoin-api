package dev.rayan.services;


import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.apache.commons.text.StringSubstitutor;

import java.time.temporal.ChronoUnit;
import java.util.Map;

@RequestScoped
public final class MailerService {

    private static final String CONTENT_TEMPLATE = """
                <h2>This email should contains the link for a front-end ForgotPassword page. Request expired in ${expiredTime} ${timeUnit}!</h2>
            <br>
                <h3> Link below: if not works, copy this link and past in your navigator: http://localhost:8080${resourcePath}/update-forgot-password?code=${code}&email=${email}</h3>
            <br>
                <p>
                    Example: <a href="http://localhost:8080${resourcePath}/update-forgot-password?code=${code}&email=${email}">update forgot password here!</a>
                </p>
            """;

    public static final long EXPIRED_TIME = 3;
    public static final ChronoUnit TIME_UNIT = ChronoUnit.MINUTES;

    private static final String SUBJECT = "Forgot Quarkus Bitcoin Password";

    @Inject
    Mailer mailer;

    public void sendForgotPasswordEmail(final String resourcePath, final String email, final String code) {

        final Map<String, Object> params = Map.of(
                "resourcePath", resourcePath,
                "code", code,
                "email", email,
                "expiredTime", EXPIRED_TIME,
                "timeUnit", TIME_UNIT.name().toLowerCase()
        );

        final String content = StringSubstitutor.replace(CONTENT_TEMPLATE, params);
        mailer.send(Mail.withHtml(email, SUBJECT, content));
    }
}
