package com.smartshop.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

public class ECPayUtils {

    // 產生綠界 CheckMacValue（SHA256)
    public static String generateCheckMacValue(Map<String, String> params, String hashKey, String hashIV) {

        // 1. 依照 ASCII 排序（綠界規定）
        Map<String, String> sorted = new TreeMap<>(params);

        // 2. 組合字串
        StringBuilder sb = new StringBuilder();
        sb.append("HashKey=").append(hashKey);

        sorted.forEach((k, v) -> sb.append("&").append(k).append("=").append(v));

        sb.append("&HashIV=").append(hashIV);

        // 3.URL encode (小寫)
        String urlEncoded = URLEncoder.encode(sb.toString(), StandardCharsets.UTF_8).toLowerCase();

        try {
            // 4. SHA256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(urlEncoded.getBytes(StandardCharsets.UTF_8));

            // 5. 轉為大寫
            StringBuilder result = new StringBuilder();
            for (byte b : digest) {
                result.append(String.format("%02X", b));
            }
            return result.toString();

        } catch (Exception e) {
            throw new RuntimeException("CheckMacValue 產生失敗");
        }
    }
}
