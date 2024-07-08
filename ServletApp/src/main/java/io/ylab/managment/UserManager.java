package io.ylab.managment;

import io.ylab.security.JwtTokenService;

public class UserManager {
    private static boolean authorized;
    private static String token;

    public static boolean isAuthorized() {
        return authorized;
    }

    public static String getToken() {
        return token;
    }

    public static void authorize(int userId) {
        authorized = true;
        token = JwtTokenService.generateToken(userId);
    }

    public static boolean validateToken(String token) {
        if (JwtTokenService.validateToken(token).isSuccess()) {
            authorized = true;
            UserManager.token = token;
            return true;
        }
        authorized = false;
        return false;
    }

    public static int getUserId() {
        if (token != null && JwtTokenService.validateToken(token).isSuccess()) {
            return JwtTokenService.getIdFromToken(token);
        }
        return -1;
    }
}
