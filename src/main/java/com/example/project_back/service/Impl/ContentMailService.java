package com.example.project_back.service.Impl;

import com.example.project_back.entity.TableReservations;
import com.example.project_back.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ContentMailService {

    private final MailService mailService;

    private static final String RESTAURANT_NAME = "JLer SKY Retaurent";

    // Tông màu thương hiệu: vàng nhạt
    private static final String GOLD_LIGHT = "#fdf6e3";
    private static final String GOLD_MID = "#f3dfa6";
    private static final String GOLD_DEEP = "#c8a24d";
    private static final String GOLD_TEXT = "#7a5c1e";

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

    /** Định dạng thời gian dạng giờ:phút:giây ngày/tháng/năm cho dễ đọc. */
    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DATETIME_FORMATTER);
    }

    // ============================================================
    //  SHARED TEMPLATE ENGINE
    //  Mọi email đều dùng chung 1 khung giao diện để đồng bộ
    //  thương hiệu, chỉ khác màu accent, icon, tiêu đề và nội dung.
    // ============================================================

    /**
     * Bọc nội dung email vào 1 layout chung: header gradient + card + footer.
     *
     * @param accentColor màu chủ đạo (hex) cho header & điểm nhấn
     * @param accentSoft  màu nền nhạt tương ứng dùng cho badge/nhấn nhẹ
     * @param emoji       icon hiển thị cạnh tiêu đề header
     * @param headerTitle tiêu đề lớn trong header
     * @param bodyHtml    nội dung HTML chính (đã format sẵn)
     */
    private String wrapEmail(String accentColor, String accentSoft, String emoji,
                             String headerTitle, String bodyHtml) {
        return """
                <html>
                <body style="margin:0;padding:0;background:#faf3e0;font-family:'Segoe UI',Arial,sans-serif;">
                  <div style="max-width:620px;margin:32px auto;background:#ffffff;
                              border-radius:16px;overflow:hidden;
                              box-shadow:0 8px 24px rgba(200,162,77,.18);">

                    <!-- Accent bar (báo hiệu loại thông báo) -->
                    <div style="height:6px;background:%s;"></div>

                    <!-- Header vàng nhạt -->
                    <div style="background:linear-gradient(135deg, %s 0%%, %s 100%%);
                                padding:36px 30px;text-align:center;">
                      <div style="font-size:13px;letter-spacing:3px;color:%s99;
                                  text-transform:uppercase;margin-bottom:8px;">
                        %s
                      </div>
                      <div style="font-size:26px;font-weight:700;color:%s;">
                        %s %s
                      </div>
                    </div>

                    <!-- Body -->
                    <div style="padding:34px 32px;color:#2c3e50;font-size:15px;line-height:1.7;">
                      %s
                    </div>

                    <!-- Footer -->
                    <div style="background:%s;padding:22px 32px;text-align:center;
                                border-top:1px solid #f0e2b8;">
                      <div style="font-size:14px;font-weight:700;color:%s;">
                        %s
                      </div>
                      <div style="font-size:12px;color:#b39a5c;margin-top:6px;">
                        Đây là email tự động, vui lòng không phản hồi trực tiếp.
                      </div>
                    </div>

                  </div>
                </body>
                </html>
                """.formatted(
                accentColor,
                GOLD_LIGHT, GOLD_MID,
                GOLD_TEXT,
                RESTAURANT_NAME,
                GOLD_TEXT, emoji, headerTitle,
                bodyHtml,
                GOLD_LIGHT,
                GOLD_TEXT, RESTAURANT_NAME
        );
    }

    /** Ô thông tin dạng key-value, dùng table để hiển thị đúng trên mọi email client. */
    private String infoRow(String label, String value) {
        return """
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
                       style="border-bottom:1px solid #f0e2b8;">
                  <tr>
                    <td style="padding:12px 16px;color:#a58b52;">%s</td>
                    <td style="padding:12px 16px;font-weight:600;color:#2c3e50;text-align:right;">%s</td>
                  </tr>
                </table>
                """.formatted(label, value);
    }

    /** Dòng trạng thái (label + badge), dùng table thay vì flex để tránh bị dính chữ. */
    private String statusRow(String label, String badgeHtml) {
        return """
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0">
                  <tr>
                    <td style="padding:12px 16px;color:#a58b52;">%s</td>
                    <td style="padding:12px 16px;text-align:right;">%s</td>
                  </tr>
                </table>
                """.formatted(label, badgeHtml);
    }

    /** Badge trạng thái nhỏ, màu theo accent. */
    private String statusBadge(String text, String color, String soft) {
        return """
                <span style="display:inline-block;padding:5px 14px;border-radius:20px;
                             background:%s;color:%s;font-weight:700;font-size:13px;">
                  %s
                </span>
                """.formatted(soft, color, text);
    }

    // ================= ĐẶT BÀN ĂN =================

    // Đặt bàn thành công
    public void sendBookingSuccess(TableReservations reservation) {

        String info = """
                <div style="background:#fdf8ea;border-radius:12px;overflow:hidden;margin:20px 0;">
                  %s%s%s%s%s
                </div>
                """.formatted(
                infoRow("Mã đặt bàn", reservation.getReservationCode()),
                infoRow("Bàn số", String.valueOf(reservation.getTable().getTableNumber())),
                infoRow("Sức chứa", reservation.getTable().getCapacity() + " người"),
                infoRow("Thời gian", formatDateTime(reservation.getReservationTime())),
                statusRow("Trạng thái", statusBadge("Đang chờ xác nhận", "#e67e22", "#fdf1e6"))
        );

        String body = """
                <p>Xin chào <b>%s</b>,</p>
                <p>Nhà hàng đã tiếp nhận yêu cầu đặt bàn của bạn.</p>
                %s
                <p>Chúng tôi sẽ xác nhận đơn đặt bàn trong thời gian sớm nhất.</p>
                <p style="color:#95a5a6;">Cảm ơn bạn đã lựa chọn %s ❤️</p>
                """.formatted(reservation.getCustomerName(), info, RESTAURANT_NAME);

        String html = wrapEmail("#27ae60", "#1e8449", "🍽", "Đặt bàn thành công", body);

        mailService.sendHtmlEmail(
                reservation.getCustomerEmail(),
                "Đặt bàn thành công - " + RESTAURANT_NAME,
                html
        );
    }

    // Xác nhận đặt bàn
    public void sendConfirmed(TableReservations reservation) {

        String body = """
                <p>Xin chào <b>%s</b>,</p>
                <p>Nhà hàng đã <b>xác nhận</b> đơn đặt bàn của bạn.</p>
                <div style="background:#fdf8ea;border-radius:12px;margin:20px 0;">
                  %s
                </div>
                <p>Chúng tôi rất mong được phục vụ bạn.</p>
                """.formatted(
                reservation.getCustomerName(),
                infoRow("Mã đặt bàn", reservation.getReservationCode())
        );

        String html = wrapEmail("#2ecc71", "#229954", "✅", "Đơn đặt bàn đã xác nhận", body);

        mailService.sendHtmlEmail(
                reservation.getCustomerEmail(),
                "Đơn đặt bàn đã được xác nhận - " + RESTAURANT_NAME,
                html
        );
    }

    // Check in
    public void sendCheckIn(TableReservations reservation) {

        String body = """
                <p>Xin chào <b>%s</b>,</p>
                <p>Bạn đã <b>check-in thành công</b>.</p>
                <p>Nhà hàng sẽ giữ bàn của bạn trong vòng <b>30 phút</b>.</p>
                <p style="color:#95a5a6;">Chúc bạn có một bữa ăn ngon miệng ❤️</p>
                """.formatted(reservation.getCustomerName());

        String html = wrapEmail("#3498db", "#2874a6", "🎉", "Check-in thành công", body);

        mailService.sendHtmlEmail(
                reservation.getCustomerEmail(),
                "Check-in thành công - " + RESTAURANT_NAME,
                html
        );
    }

    // Hủy tự động sau 30 phút không check-in
    public void sendAutoCanceled(TableReservations reservation) {

        String info = """
                <div style="background:#fdf8ea;border-radius:12px;overflow:hidden;margin:20px 0;">
                  %s%s%s%s
                </div>
                """.formatted(
                infoRow("Mã đặt bàn", reservation.getReservationCode()),
                infoRow("Bàn số", String.valueOf(reservation.getTable().getTableNumber())),
                infoRow("Thời gian đặt", formatDateTime(reservation.getReservationTime())),
                statusRow("Trạng thái", statusBadge("Đã hủy tự động", "#e74c3c", "#fdecea"))
        );

        String body = """
                <p>Xin chào <b>%s</b>,</p>
                <p>
                  Đơn đặt bàn của bạn đã bị <b>hủy tự động</b> do quá <b>30 phút</b>
                  kể từ thời gian đặt mà chưa thực hiện <b>check-in</b>.
                </p>
                %s
                <p>Nếu vẫn có nhu cầu sử dụng dịch vụ, vui lòng đặt bàn lại trên hệ thống.</p>
                <p style="color:#95a5a6;">Cảm ơn bạn đã lựa chọn %s ❤️</p>
                """.formatted(reservation.getCustomerName(), info, RESTAURANT_NAME);

        String html = wrapEmail("#e74c3c", "#c0392b", "⏰", "Đơn đặt bàn đã bị hủy", body);

        mailService.sendHtmlEmail(
                reservation.getCustomerEmail(),
                "Thông báo hủy đặt bàn tự động - " + RESTAURANT_NAME,
                html
        );
    }

    // Hủy
    public void sendCanceled(TableReservations reservation) {

        String body = """
                <p>Xin chào <b>%s</b>,</p>
                <p>Đơn đặt bàn <b>%s</b> đã bị hủy.</p>
                """.formatted(reservation.getCustomerName(), reservation.getReservationCode());

        String html = wrapEmail("#e74c3c", "#c0392b", "❌", "Đơn đặt bàn đã bị hủy", body);

        mailService.sendHtmlEmail(
                reservation.getCustomerEmail(),
                "Đơn đặt bàn đã bị hủy - " + RESTAURANT_NAME,
                html
        );
    }

    // Hoàn thành
    public void sendCompleted(TableReservations reservation) {

        String body = """
                <p>Xin chào <b>%s</b>,</p>
                <p>%s xin chân thành cảm ơn bạn đã sử dụng dịch vụ.</p>
                <p>Hy vọng sẽ tiếp tục được phục vụ bạn trong thời gian tới.</p>
                """.formatted(reservation.getCustomerName(), RESTAURANT_NAME);

        String html = wrapEmail("#16a085", "#0e6655", "❤️", "Cảm ơn bạn đã ghé thăm", body);

        mailService.sendHtmlEmail(
                reservation.getCustomerEmail(),
                "Cảm ơn bạn đã sử dụng dịch vụ - " + RESTAURANT_NAME,
                html
        );
    }

    // ================= AUTHENTICATION =================

    public void sendAccountLocked(User user) {

        String body = """
                <p>Xin chào <b>%s</b>,</p>
                <p>
                  Do đăng nhập sai quá 5 lần, tài khoản của bạn đã bị khóa
                  trong <b>15 phút</b>.
                </p>
                """.formatted(user.getUsername());

        String html = wrapEmail("#e74c3c", "#c0392b", "🔒", "Tài khoản đã bị khóa", body);

        mailService.sendHtmlEmail(
                user.getEmail(),
                "Tài khoản bị khóa - " + RESTAURANT_NAME,
                html
        );
    }

    public void sendLoginSuccess(User user) {

        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));

        String body = """
                <p>Xin chào <b>%s</b>,</p>
                <p>Tài khoản của bạn vừa đăng nhập lúc <b>%s</b>.</p>
                <p style="color:#95a5a6;">Nếu không phải bạn, vui lòng đổi mật khẩu ngay.</p>
                """.formatted(user.getUsername(), time);

        String html = wrapEmail("#27ae60", "#1e8449", "🔔", "Thông báo đăng nhập", body);

        mailService.sendHtmlEmail(
                user.getEmail(),
                "Thông báo đăng nhập - " + RESTAURANT_NAME,
                html
        );
    }

    public void sendOtp(User user, int otp) {

        String body = """
                <p>Xin chào <b>%s</b>,</p>
                <p>Mã OTP xác thực của bạn là:</p>
                <div style="text-align:center;margin:24px 0;">
                  <span style="display:inline-block;padding:14px 28px;border-radius:12px;
                               background:#fdf3d4;color:#a5790e;font-size:32px;
                               font-weight:800;letter-spacing:8px;">
                    %06d
                  </span>
                </div>
                <p style="color:#95a5a6;">Mã có hiệu lực trong 60 giây. Không chia sẻ mã này cho bất kỳ ai.</p>
                """.formatted(user.getUsername(), otp);

        String html = wrapEmail("#3498db", "#2874a6", "🔐", "Mã OTP xác thực", body);

        mailService.sendHtmlEmail(
                user.getEmail(),
                "Mã OTP xác thực - " + RESTAURANT_NAME,
                html
        );
    }

    public void sendPasswordChanged(User user) {

        String body = """
                <p>Xin chào <b>%s</b>,</p>
                <p>Mật khẩu của bạn vừa được thay đổi thành công.</p>
                <p style="color:#95a5a6;">Nếu đây không phải là bạn, hãy liên hệ quản trị viên ngay.</p>
                """.formatted(user.getUsername());

        String html = wrapEmail("#27ae60", "#1e8449", "🔑", "Đổi mật khẩu thành công", body);

        mailService.sendHtmlEmail(
                user.getEmail(),
                "Đổi mật khẩu thành công - " + RESTAURANT_NAME,
                html
        );
    }

    public void sendRegisterSuccess(User user) {

        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));

        String info = """
                <div style="background:#fdf8ea;border-radius:12px;overflow:hidden;margin:20px 0;">
                  %s%s%s
                </div>
                """.formatted(
                infoRow("👤 Tên đăng nhập", user.getUsername()),
                infoRow("📧 Email", user.getEmail()),
                infoRow("🕒 Thời gian đăng ký", time)
        );

        String body = """
                <p>Xin chào <b>%s</b>,</p>
                <p>Chúc mừng! Tài khoản của bạn đã được tạo thành công.</p>
                %s
                <p style="color:#95a5a6;">Cảm ơn bạn đã lựa chọn sử dụng hệ thống của %s ❤️</p>
                """.formatted(user.getFullName(), info, RESTAURANT_NAME);

        String html = wrapEmail("#27ae60", "#1e8449", "🎉", "Đăng ký tài khoản thành công", body);

        mailService.sendHtmlEmail(
                user.getEmail(),
                "Đăng ký tài khoản thành công - " + RESTAURANT_NAME,
                html
        );
    }

    // ================= USER MANAGEMENT =================

    public void sendAccountLockedByAdmin(User user) {

        String body = """
                <p>Xin chào <b>%s</b>,</p>
                <p>Tài khoản của bạn đã bị khóa.</p>
                <p>Trong thời gian tài khoản bị khóa, bạn sẽ không thể đăng nhập vào hệ thống.</p>
                <p>Nếu bạn cho rằng đây là nhầm lẫn, vui lòng liên hệ quản trị viên để được hỗ trợ.</p>
                """.formatted(user.getFullName());

        String html = wrapEmail("#e74c3c", "#c0392b", "🔒", "Tài khoản đã bị khóa", body);

        mailService.sendHtmlEmail(
                user.getEmail(),
                "Tài khoản của bạn đã bị khóa - " + RESTAURANT_NAME,
                html
        );
    }

    public void sendAccountUnlocked(User user) {

        String body = """
                <p>Xin chào <b>%s</b>,</p>
                <p>Quản trị viên đã mở khóa tài khoản của bạn thành công.</p>
                <p>Bạn có thể đăng nhập và tiếp tục sử dụng hệ thống như bình thường.</p>
                <p style="color:#95a5a6;">Nếu vẫn gặp sự cố khi đăng nhập, vui lòng liên hệ quản trị viên.</p>
                """.formatted(user.getFullName());

        String html = wrapEmail("#27ae60", "#1e8449", "🔓", "Tài khoản đã được mở khóa", body);

        mailService.sendHtmlEmail(
                user.getEmail(),
                "Tài khoản của bạn đã được mở khóa - " + RESTAURANT_NAME,
                html
        );
    }

}