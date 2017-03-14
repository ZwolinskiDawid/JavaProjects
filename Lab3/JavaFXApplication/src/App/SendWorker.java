package App;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Path;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SendWorker extends Task<Void> {
    private Path file;
    private String server;
    private int port;
    private ProgressGUIController controller;

    SendWorker(Path selectedFile, String serverName, int PortNumber, ProgressGUIController controller) {
        this.file = selectedFile;
        this.server = serverName;
        this.port = PortNumber;
        this.controller = controller;
    }

    @Override
    protected Void call() throws InterruptedException, IOException
    {
        updateMessage("Conecting");
        Thread.sleep(200);
        Socket socket = new Socket(server, port);
        OutputStream os = socket.getOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeObject(file.getFileName().toString()); //push name of file
            
            try (FileInputStream fis = new FileInputStream(file.toFile())) {
                int available = fis.available();
                int licznik = 0;
                int dataSize;
                byte[] buffor = new byte[100];
                
                while((dataSize = fis.read(buffor)) != -1)
                {
                    oos.write(buffor, 0, dataSize);
                    updateMessage("Sending " + file.getFileName().toString());
                    Thread.sleep(20);
                    licznik += dataSize;
                    updateProgress(licznik, available);
                }
                updateMessage("Done");
            }
        }
        return null;
    }
}
