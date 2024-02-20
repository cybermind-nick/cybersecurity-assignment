import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Base64.*;

public class Utils {

    public static byte[] readPubKeys(String userId) throws IOException {
        File f = new File(userId+".pub");
        try {
            return Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            System.err.println("Error reading file");
            System.err.println(e);
            throw e;
        }
        // return null;
    }

    public static byte[] readPrvKeys(String userId) throws IOException {
        File f = new File(userId+".prv");
        try {
            return Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            System.err.println("Error reading file");
            System.err.println(e);
            throw e;
        }
        // return null;
    }

    
    public static byte[] readFiles(String filename) throws IOException {
        try {
            File f = new File(filename);

            byte[] fileBytes = Files.readAllBytes(f.toPath());
            return fileBytes;
        } catch (IOException e) {
            System.err.println(filename + "does not exits");
            System.err.println(e);
            throw e;
        }
    }

    public static byte[] getMD5Hash(String message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return md.digest(message.getBytes());
    }

    public static PrivateKey getPrvKeyInstance(byte[] prvKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(prvKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static PublicKey getPubKeyInstance(byte[] prvKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(prvKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static Timestamp convertStringToTimestamp(String timeString) {
        try {
            DateFormat tsFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = tsFormat.parse(timeString);
            return new Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String convertTimestampToMillisecondsString(Timestamp t) {
       return Long.toString(t.getTime());
        
    }

    public static byte[] byteToBase64Byte(byte[] b) {
        Encoder encoder = Base64.getEncoder();
        return encoder.encode(b);
    }

    public static String base64BytestoString(byte[] b) {
        Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(b);
    }

    public static byte[] base64StringtoBytes(String s) {
        Decoder decoder = Base64.getDecoder();
        return decoder.decode(s);
    }

    public static byte[] fromBase64BytesToDefaultBytes(byte[] b) {
        Decoder decoder = Base64.getDecoder();
        return decoder.decode(b);
    }

    // public static String bytestoString(byte[] b) throws UnsupportedEncodingException {
    //     return new String(b, "UTF8");
    // }
}
