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
// ĐẶT BÀN ĂN
    // Đặt bàn thành công
    public void sendBookingSuccess(TableReservations reservation) {

        String html = """
                <html>
                <body style="font-family:Arial,sans-serif;background:#f5f5f5;padding:20px">

                <div style="max-width:650px;margin:auto;background:#fff;padding:30px;
                            border-radius:10px;box-shadow:0 0 10px rgba(0,0,0,.1)">

                    <h2 style="color:#27ae60;text-align:center">
                        🍽 ĐẶT BÀN THÀNH CÔNG
                    </h2>

                    <p>Xin chào <b>%s</b>,</p>

                    <p>
                        Nhà hàng đã tiếp nhận yêu cầu đặt bàn của bạn.
                    </p>

                    <table style="width:100%%;border-collapse:collapse" border="1" cellpadding="10">
                        <tr>
                            <td><b>Mã đặt bàn</b></td>
                            <td>%s</td>
                        </tr>

                        <tr>
                            <td><b>Bàn số</b></td>
                            <td>%s</td>
                        </tr>

                        <tr>
                            <td><b>Sức chứa</b></td>
                            <td>%d người</td>
                        </tr>

                        <tr>
                            <td><b>Thời gian</b></td>
                            <td>%s</td>
                        </tr>

                        <tr>
                            <td><b>Trạng thái</b></td>
                            <td style="color:#e67e22"><b>Đang chờ xác nhận</b></td>
                        </tr>

                    </table>

                    <br>

                    <p>
                        Chúng tôi sẽ xác nhận đơn đặt bàn trong thời gian sớm nhất.
                    </p>

                    <hr>

                    <p style="color:gray">
                        Cảm ơn bạn đã lựa chọn nhà hàng ❤️
                    </p>

                </div>

                </body>
                </html>
                """.formatted(
                reservation.getCustomerName(),
                reservation.getReservationCode(),
                reservation.getTable().getTableNumber(),
                reservation.getTable().getCapacity(),
                reservation.getReservationTime()
        );

        mailService.sendHtmlEmail(
                reservation.getCustomerEmail(),
                "Đặt bàn thành công",
                html
        );
    }

    // Xác nhận đặt bàn
    public void sendConfirmed(TableReservations reservation) {

        String html = """
                <html>
                <body style="font-family:Arial">
                    <h2 style="color:#2ecc71">✅ Đơn đặt bàn đã được xác nhận</h2>

                    <p>Xin chào <b>%s</b>,</p>

                    <p>
                        Nhà hàng đã xác nhận đơn đặt bàn của bạn.
                    </p>

                    <p>
                        <b>Mã đặt:</b> %s
                    </p>

                    <p>
                        Chúng tôi rất mong được phục vụ bạn.
                    </p>

                </body>
                </html>
                """.formatted(
                reservation.getCustomerName(),
                reservation.getReservationCode()
        );

        mailService.sendHtmlEmail(
                reservation.getCustomerEmail(),
                "Đơn đặt bàn đã được xác nhận",
                html
        );
    }

    // Check in
    public void sendCheckIn(TableReservations reservation) {

        String html = """
                <html>
                <body style="font-family:Arial">

                <h2 style="color:#3498db">
                🎉 Check-in thành công
                </h2>

                <p>Xin chào <b>%s</b>,</p>

                <p>Bạn đã check-in thành công.</p>

                <p>
                Nhà hàng sẽ giữ bàn của bạn trong vòng
                <b>30 phút</b>.
                </p>

                <p>
                Chúc bạn có một bữa ăn ngon miệng ❤️
                </p>

                </body>
                </html>
                """.formatted(
                reservation.getCustomerName()
        );

        mailService.sendHtmlEmail(
                reservation.getCustomerEmail(),
                "Check-in thành công",
                html
        );
    }

    // Hủy tự động sau 30 phút không check-in
    public void sendAutoCanceled(TableReservations reservation) {

        String html = """
        <html>
        <body style="font-family:Arial,sans-serif;background:#f5f5f5;padding:20px">

        <div style="max-width:650px;margin:auto;background:#fff;
                    padding:30px;border-radius:10px;
                    box-shadow:0 0 10px rgba(0,0,0,.1)">

            <h2 style="color:#e74c3c;text-align:center">
                ⏰ Đơn đặt bàn đã bị hủy tự động
            </h2>

            <p>Xin chào <b>%s</b>,</p>

            <p>
                Đơn đặt bàn của bạn đã bị <b>hủy tự động</b> do
                quá <b>30 phút</b> kể từ thời gian đặt mà chưa thực hiện
                <b>check-in</b>.
            </p>

            <table style="width:100%%;border-collapse:collapse"
                   border="1" cellpadding="10">

                <tr>
                    <td><b>Mã đặt bàn</b></td>
                    <td>%s</td>
                </tr>

                <tr>
                    <td><b>Bàn số</b></td>
                    <td>%s</td>
                </tr>

                <tr>
                    <td><b>Thời gian đặt</b></td>
                    <td>%s</td>
                </tr>

                <tr>
                    <td><b>Trạng thái</b></td>
                    <td style="color:#e74c3c"><b>Đã hủy tự động</b></td>
                </tr>

            </table>

            <br>

            <p>
                Nếu vẫn có nhu cầu sử dụng dịch vụ,
                vui lòng thực hiện đặt bàn lại trên hệ thống.
            </p>

            <hr>

            <p style="color:gray">
                Cảm ơn bạn đã lựa chọn nhà hàng ❤️
            </p>

        </div>

        </body>
        </html>
        """.formatted(
                reservation.getCustomerName(),
                reservation.getReservationCode(),
                reservation.getTable().getTableNumber(),
                reservation.getReservationTime()
        );

        mailService.sendHtmlEmail(
                reservation.getCustomerEmail(),
                "Thông báo hủy đặt bàn tự động",
                html
        );
    }

    // Hủy
    public void sendCanceled(TableReservations reservation) {

        String html = """
                <html>
                <body style="font-family:Arial">

                <h2 style="color:#e74c3c">
                ❌ Đơn đặt bàn đã bị hủy
                </h2>

                <p>Xin chào <b>%s</b>,</p>

                <p>
                Đơn đặt bàn <b>%s</b> đã bị hủy.
                </p>

                </body>
                </html>
                """.formatted(
                reservation.getCustomerName(),
                reservation.getReservationCode()
        );

        mailService.sendHtmlEmail(
                reservation.getCustomerEmail(),
                "Đơn đặt bàn đã bị hủy",
                html
        );
    }

    // Hoàn thành
    public void sendCompleted(TableReservations reservation) {

        String html = """
                <html>
                <body style="font-family:Arial">

                <h2 style="color:#16a085">
                ❤️ Cảm ơn bạn đã ghé thăm
                </h2>

                <p>Xin chào <b>%s</b>,</p>

                <p>
                Nhà hàng xin chân thành cảm ơn bạn đã sử dụng dịch vụ.
                </p>

                <p>
                Hy vọng sẽ tiếp tục được phục vụ bạn trong thời gian tới.
                </p>

                </body>
                </html>
                """.formatted(
                reservation.getCustomerName()
        );

        mailService.sendHtmlEmail(
                reservation.getCustomerEmail(),
                "Cảm ơn bạn đã sử dụng dịch vụ",
                html
        );
    }


