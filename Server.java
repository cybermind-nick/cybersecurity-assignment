import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.security.*;
import javax.crypto.*;
import java.sql.Timestamp;

class Server {

	public static void main(String [] args) throws Exception {

        if (args.length < 1) {
            System.out.println("You must supply a port number to start the server");
            System.exit(1);
        }

		int port = Integer.parseInt(args[0]);

        HashMap<String, Message[]> msghm = new HashMap<String, Message[]>();

		ServerSocket ss = new ServerSocket(port);
        // IOException invalidException = new IOException("Invalid user id");
        while (true) {
            try {
                System.out.println("Waiting incoming connection...");

                Socket s = ss.accept();
                System.out.println("Client connection established");
                
                // set up input and output streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
        
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                // read server private key
                byte[] prvKeyBytes = Utils.readPrvKeys("server");
                PrivateKey serverPrvKey = Utils.getPrvKeyInstance(prvKeyBytes);
                // request user id
                dos.writeUTF("Input user id: ");
    
                String hashedUserId = dis.readUTF();

                System.out.println("user id: "+ hashedUserId);

                dos.writeUTF("user_id: " + hashedUserId);



                if (msghm.get(hashedUserId) != null) {
                    Message[] msgList = msghm.get(hashedUserId);

                    dos.writeUTF("You have " + msgList.length + " message(s)");
                    for (Message message : msgList) {
                        // get cipher signature
                        Signature sig = Signature.getInstance("SHA256withRSA");
                        sig.initSign(serverPrvKey);
                        String timestampms = Utils.convertTimestampToMillisecondsString(message.timestamp);
                        String rawAString = Arrays.toString(Utils.base64StringtoBytes(message.msg));
                        String concat = timestampms.concat(rawAString);
                        // sig.update(message.messageTimestampEncryptConcat().getBytes());
                        sig.update(concat.getBytes());
                        byte[] signature = sig.sign();

                        // send signature

                        byte[] base64SigBytes = Utils.byteToBase64Byte(signature);
                        String stringsig = Utils.base64BytestoString(base64SigBytes);

                        dos.writeUTF(stringsig);
                        // send encrypted content and timestamp
                        dos.writeUTF(message.msg);

                        dos.writeUTF(timestampms);
                        dos.writeUTF("/next");

                    }
                    dos.writeUTF("/end");

                    dos.flush();

                    msghm.remove(hashedUserId);
            
                } else {
                    dos.writeUTF("You have " + 0 + " message(s)");
                    dos.writeUTF("/end");
                }

                String unhashedUserId = dis.readUTF();

                String timestampmsString = dis.readUTF();

                String signatureStringB64 = dis.readUTF();

                String rawBase64 = dis.readUTF();
            
                byte[] userbytes = Utils.readPubKeys(unhashedUserId);
                PublicKey userPubKey = Utils.getPubKeyInstance(userbytes);

                // verify signature
                Signature sig = Signature.getInstance("SHA256withRSA");
                sig.initVerify(userPubKey);
                String rawAString = Arrays.toString(Utils.base64StringtoBytes(rawBase64));

                String concat = timestampmsString.concat(rawAString);
                sig.update(concat.getBytes());
                byte[] base64SigBytes = Utils.base64StringtoBytes(signatureStringB64);
                byte[] decodedSig = Utils.fromBase64BytesToDefaultBytes(base64SigBytes);

                Boolean b = sig.verify(decodedSig);
                if (!b) {
                    System.out.println("User Signature verification failed. Aborting...");
                    dos.writeUTF("User Signature verification failed. Aborting...");
                    continue;
                } else {
                    System.out.println("Message verified!!!");
                    dos.writeUTF("Message verified!!!");
                }

                // decrypt
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, userPubKey);
                byte[] rawBytes = cipher.doFinal(Utils.base64StringtoBytes(rawBase64));
                String decryptedMsg = new String(rawBytes, "UTF8");

                String recipeintId = decryptedMsg.split(":")[0];
                // get hash
                byte[] hashedRecipientId = Utils.getMD5Hash(recipeintId);
                String recipeintIdBase64 = Utils.base64BytestoString(hashedRecipientId);


                // encrypt with server key
                cipher.init(Cipher.ENCRYPT_MODE, serverPrvKey);
                byte[] raw = cipher.doFinal(decryptedMsg.split(":")[1].getBytes());

                // create message
                Message storedMsg = new Message(Utils.base64BytestoString(raw), recipeintId);
                storedMsg.timestamp = new Timestamp(Long.parseLong(timestampmsString));
                
                Message[] msgArr = msghm.get(recipeintIdBase64);
                if (msgArr == null) {
                    Message[] arr = new Message[]{storedMsg};
                    msghm.put(recipeintIdBase64, arr);

                } else {
                    Message[] newArr = Arrays.copyOf(msgArr, msgArr.length + 1);
                    newArr[msgArr.length] = storedMsg;

                    msghm.put(recipeintIdBase64, newArr);
                }

                System.out.println("Message Stored for recipient");
                


            }  catch(IOException e) {
                System.err.println("Client connection closed");
            }
        }
	}
}