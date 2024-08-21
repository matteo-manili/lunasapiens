package com.lunasapiens.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lunasapiens.Constants;
import com.lunasapiens.config.JwtElements;
import com.lunasapiens.config.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

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
            //calendar.add(Calendar.MINUTE, 1);
            Date expiresAt = calendar.getTime();

            JwtElements.JwtToken jwtConfigInfo = new JwtElements.JwtToken(
                    JWT.create()
                    .withIssuer( Constants.JWT_WITH_ISSUER )
                    .withSubject( emailUtente )
                    .withExpiresAt(expiresAt)  // Imposta la data di scadenza
                    .sign(algorithm));

            return jwtConfigInfo;

        } catch (JWTCreationException exception) {
            exception.printStackTrace();
            return null; // Token creation failed
        }
    }


    public JwtElements.JwtDetails validateToken(String codeTokenJwt) {
        try {
            String publicKeyB64 = getJwtRsaKeys.getKeyPublic();
            String privateKeyB64 = getJwtRsaKeys.getKeyPrivate();

            RSAPublicKey rSAPublicKey = decodificaChiaveJwtPublic(publicKeyB64);
            RSAPrivateKey rSAPrivateKey = decodificaChiaveJwtPrivate(privateKeyB64);

            Algorithm algorithm = Algorithm.RSA256(rSAPublicKey, rSAPrivateKey);

            JWTVerifier verifier = JWT.require(algorithm)
                    // specify any specific claim validations
                    .withIssuer( Constants.JWT_WITH_ISSUER )
                    // reusable verifier instance
                    .build();

            DecodedJWT decodedJWT = verifier.verify(codeTokenJwt);

            //System.out.println("decodedJWT.getIssuer(): "+decodedJWT.getIssuer());
            //System.out.println("decodedJWT.getSubject():"+decodedJWT.getSubject());
            //System.out.println("decodedJWT.getExpiresAt(): "+decodedJWT.getExpiresAt());
            //System.out.println("decodedJWT.getHeader():"+decodedJWT.getHeader());
            //System.out.println("decodedJWT.getClaims(): "+decodedJWT.getClaims());

            return new JwtElements.JwtDetails(true, false, null, decodedJWT.getIssuer(), decodedJWT.getSubject(),
                    decodedJWT.getExpiresAt(), decodedJWT.getClaims(), decodedJWT.getHeader(), decodedJWT.getToken()) ;


        } catch (JWTVerificationException exceptionJwt) {
            logger.info("JWTVerificationException validateTokenAndGetEmail: " +exceptionJwt.getMessage());
            if( exceptionJwt.getMessage().contains("The Token has expired") ) {
                return new JwtElements.JwtDetails(false, true, null) ;
            }
            return new JwtElements.JwtDetails(false, false, exceptionJwt.getMessage());


        } catch (Exception exception) {
            logger.info("Exception validateTokenAndGetEmail: " +exception.getMessage());
            return new JwtElements.JwtDetails(false, false, exception.getMessage());
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
