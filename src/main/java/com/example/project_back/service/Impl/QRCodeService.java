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

    // domain backend public
    private static final String BASE_URL =
            "https://subfractionally-wrinkleable-kenneth.ngrok-free.dev";

    public String generateQRCode(String tableNumber) {

        try {

            // URL khi khách quét QR
            String frontendUrl =
                    BASE_URL + "/customer/table-order?table=" + tableNumber;

            String folderPath = "uploads/qrcodes/";

            File folder = new File(folderPath);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            String fileName = "table-" + tableNumber + ".png";

            String filePath = folderPath + fileName;

            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    frontendUrl,
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

            // URL public ảnh QR
            return BASE_URL + "/qrcodes/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Generate QR Failed");
        }
    }

    public void deleteQRCode(String qrUrl) {

        try {

            String fileName =
                    qrUrl.substring(qrUrl.lastIndexOf("/") + 1);

            String filePath =
                    "uploads/qrcodes/" + fileName;

            File file = new File(filePath);

            if (file.exists()) {
                file.delete();
            }

        } catch (Exception e) {
            throw new RuntimeException("Delete QR Failed");
        }
    }
}