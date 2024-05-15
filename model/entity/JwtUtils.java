package model.entity;

import io.jsonwebtoken.*;

import java.io.*;
import java.util.Base64;
import java.util.Date;
import org.json.JSONObject;

public class JwtUtils {

    private static final String JWT_SECRET = "myJwtSecret";
    private static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000; // 1 day

    public static boolean verifyJwt(String jwtToken, String secretKey, String expectedUsername) {
        try {
            // Parse the JWT token
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);

            // Extract the username from the JWT payload
            String username = claims.getBody().get("username", String.class);

            // Check if the username matches the expected value
            if (!username.equals(expectedUsername)) {
                return false;
            }

            // JWT is valid
            return true;
        } catch (JwtException e) {
            // JWT is invalid
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            // Read the request body from the input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String requestBody = HttpRequestUtils.readRequestBody(reader);

            // Parse the JWT token from the request body
            String[] parts = requestBody.split("\\.");
            String encodedPayload = parts[1];
            byte[] decodedPayload = Base64.getUrlDecoder().decode(encodedPayload);
            String payload = new String(decodedPayload, "UTF-8");

            // Extract the username from the JWT payload
            JSONObject payloadJson = new JSONObject(payload);
            String username = payloadJson.getString("username");

            // Verify the JWT token using the separate method
            String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImpvaG4iLCJpYXQiOjE1MTYyMzkwMjJ9.5FuuhXaWmP6mEeZB1XpT1V4vMtcLjVz0Pe3UQ1lA7po";
            String secretKey = "my_secret_key";
            boolean isValidJwt = verifyJwt(jwtToken, secretKey, username);

            // Check if the JWT token is valid
            if (!isValidJwt) {
                throw new Exception("Invalid JWT token");
            }

            // JWT is valid, proceed with processing the request
            System.out.println("JWT is valid!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String generateJwtToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + JWT_EXPIRATION_MS);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public static String verifyJwtToken(String token) throws SignatureException, MalformedJwtException {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}