import java.io.*;
import java.net.*;
import java.util.HashMap;

public class BackUpServer {
    
}

class Server {

	public static void main(String [] args) throws Exception {

        if (args.length < 1) {
            System.out.println("You must supply a port number to start the server");
            System.exit(1);
        }

		int port = Integer.parseInt(args[0]);

        HashMap<String, String> msgs = new HashMap<String, String>();

		ServerSocket ss = new ServerSocket(port);
        
		System.out.println("Waiting incoming connection...");

		Socket s = ss.accept();
        System.out.println("Client connection established");

		DataInputStream dis = new DataInputStream(s.getInputStream());

        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

		String x = null;

		try {
			while ((x = dis.readUTF()) != null) {

				System.out.println(x);

			}
		}
		catch(IOException e) {
			System.err.println("Client closed its connection.");
		}
	}
}
