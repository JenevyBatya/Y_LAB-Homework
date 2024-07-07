package io.ylab.security;

import io.jsonwebtoken.*;
import io.ylab.managment.ResultResponse;
import io.ylab.managment.enums.ResponseEnum;

import java.util.Date;

public class JwtTokenService {
    private static final String secret_key = "never_gonna_give_you_up";

    public static String generateToken(int userId) {
        Date now = new Date();
        int exp_time = 60 * 50 * 5 * 1000;
        Date expiration = new Date(now.getTime() + exp_time);

        return Jwts.builder()
                .setExpiration(expiration)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }

    public static ResultResponse validateToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secret_key).parseClaimsJws(token).getBody();
            return new ResultResponse(claims.getExpiration().after(new Date()), ResponseEnum.USER_AUTH_SUCCESS);
        } catch (ExpiredJwtException expEx) {
            return new ResultResponse(false, ResponseEnum.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException unsEx) {
            return new ResultResponse(false, ResponseEnum.TOKEN_UNSUPPORTED_JWT);
        } catch (MalformedJwtException mjEx) {
            return new ResultResponse(false, ResponseEnum.TOKEN_MALFORMED_JWT);
        } catch (SignatureException sEx) {
            return new ResultResponse(false, ResponseEnum.TOKEN_INVALID_SIGNATURE);
        } catch (Exception e) {
            return new ResultResponse(false, ResponseEnum.TOKEN_INVALID);
        }


    }

    public static int getIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret_key).parseClaimsJws(token).getBody();
        return Integer.parseInt(claims.getSubject().split(" ")[1]);
    }

    public JwtTokenService() {
    }
}
