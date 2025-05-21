package com.myapp.app.utils;

import com.myapp.app.entity.UserEntity;
import com.myapp.app.exception.AppException;
import com.myapp.app.exception.ErrorCode;
import com.myapp.app.service.TokenService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@RequiredArgsConstructor
public class TokenUtils {
    @Autowired
    private TokenService tokenService;
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;


    public String getUserIdByToken(String token) throws ParseException {
        SignedJWT signedJWT;
        try {
            signedJWT = SignedJWT.parse(token);
        } catch (ParseException e) {
            return null;
        }
        return signedJWT.getJWTClaimsSet().getSubject();
    }


    public void isValidToken(String token) throws AppException, ParseException, JOSEException {
        SignedJWT signedJWT;
        try {
            signedJWT = SignedJWT.parse(token);
        } catch (ParseException e) {
            throw new AppException(ErrorCode.NOT_AUTHENTICATION);
        }
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        boolean verified = signedJWT.verify(verifier);
        if (!verified) {
            throw new AppException(ErrorCode.NOT_AUTHENTICATION);
        }
        if ( !tokenService.existToken(token)) {
            throw new AppException(ErrorCode.NOT_AUTHENTICATION);
        }
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiryTime.before(new Date())) {
            tokenService.deleteToken(token);
            throw new AppException(ErrorCode.NOT_AUTHENTICATION);
        }
        else{
            System.out.println("123123123123");
        }
    }

    public boolean checkToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        if(!tokenService.existToken(token)) return false;
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean verified = signedJWT.verify(verifier);
        return verified && expiryTime.after(new Date());
    }

    public String generateToken(UserEntity user) throws JOSEException {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet;
            jwtClaimsSet = new JWTClaimsSet.Builder()
                    .issuer("hoangtuyen.com")
                    .subject(user.getId())
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plus(24*60*60, ChronoUnit.SECONDS)))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("roles",buildRoles())
                    .build();

        JWSObject jwsObject = new JWSObject(jwsHeader, new Payload(jwtClaimsSet.toJSONObject()));
        jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
        String token =  jwsObject.serialize();
        return token;
    }

    private List<String> buildRoles(){
        List<String> list = new ArrayList<>();
        list.add("USER");
        return list;
    }
}
