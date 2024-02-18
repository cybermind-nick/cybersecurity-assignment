import javax.crypto.*;
import java.security.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

public class Client {
  public static void main(String[] args) {
    
      String host = args[0];
      int port = Integer.parseInt(args[1]);
      String userId = args[2];
    
    if (args.length != 3) {
        System.out.println("Please input 3 arguments: host, port, and user id");
        return;
    }

    // Load the client's private key and the server's public key
    
  }
}
