package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {
        
        ServerSocket serverSocket = new ServerSocket(9999);
            
        while (true) {
            Socket server = serverSocket.accept();
            Receive rec = new Receive(server);
            new Thread(rec).start();
        }
        
    }
    
}
