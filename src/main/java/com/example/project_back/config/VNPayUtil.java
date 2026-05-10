package com.example.project_back.config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VNPayUtil {

    public static String hmacSHA512(String key, String data) {

        try {

            Mac hmac512 = Mac.getInstance("HmacSHA512");

            SecretKeySpec secretKey =
                    new SecretKeySpec(
                            key.getBytes(StandardCharsets.UTF_8),
                            "HmacSHA512"
                    );

            hmac512.init(secretKey);

            byte[] hashBytes =
                    hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();

            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return sb.toString();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String buildQuery(
            Map<String, String> params,
            boolean encodeKey,
            boolean encodeValue
    ) {

        List<String> fieldNames =
                new ArrayList<>(params.keySet());

        Collections.sort(fieldNames);

        StringBuilder sb = new StringBuilder();

        for (String fieldName : fieldNames) {

            String value = params.get(fieldName);

            if (value != null && value.length() > 0) {

                String key = fieldName;

                if (encodeKey) {
                    key = URLEncoder.encode(
                            fieldName,
                            StandardCharsets.US_ASCII
                    );
                }

                if (encodeValue) {
                    value = URLEncoder.encode(
                            value,
                            StandardCharsets.US_ASCII
                    );
                }

                sb.append(key);
                sb.append("=");
                sb.append(value);
                sb.append("&");
            }
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    public static boolean verify(
            Map<String, String> fields,
            String secretKey,
            String secureHash
    ) {

        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String signData =
                buildQuery(fields, false, true);

        String signValue =
                hmacSHA512(secretKey, signData);

        return signValue.equalsIgnoreCase(secureHash);
    }
}