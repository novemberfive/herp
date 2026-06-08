package com.asset.util;

import cn.hutool.crypto.digest.BCrypt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * JWT 工具类
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    /**
     * 生成 Token
     */
    public String generateToken(String username, String role, List<String> permissions) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);
        
        return JWT.create()
                .withSubject(username)
                .withClaim("username", username)
                .withClaim("role", role)
                .withClaim("permissions", permissions)
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * 生成刷新 Token
     */
    public String generateRefreshToken(String username, String role, List<String> permissions) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpiration);
        
        return JWT.create()
                .withSubject(username)
                .withClaim("username", username)
                .withClaim("role", role)
                .withClaim("permissions", permissions)
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * 验证 Token
     */
    public DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secret)).build();
        return verifier.verify(token);
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = verifyToken(token);
        return decodedJWT.getClaim("username").asString();
    }

    /**
     * 检查 Token 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = verifyToken(token);
            return decodedJWT.getExpiresAt().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 密码加密
     */
    public String encodePassword(String password) {
        return BCrypt.hashpw(password);
    }

    /**
     * 密码校验
     */
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
