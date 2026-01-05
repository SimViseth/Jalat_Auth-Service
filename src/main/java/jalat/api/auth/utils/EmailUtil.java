package jalat.api.auth.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
@AllArgsConstructor
public class EmailUtil {

    private JavaMailSender javaMailSender;
    private SpringTemplateEngine templateEngine;

    public void sendOtpEmail(String email, String otpCode) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Verify OTP");

        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("otpCode", otpCode);

        String htmlContent = templateEngine.process("otp-email-template", thymeleafContext);

        mimeMessageHelper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }
}
