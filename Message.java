import java.util.Date;
import java.sql.Timestamp;

public class Message {
    String msg;
    Date date;
    public Timestamp timestamp;
    String userId;
    String encryptedMsg; // base64

    public Message(String msg, String userId) {
        this.msg = msg;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.userId =  userId;
    }

    public String messageTimestampConcatFormat() {
        return "Date: " + this.timestamp.toString() + "\n" + "Message: " + this.msg;
    }

    public String messageTimestampEncryptConcat() {
        return this.timestamp.toString() + this.msg;
    }

    // public String base64EncodedTimestamp() {
    //     Encoder encoder = Base64.getEncoder();
    //     encoder.
    // }

    public String messageRecipientIdConcat(String recipientId) {
        return recipientId +":"+this.msg;
    }

    public static String formatMessageListToSingleString(Message[] msgs) {
        String finalMsgString = "";
        for (Message message : msgs) {
            finalMsgString = finalMsgString +"\n\n"+ message.messageTimestampConcatFormat();
        }

        return finalMsgString.trim();
    }

    public static String returnTimestampString(String msg) {
        String[] unprocessedTimestamp = msg.split("\n");
        String[] splitMessage = unprocessedTimestamp[0].split(":");
        return splitMessage[0].trim();
    }
}
