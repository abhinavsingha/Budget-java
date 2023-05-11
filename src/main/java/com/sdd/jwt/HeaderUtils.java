package com.sdd.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sdd.exception.SDDException;
import com.sdd.jwtParse.TokenParseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Map;

@Component
public class HeaderUtils {

    @Autowired
    private HttpServletRequest httpServletRequest;


    public static final String USER_ID = "user_id";
    public static final String EMAIL_ID = "user_id";
    public static final String MOBILE_NO = "mobile_no";
    public static final String LEVEL = "level";


    public String getTokeFromHeader() {

        String tokenWithoutBearer = "";
        String token = httpServletRequest.getHeader("Authorization");
        if (token == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-001");
        }
        if (!(token.contains("Bearer"))) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-002");
        }
        try {
            String[] tokenWithBearer = token.split(" ");
            tokenWithoutBearer = tokenWithBearer[1];
        } catch (Exception e) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-03");
        }

        return tokenWithoutBearer;

    }

    public String getBothTokeFromHeader() {

        String tokenWithoutBearer = "";
        String token = httpServletRequest.getHeader("Authorization");
        String budgetToken = httpServletRequest.getHeader("Token");
        if (token == null || budgetToken == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN");
        }
        if (!(token.contains("Bearer"))) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN");
        }
        try {
            String[] tokenWithBearer = token.split(" ");
            tokenWithoutBearer = tokenWithBearer[1];
        } catch (Exception e) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN");
        }

        return tokenWithoutBearer;

    }


    public TokenParseData getUserCurrentDetails(String token) {
        TokenParseData exampleData = null;
        try {
            Base64.Decoder decoder = Base64.getUrlDecoder();
            DecodedJWT decodedJWT = JWT.decode(token);
            Map<String, Claim> claims = decodedJWT.getClaims();

            Gson gson = new GsonBuilder().create();
            exampleData = gson.fromJson(claims.toString(), TokenParseData.class);
            if (exampleData == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-003");
            }
            if (exampleData.getPreferred_username() == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-005");
            }

        } catch (Exception e) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-001");
        }


        return exampleData;

    }


}
