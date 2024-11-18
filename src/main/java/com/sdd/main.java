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
//        Test t=new Test();
//        t.main();

//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJnS1MwOGlfTkFvdWNqOUN3ZGdXcTFmcmhOSzdxc2RfVFhiU1hhOFhVbVEwIn0.eyJleHAiOjE3MzEzOTg5MjksImlhdCI6MTczMTM5ODYyOSwiYXV0aF90aW1lIjoxNzMxMzk4NjI4LCJqdGkiOiIwOGExYjM5ZC04NWQ0LTRhZjktOTVhNS01NjllM2Q4OWVjNjkiLCJpc3MiOiJodHRwOi8vaWNnLm5ldC5pbi9hdXRoL3JlYWxtcy9pY2dybXMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiMjE5MWQwODgtMGMwYS00NDI5LTlmNjUtYzMwMGYwMzQwYjM5IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiY2didWRnZXQiLCJub25jZSI6IjJmOTk1NWQzLTJlMzgtNDZjMi1hOWI3LTVlNWZhYTEzMGQzNCIsInNlc3Npb25fc3RhdGUiOiIyY2UwZDE1Yi04NDBhLTQwMzktYTUzNi1hNjE4MjQ3OGIzZmMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtaWNncm1zIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGljZ3NzbyBlbWFpbCBwcm9maWxlIGljZ3JtcyIsInNpZCI6IjJjZTBkMTViLTg0MGEtNDAzOS1hNTM2LWE2MTgyNDc4YjNmYyIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiYWRkcmVzcyI6e30sIm5hbWUiOiJTaHJpb20gSCIsInByZWZlcnJlZF91c2VybmFtZSI6InNocmlvbS4wNDYxMSIsImdpdmVuX25hbWUiOiJTaHJpb20iLCJmYW1pbHlfbmFtZSI6IkgiLCJlbWFpbCI6InNocmlvbS4wNDYxMUBpY2cuZ292LmluIn0.d7QhwLgTBPmRgjW4-jAap01AA2sqxvZ3WSzVINtauq0JmdalpfxMlWnfMuqAf_AGNPBpuPFPkXF6nPSu1WZ2c3ILdl_A17Kt_NKkktNjAtcobn9lprM3AoKxqsuCxw3XnTrFEWYREUiVwgpUwCYM_n96dI_vaFAVe4teq93n4hsGyf1QNZHYv8NCsAy-AJ7Bv_fw8w0ZbQkiL5fnHhvOAyPVd3kCm4Gm31pv1Qc9ocVJWCsV5Nm7MLgWiX93Oze34mWqnjyaHAc_myas2wdOnVDyVtjVEl4IV5GVGN9tabRljp3jp30up60gNdMbW1ebysmUEhQUIZqPR_BFDqg_KA";
//        DefaultResponse defaultResponse = new DefaultResponse();
//        validateToken(token);
//        if(validateToken(token).equals("true")){
//           // System.out.println("Active: ");
//            //throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "Token not expired");
//            //defaultResponse.setMsg("Token  not expired");
//        }else{
//            //System.out.println("DeActive: ");
//           // defaultResponse.setMsg("Token expired");
//        }
    }
    public static String validateToken(String tokenWithoutBearer) {
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
                System.out.println("Response : " + response.toString());
            }
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(String.valueOf(response));
                boolean isActive = rootNode.path("active").asBoolean();
                //System.out.println("Active: " + isActive);
            return response.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
