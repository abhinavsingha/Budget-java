package com.sdd.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sdd.entities.AllocationType;
import com.sdd.exception.SDDException;
import com.sdd.jwtParse.TokenParseData;
import com.sdd.response.DefaultResponse;
import com.sdd.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;

@Component
public class HeaderUtils {
  DefaultResponse defaultResponse = new DefaultResponse();
  public static final String USER_ID = "user_id";
  public static final String EMAIL_ID = "user_id";
  public static final String MOBILE_NO = "mobile_no";
  public static final String LEVEL = "level";
  @Autowired private HttpServletRequest httpServletRequest;

  public String getTokeFromHeader()
//          throws IOException {
  {

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
    https://icg.net.in/auth/realms/icgrms/protocol/openid-connect/token/introspect
    //System.out.println("token::"+tokenWithoutBearer);
    try {
      boolean flag= validateToken(tokenWithoutBearer);
      if (flag) {
        //System.out.println("ifflag::"+flag);
//        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "Token not expired");
//        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-004");
      } else {
      //  System.out.println("DeActive: ");
       // System.out.println("elseflag::"+flag);
        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-005");
      }
    }catch (Exception ed){
      System.out.println(ed.getMessage());
        ed.printStackTrace();
      throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-006");
    }
    return tokenWithoutBearer;
  }



//  public String getTokeFromHeader() {
//
//    String tokenWithoutBearer = "";
//    String token = httpServletRequest.getHeader("Authorization");
//    if (token == null) {
//      throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-001");
//    }
//    if (!(token.contains("Bearer"))) {
//      throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-002");
//    }
//    try {
//      String[] tokenWithBearer = token.split(" ");
//      tokenWithoutBearer = tokenWithBearer[1];
//    } catch (Exception e) {
//      throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-03");
//    }
//
//    return tokenWithoutBearer;
//  }

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
        throw new SDDException(
            HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-003");
      }
      if (exampleData.getPreferred_username() == null) {
        throw new SDDException(
            HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-005");
      }

    } catch (Exception e) {
      throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-001");
    }

    return exampleData;
  }
  public static boolean validateToken(String tokenWithoutBearer) {
    StringBuilder response = new StringBuilder();
    try {
      String url = "https://icg.net.in/auth/realms/icgrms/protocol/openid-connect/token/introspect";
      //String url = "http://172.18.3.21:8080/auth/realms/icgrms/protocol/openid-connect/token/introspect";
      String token = tokenWithoutBearer;
      String Authorization = "BASIC Auth";
      String client_id = "budgetAPI";
      String client_secret = "2uz6nswgHfwJpiQzHuqnpnjP5fAQGKtf";

      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();

      // Set the request method to POST
      con.setRequestMethod("POST");

      // Set the content type to x-www-form-urlencoded
      con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      // Enable output
      con.setDoOutput(true);
      // Build the request body

      String urlParameters = "token=" + URLEncoder.encode(token, "UTF-8") +
              "&Authorization=" + URLEncoder.encode(Authorization, "UTF-8") +
              "&client_id=" + URLEncoder.encode(client_id, "UTF-8") +
              "&client_secret=" + URLEncoder.encode(client_secret, "UTF-8");


      // Send the request body
      try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
        wr.writeBytes(urlParameters);
        wr.flush();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      // Get the response code
      int responseCode = con.getResponseCode();
      System.out.println("Response Code : " + responseCode);
      // Read the response
      try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
//                System.out.println("Response : " + response.toString());
      }
      ObjectMapper mapper = new ObjectMapper();
      JsonNode rootNode = mapper.readTree(String.valueOf(response));
      boolean isActive = rootNode.path("active").asBoolean();

      System.out.println("Active: " + isActive);
      return isActive;
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

