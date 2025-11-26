package com.smartshop.util;

import com.smartshop.model.dto.UserDTO;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    private static final String SESSION_USER = "SESSION_USER";

    // 取得登入的 UserDTO
    public static UserDTO getLoginUser(HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute(SESSION_USER);
        if (user == null) {
            throw new RuntimeException("尚未登入，請先登入後再操作");
        }
        return user;
    }

    // 是否為 ADMIN
    public static boolean isAdmin(HttpSession session) {
        UserDTO user = getLoginUser(session);
        return "ADMIN".equalsIgnoreCase(user.getRole());
    }

    // 需要 ADMIN 權限（否則丟例外）
    public static void requireAdmin(HttpSession session) {
        if (!isAdmin(session)) {
            throw new RuntimeException("需要 ADMIN 權限");
        }
    }

    // 是否登入（給 /auth/isLoggedIn 使用）
    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(SESSION_USER) != null;
    }

    // 確保登入，回傳 username
    public static String requireLogin(HttpSession session) {
        UserDTO user = getLoginUser(session);
        return user.getUsername();
    }

    // 放入 session（登入成功使用）
    public static void setLoginUser(HttpSession session, UserDTO user) {
        session.setAttribute(SESSION_USER, user);
    }

    // 清除 session（登出使用）
    public static void clearLogin(HttpSession session) {
        session.invalidate();
    }
}