//    AUTHENTICATION
public void sendAccountLocked(User user) {

    String html = """
        <html>
        <body style="font-family:Arial">

        <h2 style="color:red">
        🔒 Tài khoản đã bị khóa
        </h2>

        <p>Xin chào <b>%s</b>,</p>

        <p>
        Do đăng nhập sai quá 5 lần,
        tài khoản của bạn đã bị khóa trong <b>15 phút</b>.
        </p>

        </body>
        </html>
        """.formatted(user.getUsername());

    mailService.sendHtmlEmail(
            user.getEmail(),
            "Tài khoản bị khóa",
            html
    );
}

    public void sendLoginSuccess(User user) {

        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));

        String html = """
        <html>
        <body style="font-family:Arial">

        <h2 style="color:#27ae60">
         Thông báo đăng nhập
        </h2>

        <p>Xin chào <b>%s</b>,</p>

        <p>
        Tài khoản của bạn vừa đăng nhập lúc
        <b>%s</b>.
        </p>

        </body>
        </html>
        """.formatted(user.getUsername(), time);

        mailService.sendHtmlEmail(
                user.getEmail(),
                "Thông báo đăng nhập",
                html
        );
    }

    public void sendOtp(User user, int otp) {

        String html = """
        <html>
        <body style="font-family:Arial">

        <h2>🔐 Mã OTP xác thực</h2>

        <p>Xin chào <b>%s</b></p>

        <p>Mã OTP của bạn là:</p>

        <h1 style="color:#3498db">%06d</h1>

        <p>Mã có hiệu lực trong 30 giây.</p>

        </body>
        </html>
        """.formatted(
                user.getUsername(),
                otp
        );

        mailService.sendHtmlEmail(
                user.getEmail(),
                "Mã OTP xác thực",
                html
        );
    }

    public void sendPasswordChanged(User user) {

        String html = """
        <html>
        <body style="font-family:Arial">

        <h2 style="color:#27ae60">
        🔑 Đổi mật khẩu thành công
        </h2>

        <p>Xin chào <b>%s</b>,</p>

        <p>Mật khẩu của bạn vừa được thay đổi thành công.</p>

        <p>Nếu đây không phải là bạn, hãy liên hệ quản trị viên ngay.</p>

        </body>
        </html>
        """.formatted(user.getUsername());

        mailService.sendHtmlEmail(
                user.getEmail(),
                "Đổi mật khẩu thành công",
                html
        );
    }


//

}