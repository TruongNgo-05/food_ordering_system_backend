package com.example.project_back.config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VNPayUtil {

    // ===== HASH =====
    public static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKey);

            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ===== BUILD QUERY (CREATE URL) =====
    public static String buildQuery(Map<String, String> params, boolean encode) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder();

        for (String key : keys) {
            String value = params.get(key);
            if (value != null && !value.isEmpty()) {

                sb.append(encode ? URLEncoder.encode(key, StandardCharsets.UTF_8) : key)
                        .append("=")
                        .append(encode ? URLEncoder.encode(value, StandardCharsets.UTF_8) : value)
                        .append("&");
            }
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    // ===== VERIFY SIGNATURE (RETURN URL) =====
    public static boolean verify(Map<String, String> fields, String secretKey, String secureHash) {

        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String signData = buildQuery(fields, false); // ❗ KHÔNG encode khi verify

        String sign = hmacSHA512(secretKey, signData);

        return sign.equalsIgnoreCase(secureHash);
    }
}