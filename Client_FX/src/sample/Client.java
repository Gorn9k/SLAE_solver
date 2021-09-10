package sample;

import org.json.JSONObject;
import java.io.*;
import java.net.Socket;

public class Client {

    static JSONObject line;

    public void client_go() throws Exception {
        try {
            Socket sock = new Socket("localhost", 13267);
            System.out.println("Connecting...");
            ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
            oos.writeObject(line.toString());
            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
            try {
                line = new JSONObject((String) ois.readObject());
            } catch (Throwable e) {
                line.put("result", "Error");
            }
            oos.close();
            ois.close();
            sock.close();
        } catch (Throwable e){
            line.put("result", "Server_error");
        }
    }
}
