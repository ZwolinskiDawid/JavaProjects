package server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

class Receive implements Runnable {

    private Socket server;
    
    Receive(Socket server) {
        this.server = server;
    }

    @Override
    public void run() {
        
        String name = null;
        
        try {
            InputStream is = server.getInputStream();
            try (ObjectInputStream ois = new ObjectInputStream(is)) {
                name = (String) ois.readObject();
                
                try (FileOutputStream fos = new FileOutputStream("C:\\Users\\Dawid\\Desktop\\Pobrane\\" + name)) {
                    int dataSize;
                    byte[] buffor = new byte[100];
                    
                    while((dataSize = ois.read(buffor)) != -1)
                    {
                        fos.write(buffor, 0, dataSize);
                    }
                }
            }            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Receive the file: " + name);
        
    }
    
}
