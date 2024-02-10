/*
 * @author Declan Onunkwo
 * Spring 2024
 */
package functions;

public class HelperFunctions {

    // simple rng using xor shift
    public static long generateRandomNumber(long r) {
        r ^= r << 13;
        r ^= r >>> 7;
        r ^= r << 17;
        return r;
    }

    public static String encrypt(String message, long key) {
        StringBuilder encryption = new StringBuilder();

        for (int i=0; i<message.length(); i++) {
            encryption.append((char) (message.charAt(i) ^ key));
        }

        return encryption.toString();
    }

    public static String decrypt(String encryption, long key) {
        StringBuilder decryption = new StringBuilder();

        for (int i=0; i<encryption.length(); i++) {
            decryption.append((char) (encryption.charAt(i) ^ key));
        }

        return decryption.toString();
    }
}