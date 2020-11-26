import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Date;

public class Encryption {
    private static final String SECRET = "secret"; // todo get secret as a system variable
    private static final Long TIMEACTIVE = 3600000L; //1 hour


    public static String encodeJWT(String id, Long ttl, String role) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        if (ttl == null){
            ttl = TIMEACTIVE;
        }
        String token = JWT.create()
                .withSubject(id)
                .withClaim("role", role)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withIssuer("ImageHost.sdu.dk")
                .withExpiresAt(new Date(System.currentTimeMillis()+ttl))
                .sign(algorithm);
        return token;
    }

    public static DecodedJWT decodeJWT(String token){
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET))
                    .withIssuer("ImageHost.sdu.dk")
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            return jwt;
        } catch (JWTVerificationException exception){
            //Invalid signature/claims
            System.out.println("Invalid token");
            return null;
        }
    }

    public static String PassHash(String password) {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(password.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public static String generateRandomHexToken(int byteLength) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[byteLength];
        secureRandom.nextBytes(token);
        return new BigInteger(1, token).toString(16); // Hexadecimal encoding
    }

    public static void main(String[] args) {
        System.out.println(generateRandomHexToken(4));
    }
}
