package com.sdd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdd.exception.SDDException;
import com.sdd.response.DefaultResponse;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;



public class main {
    public static void main(String[] args) {
        String token="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJnS1MwOGlfTkFvdWNqOUN3ZGdXcTFmcmhOSzdxc2RfVFhiU1hhOFhVbVEwIn0.eyJleHAiOjE3Mzg3NDA5ODksImlhdCI6MTczODc0MDg2OSwiYXV0aF90aW1lIjoxNzM4NzQwODU2LCJqdGkiOiI3YjRmNzUwMC0yYTU3LTRlZDAtODNhNi1kNTdlNDNlYjZhNzgiLCJpc3MiOiJodHRwOi8vaWNnLm5ldC5pbi9hdXRoL3JlYWxtcy9pY2dybXMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiMjE5MWQwODgtMGMwYS00NDI5LTlmNjUtYzMwMGYwMzQwYjM5IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYnVkZ2V0Iiwibm9uY2UiOiJjMDdjZDllNC01Mzg5LTQ2NGItOWU4Zi0xNTM2MzRhMWE3ZjMiLCJzZXNzaW9uX3N0YXRlIjoiMjdhNDJmZjItZTIzOC00MWYwLWE5ZjQtNDBiZTk1NTYxZWY5IiwiYWNyIjoiMCIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL2ljZy5uZXQuaW4iXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtaWNncm1zIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJzaWQiOiIyN2E0MmZmMi1lMjM4LTQxZjAtYTlmNC00MGJlOTU1NjFlZjkiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJTaHJpb20gSCIsInByZWZlcnJlZF91c2VybmFtZSI6InNocmlvbS4wNDYxMSIsImdpdmVuX25hbWUiOiJTaHJpb20iLCJmYW1pbHlfbmFtZSI6IkgiLCJlbWFpbCI6InNocmlvbS4wNDYxMUBpY2cuZ292LmluIn0.cL43IeP_0GwFeki4oVO7PfB31_8_z__1jvQgVJzMROClLWeSY7derpfoMptF_gXiq2nqe-mGA4RBxtMJR4Yz3tgAj52OOzHHuUssAwlHFjPYsttFJJTa0fI6CmMd-1ODBOHY_uAH1XQM0S647HyyRhPTVNQ7Ya0lC7B7NMGIEo0eciuDmnRLsuwpa68SCYLt34xiZ3PVCBCHV5ns0I5qg3Fq9k1JeiWnQNhiWy5ImE6XRrpunwFYEO7qm7SS6F7TCrYnTwZJ2Hu73DxQuN9eMWYueXQ60-skMv9zwKIUrpy9I_3HCJrsuC0Jlam6Rf_j_vDddLmz_n8XKZKAOaC19A";
        try {
            boolean flag= validateToken123(token);
            if (flag) {
                System.out.println(flag);
//        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "Token not expired");
//        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-004");
            } else {
                //  System.out.println("DeActive: ");

                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN. LOGIN AGAIN.IN-005");
            }
        }catch (Exception ed){
            System.out.println(ed.getMessage());
            ed.printStackTrace();
        }
}
    public static boolean validateToken123(String tokenWithoutBearer) {
        StringBuilder response = new StringBuilder();
        try {
            String url = "https://icg.net.in/auth/realms/icgrms/protocol/openid-connect/token/introspect";
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
    }}
