import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import java.sql.Timestamp;

public class Client {
    public static void main(String[] args) throws Exception {
        // check if client is started properly
        if (args.length < 2) {
            System.err.println("You need to indicate a host and port number to start the Client program");
            System.exit(1);
        }
        // IOException invalidException = new IOException("Invalid user id");

        // create signature

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        Socket client = new Socket(host, port);

        // Set up input and output streams
        DataOutputStream dos = new DataOutputStream(client.getOutputStream());

        DataInputStream dis = new DataInputStream(client.getInputStream());

        // supply user id
        Scanner sc = new Scanner(System.in);
        String signUpMsg = dis.readUTF();
        System.out.println(signUpMsg);
        String userId = sc.nextLine();
        
        byte[] hashedUserId = Utils.getMD5Hash(userId);
        String userIdBase64 = Utils.base64BytestoString(hashedUserId);
        // send the hashed user id in base64 format
        dos.writeUTF(userIdBase64);
        // sc.close();
        System.out.println(dis.readUTF()); // read back the user id

        // read prv key
        byte[] prvKeyBytes = Utils.readPrvKeys(userId);
        PrivateKey prvKey = Utils.getPrvKeyInstance(prvKeyBytes);

        byte[] serverPubKeyBytes = Utils.readPubKeys("server");
        PublicKey serverPubKey = Utils.getPubKeyInstance(serverPubKeyBytes);

        // Receive messages sent
        String x = null;
        try {
            System.out.println(dis.readUTF());
            while (!(x = dis.readUTF()).equals("/end")) {
                // System.out.println("A loop");
                if (x.equals("/end") || x.equals("You have 0 message(s)")) {
                    break;
                }
                int count = 0;
                String encryptedMsg = null;
                String timestampString = null;
                String signatureString = null;
                while (!x.equals("/next")) {
                    switch (count) {
                        case 0:
                            signatureString = x;
                            // System.out.println("signatureString: " + signatureString);
                            break;
                        case 1:
                            encryptedMsg = x;
                            // System.out.println("encryptedMsg: " + encryptedMsg);
                            break;
                        case 2:
                            timestampString = x;
                            // System.out.println("timestampString: " + timestampString);
                            break;
                    }
                    count++;
                    x = dis.readUTF();
                }
                // verify signature
                Signature sig = Signature.getInstance("SHA256withRSA");
                sig.initVerify(serverPubKey);
                // timestampString = timestampString.split("e:")[1].trim();
                // String timestampms = Utils.convertTimestampToMillisecondsString(Utils.convertStringToTimestamp(timestampString));
                // String rawAString = Utils.base64StringtoBytes(encryptedMsg).toString();
                byte[] rawAsbytes = Utils.base64StringtoBytes(encryptedMsg);
                String rawAString = Arrays.toString(rawAsbytes);
                String concat = timestampString.concat(rawAString);
                sig.update(concat.getBytes());
                byte[] base64SigBytes = Utils.base64StringtoBytes(signatureString);
                byte[] sigBytes = Utils.fromBase64BytesToDefaultBytes(base64SigBytes);
                // boolean b = sig.verify(signatureString.getBytes("UTF8"));

                boolean b = sig.verify(sigBytes);
    
                if (!b) {
                    System.out.println("Signature verification failed. Aborting...");
                    sc.close();
                    client.close();
                    System.exit(1);
                }
                // Decrypt message
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, serverPubKey);
                byte[] msgBytes = cipher.doFinal(Utils.base64StringtoBytes(encryptedMsg)); //encryptedMsg.getBytes()
                String originalMsg = new String(msgBytes, "UTF8");
                Timestamp ts = new Timestamp(Long.parseLong(timestampString));
                System.out.println("=======================================");
                System.out.println("Date: " + ts.toString());
                System.out.println("Message: " + originalMsg);
            }
        } catch (Exception e) {
            System.err.println("Could not process all messages due to an error. Sorry :(");
            e.printStackTrace();
        }
    
        System.out.println("\n");
        System.out.println("Done reading messages");
        System.out.println("\n");

        // Confirm if there is a message to send
        // sc = new Scanner(System.in);
        System.out.println("Would you like to send a message? Y/N");
        String send_msg = sc.nextLine().toLowerCase();
        send_msg = send_msg.trim();
 
        if (send_msg.equals("n")) {
            System.out.println("No messages to send. Good Bye!");
            sc.close();
            client.close();
            System.exit(0);
        }

/* ------------------------------------------- SENDING MESSAGE -------------------------------------------------------- */
        System.out.println("Enter Recipient id: "); // Recipient id prompt
        String recipientID = sc.nextLine().toLowerCase();

        System.out.println("Enter message: "); // Message prompt
        String msgString = sc.nextLine();
        Message msg = new Message(msgString, userId);

        // Encryption cipher
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, prvKey);
        byte[] raw = cipher.doFinal(msg.messageRecipientIdConcat(recipientID).getBytes());

        // Get private key signed signature
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(prvKey);

        String rawAsString = Arrays.toString(raw);

        String timestampms = Utils.convertTimestampToMillisecondsString(msg.timestamp);


        String concat = timestampms.concat(rawAsString);
        sig.update(concat.getBytes());
        byte[] signature = sig.sign();


        byte[] base64SigBytes = Utils.byteToBase64Byte(signature);
        String encodedSig = Utils.base64BytestoString(base64SigBytes);



        // Sender id
        dos.writeUTF(msg.userId);
        // timestamp
        dos.writeUTF(timestampms);
        // signature
        dos.writeUTF(encodedSig);
        // encrypted message
        dos.writeUTF(Utils.base64BytestoString(raw));


        System.out.println("\nMessage sent");

        Thread.sleep(1000);

        client.close();
    
  }
}
