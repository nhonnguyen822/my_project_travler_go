package com.example.tourtravelserver.service.email;

import com.example.tourtravelserver.entity.Booking;
import com.example.tourtravelserver.entity.Tour;
import com.example.tourtravelserver.service.IMailService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService implements IMailService {
    @Value("${app.frontend.url}")
    private String frontendUrl;
    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendUserVerificationEmail(String to, String name, String token) throws MessagingException {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("verificationUrl", frontendUrl + "/verification?token=" + token);
//            context.setVariable("verificationUrl",
//                    "http://localhost:8080/api/auth/email-verification?token=" + token);

            String htmlContent = templateEngine.process("verification-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Auto Marketing System");
            helper.setTo(to);
            helper.setSubject("Yêu cầu xác nhận đăng ký tài khoản");
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Lỗi encoding khi gửi email", e);
        }
    }

    @Override
    public void sendBookingConfirmation(Booking booking, String txnRef) throws Exception {
        Tour tour = booking.getTourSchedule().getTour();

        // 1️⃣ Tạo QR
        String qrDataUrl = generateQrDataUrl(frontendUrl + "/booking/" + booking.getId());

        // 2️⃣ Build HTML ticket
        Context ctx = new Context();
        ctx.setVariable("booking", booking);
        ctx.setVariable("tour", tour);
        ctx.setVariable("schedule", booking.getTourSchedule());
        ctx.setVariable("txnRef", txnRef);
        ctx.setVariable("qrDataUrl", qrDataUrl);
        ctx.setVariable("itineraryDays", buildItineraryDays(booking));

        String htmlContent = templateEngine.process("ticket", ctx);

        // 3️⃣ Convert HTML → PDF
        byte[] pdfBytes = convertHtmlToPdf(htmlContent);

        // 4️⃣ Gửi email
        sendEmailWithAttachment(booking.getUser().getEmail(), booking, pdfBytes);
    }
    private void sendEmailWithAttachment(String to, Booking booking, byte[] pdfBytes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        helper.setTo(to);
        helper.setSubject("Vé điện tử - " + booking.getTourSchedule().getTour().getTitle());

        String htmlBody = """
                <p>Chào %s,</p>
                <p>Cảm ơn bạn đã đặt tour <strong>%s</strong>. Đính kèm là vé điện tử và lộ trình chi tiết của bạn.</p>
                <p>Chúc bạn có chuyến đi vui vẻ!</p>
                """.formatted(booking.getUser().getName(), booking.getTourSchedule().getTour().getTitle());

        helper.setText(htmlBody, true);
        helper.addAttachment("booking_" + booking.getId() + ".pdf", new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }

    private byte[] convertHtmlToPdf(String html) throws Exception {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);

            // Load font đúng cách cho tiếng Việt
            builder.useFont(() -> getClass().getResourceAsStream("/fonts/DejaVuSans.ttf"), "DejaVu Sans");

            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        }
    }


    private String generateQrDataUrl(String text) {
        try {
            int size = 200; // kích thước QR (px)
            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size);

            ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", pngOutput);

            String base64 = Base64.getEncoder().encodeToString(pngOutput.toByteArray());
            return "data:image/png;base64," + base64;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private List<Map<String, Object>> buildItineraryDays(Booking booking) {
        try {
            var tour = booking.getTourSchedule().getTour();
            var days = tour.getItineraryDays();

            if (days != null && !days.isEmpty()) {
                return days.stream().map(day -> Map.<String, Object>of(
                        "dayIndex", day.getDayIndex(),
                        "title", day.getTitle(),
                        "description", day.getDescription(),
                        "activities", day.getActivities().stream().map(act -> Map.of(
                                "title", act.getTitle(),
                                "details", act.getDetails(),
                                "time", act.getTime(),
                                "imageUrl", act.getImageUrl()
                        )).toList()
                )).toList();
            }
        } catch (Exception ignored) {
        }

        // fallback demo nếu tour chưa có lộ trình
        return List.of(
                Map.of(
                        "dayIndex", 1,
                        "title", "Khởi hành & Tham quan",
                        "description", "Đón khách và bắt đầu hành trình tham quan Sa Pa"
                ),
                Map.of(
                        "dayIndex", 2,
                        "title", "Khám phá bản Cát Cát",
                        "description", "Tham quan và tìm hiểu văn hóa của người H'Mông"
                )
        );
    }
}
