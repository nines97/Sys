package com.nines.sys.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import java.util.Date;

/**
 * @author TYJ
 * @date 2020/10/21 14:43
 */
public class JWTUtil {

    @Value("${expireTime}")
    private static long expireTime;

    @Value("${secret}")
    private static String secret;

    /**
     * 生成签名
     * @param username 用户名
     * @param password 密码
     * @return 签名
     */
    public static String sign(String username, String password) {
        // 指定过期时间
        Date date = new Date(System.currentTimeMillis() + expireTime);
        // 给加密算法传递加密盐
        Algorithm algorithm = Algorithm.HMAC256(password + secret);
        // 附带username信息
        return JWT.create()
                .withClaim("username", username)
                //到期时间
                .withExpiresAt(date)
                //创建一个新的JWT，并使用给定的算法进行标记
                .sign(algorithm);
    }

    /**
     * 校验token是否正确
     *
     * @param token    密钥
     * @param username 登录名
     * @param password 密码
     * @return boolean
     */
    public static boolean verify(String token, String username, String password) {
        try {
            // 给加密算法传递加密盐
            Algorithm algorithm = Algorithm.HMAC256(password + secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获得token中的信息，无需secret解密也能获得
     *
     * @param token 密钥
     * @return 用户名
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
}
