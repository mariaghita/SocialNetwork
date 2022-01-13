package socialnetwork.BCrypt;

public class MyCrypt {
    /**
     *
     * @param password-parola de criptat
     * @return-stringul criptat
     */
    public static String hash(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt(10));
    }

    /**
     *
     * @param password-parola text
     * @param hash-parola criptata
     * @return-true daca hash se potriveste cu parola
     */
    public static boolean verifyHash(String password,String hash){
        return BCrypt.checkpw(password,hash);
    }
}
