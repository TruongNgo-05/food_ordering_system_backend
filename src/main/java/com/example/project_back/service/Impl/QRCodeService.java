package com.example.project_back.service.Impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@Service
public class QRCodeService {

    //  Backend domain (chỉ dùng cho ảnh QR + API)
    private static final String BACKEND_URL =
            "https://subfractionally-wrinkleable-kenneth.ngrok-free.dev";

    //  Frontend domain (QUAN TRỌNG: QR phải mở cái này)
    private static final String FRONTEND_URL =
            "http://localhost:5173"; // đổi thành domain frontend khi deploy

    public String generateQRCode(String tableNumber) {

        try {
            String qrContentUrl = FRONTEND_URL + "/table-order?table=" + tableNumber;

            String folderPath = "uploads/qrcodes/";

            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String safeTableNumber = tableNumber.replaceAll("\\s+", "-");

            String fileName = "table-" + safeTableNumber + ".png";
            String filePath = folderPath + fileName;

            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    qrContentUrl,
                    BarcodeFormat.QR_CODE,
                    350,
                    350
            );

            Path path = FileSystems.getDefault().getPath(filePath);

            MatrixToImageWriter.writeToPath(
                    bitMatrix,
                    "PNG",
                    path
            );
            return BACKEND_URL + "/qrcodes/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Generate QR Failed", e);
        }
    }

    public void deleteQRCode(String qrUrl) {
        try {
            String fileName = qrUrl.substring(qrUrl.lastIndexOf("/") + 1);

            File file = new File("uploads/qrcodes/" + fileName);

            if (file.exists()) {
                boolean deleted = file.delete();
                System.out.println("Delete QR: " + deleted);
            }

        } catch (Exception e) {
            throw new RuntimeException("Delete QR Failed", e);
        }
    }
}