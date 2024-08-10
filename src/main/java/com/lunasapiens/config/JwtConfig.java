package com.lunasapiens.config;

public class JwtConfig {



    /*

        //Questo codice Serve a generare le KIAVI RSA da Java

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        // Converti le chiavi in formato Base64
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        // Stampa le chiavi Base64
        System.out.println("jwt.rsa.public.key=" + publicKeyBase64);
        System.out.println("jwt.rsa.private.key=" + privateKeyBase64);

 */




    private String keyPublic;
    private String keyPrivate;


    public JwtConfig(String keyPublic, String keyPrivate) {
        this.keyPublic = keyPublic;
        this.keyPrivate = keyPrivate;
    }



    public String getKeyPublic() {
        return keyPublic;
    }

    public String getKeyPrivate() {
        return keyPrivate;
    }
}
