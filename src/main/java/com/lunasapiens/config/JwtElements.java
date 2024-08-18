package com.lunasapiens.config;




public class JwtElements {


    /**
     * Classe
     */
    public static class JwtEmail {
        private String email;
        public JwtEmail(String email) {
            this.email = email;
        }
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
    }



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



    public static class JwtKeys {
        private String keyPublic;
        private String keyPrivate;
        public JwtKeys(String keyPublic, String keyPrivate) {
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
