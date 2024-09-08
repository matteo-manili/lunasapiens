package com.lunasapiens.config;


import com.auth0.jwt.interfaces.Claim;

import java.util.Date;
import java.util.Map;


public class JwtElements {



    /**
     * Classe 1
     */
    public static class JwtDetails {

        private boolean success;
        private boolean tokenScaduto;
        private String messaggioErroreJwt;
        //Restituisce l'issuer (l'emittente) del token JWT. Questo è il campo iss nel payload del JWT.
        private String issuer;
        // è un metodo che restituisce il subject (soggetto) del token JWT. Il subject è un campo (sub) standard nel payload di un JWT e solitamente
        // rappresenta l'identità principale a cui il token si riferisce, come l'ID utente, il nome utente o un altro identificatore univoco dell'utente.
        private String subject;
        // Restituisce la data e l'ora di scadenza del token JWT (exp claim). Questo valore indica quando il token non sarà più valido.
        private Date expiresAt;
        // Restituisce una mappa (Map<String, Claim>) di tutti i claims presenti nel token JWT. I claims sono coppie chiave-valore
        // che rappresentano le informazioni trasmesse nel token, come l'utente, i ruoli, o altre informazioni personalizzate.
        private Map<String, Claim> claims;
        // Restituisce l'header del token JWT come stringa in formato Base64URL. L'header contiene informazioni su come il token è stato firmato
        // e quali algoritmi sono stati usati.
        private String header;
        private String token;



        public JwtDetails(boolean success, boolean tokenScaduto, String messaggioErroreJwt) {
            this.success = success;
            this.tokenScaduto = tokenScaduto;
            this.messaggioErroreJwt = messaggioErroreJwt;
        }

        public JwtDetails(boolean success, boolean tokenScaduto, String messaggioErroreJwt, String issuer, String subject, Date expiresAt, Map<String, Claim> claims, String header, String token) {
            this.success = success;
            this.tokenScaduto = tokenScaduto;
            this.messaggioErroreJwt = messaggioErroreJwt;
            this.issuer = issuer;
            this.subject = subject;
            this.expiresAt = expiresAt;
            this.claims = claims;
            this.header = header;
            this.token = token;
        }


        public boolean isSuccess() { return success; }

        public boolean isTokenScaduto() { return tokenScaduto; }

        public String getMessaggioErroreJwt() { return messaggioErroreJwt; }

        public String getIssuer() { return issuer; }

        public String getSubject() { return subject; }

        public Date getExpiresAt() { return expiresAt; }

        public Map<String, Claim> getClaims() { return claims; }

        public String getHeader() { return header; }

        public String getToken() { return token; }


    }


    /**
     * Classe 2
     */
    public static class JwtToken {
        private String token;
        public JwtToken(String token) {
            this.token = token;
        }
        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }
    }


    /**
     * Classe 3
     */
    public static class JwtRsaKeys {
        private String keyPublic;
        private String keyPrivate;
        public JwtRsaKeys(String keyPublic, String keyPrivate) {
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

}
