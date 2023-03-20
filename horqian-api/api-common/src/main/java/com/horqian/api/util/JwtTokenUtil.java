package com.horqian.api.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.horqian.api.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author bz
 */
@Slf4j
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    /**
     * 根据负责生成JWT的token
     */
    public String sign(Integer userId) {

        Date date = new Date(System.currentTimeMillis() + expiration * 1000);

        Algorithm algorithm = Algorithm.HMAC512(secret);

        return JWT.create()
                // token 时间限制
                //.withExpiresAt(date)
                .withClaim("userId", userId)
                //
                //.withExpiresAt(new Date(System.currentTimeMillis() + expiration * 1000))
                .sign(algorithm);

    }

    public  boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);

            JWTVerifier verifier = JWT.require(algorithm)
                    .build();

            verifier.verify(token);

        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    public static Integer getUserId(String token) {
        DecodedJWT jwt = JWT.decode(token);
        //根据名字获取
        return jwt.getClaim("userId").asInt();
    }


}
