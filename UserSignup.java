import java.io.FileOutputStream;
import java.security.*;
import java.util.Scanner;

class UserSignup {
    public static void main(String[] args) throws Exception {
        System.out.println("Please input your user name (preferably you first name): ");
        Scanner sc = new Scanner(System.in);

        String userName = sc.nextLine();
        sc.close();
        
        /*  Start generating the public private key pair */

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");

        kpg.initialize(2048);
        KeyPair kp = kpg.genKeyPair();

        FileOutputStream fos = new FileOutputStream("keys/"+userName+".pub");
        fos.write(kp.getPublic().getEncoded());
        fos.close();

        fos = new FileOutputStream("keys/"+userName+".prv");
        fos.write(kp.getPrivate().getEncoded());
        fos.close();
        
    }
}