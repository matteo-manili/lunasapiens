package com.lunasapiens.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lunasapiens.Constants;
import com.lunasapiens.config.JwtElements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

@Service
public class JwtService {


    @Autowired
    private JwtElements.JwtKeys getJwtRsaKeys;


    public JwtElements.JwtToken generateToken(String emailUtente) {
        try {
            String publicKeyB64 = getJwtRsaKeys.getKeyPublic();
            String privateKeyB64 = getJwtRsaKeys.getKeyPrivate();

            System.out.println("publicKeyB64 JwtService: " + publicKeyB64);
            System.out.println("privateKeyB64 JwtService: " + privateKeyB64);

            RSAPublicKey rSAPublicKey = decodificaChiaveJwtPublic(publicKeyB64);
            RSAPrivateKey rSAPrivateKey = decodificaChiaveJwtPrivate(privateKeyB64);

            Algorithm algorithm = Algorithm.RSA256(rSAPublicKey, rSAPrivateKey);

            // Calcola la data di scadenza a 7 giorni da ora
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            Date expiresAt = calendar.getTime();


            JwtElements.JwtToken jwtConfigInfo = new JwtElements.JwtToken(
                    JWT.create()
                    .withIssuer( Constants.JWT_WITH_ISSUER )
                    .withSubject( emailUtente )
                    .withExpiresAt(expiresAt)  // Imposta la data di scadenza
                    .sign(algorithm)

            );

            return jwtConfigInfo;


        } catch (JWTCreationException exception) {
            exception.printStackTrace();
            return null; // Token creation failed
        }
    }



    public JwtElements.JwtEmail validateTokenAndGetEmail(String codeTokenJwt) {
        try {
            String publicKeyB64 = getJwtRsaKeys.getKeyPublic();
            String privateKeyB64 = getJwtRsaKeys.getKeyPrivate();

            RSAPublicKey rSAPublicKey = decodificaChiaveJwtPublic(publicKeyB64);
            RSAPrivateKey rSAPrivateKey = decodificaChiaveJwtPrivate(privateKeyB64);

            DecodedJWT decodedJWT;
            Algorithm algorithm = Algorithm.RSA256(rSAPublicKey, rSAPrivateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    // specify any specific claim validations
                    .withIssuer( Constants.JWT_WITH_ISSUER )
                    // reusable verifier instance
                    .build();

            decodedJWT = verifier.verify(codeTokenJwt);

            // Se arriviamo qui, il token è valido
            //System.out.println("Token valido!");
            //System.out.println("Issuer: " + decodedJWT.getIssuer());
            //System.out.println("Claims: " + decodedJWT.getClaims());
            //System.out.println("email: " + decodedJWT.getSubject());


            JwtElements.JwtEmail jwtConfigEmail = new JwtElements.JwtEmail(
                    decodedJWT.getSubject()
            );

            return jwtConfigEmail;

        } catch (JWTVerificationException exception) {
            System.out.println("Token non valido: " + exception.getMessage());
            return null; // Token non valido
        }
    }




    public static RSAPublicKey decodificaChiaveJwtPublic(String publicKeyB64){
        // Decodifica Base64
        byte[] publicKeyDecoded = Base64.getDecoder().decode(publicKeyB64);
        // Crea la chiave pubblica da X.509
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyDecoded);
        RSAPublicKey publicKey = null;
        try {
            publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }





    public static RSAPrivateKey decodificaChiaveJwtPrivate(String privateKeyB64){
        byte[] privateKeyDecoded = Base64.getDecoder().decode(privateKeyB64);
        // Crea la chiave privata da PKCS#8
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyDecoded);
        RSAPrivateKey rSAPrivateKey = null;
        try {
            rSAPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return rSAPrivateKey;
    }

}
