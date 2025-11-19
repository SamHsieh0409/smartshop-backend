package com.smartshop.util;

import com.smartshop.model.dto.UserDTO;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    private static final String SESSION_USER = "SESSION_USER";

    public static UserDTO getLoginUser(HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute(SESSION_USER);
        if (user == null) {
            throw new RuntimeException("尚未登入，請先登入後再操作");
        }
        return user;
    }
    public static boolean isAdmin(HttpSession session) {
        UserDTO user = getLoginUser(session);
        return "ADMIN".equalsIgnoreCase(user.getRole());
    }

    public static void requireAdmin(HttpSession session) {
        if (!isAdmin(session)) {
            throw new RuntimeException("需要 ADMIN 權限");
        }
    }
   
}
