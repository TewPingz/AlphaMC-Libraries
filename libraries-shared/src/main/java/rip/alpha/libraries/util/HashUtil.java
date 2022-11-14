package rip.alpha.libraries.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HashUtil {

    private static MessageDigest MESSAGE_DIGEST;

    static {
        try {
            MESSAGE_DIGEST = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String encryptThisString(String input) {
        byte[] messageDigest = MESSAGE_DIGEST.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);

        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }

        return hashtext;
    }
}
