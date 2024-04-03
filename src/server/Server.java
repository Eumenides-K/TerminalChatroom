package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Server {
    static List<Socket> list = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(6666);

        FileInputStream fis = new FileInputStream("assets/userinfo.txt");
        Properties props = new Properties();
        props.load(fis);
        fis.close();

        while (true) {
            Socket socket = ss.accept();

            String clientIP = socket.getInetAddress().getHostAddress();
            int clientPort = socket.getPort();
            System.out.println("Successfully connected with " + clientIP + ":" + clientPort);

            ServerRunnable sr = new ServerRunnable(socket, props);
            new Thread(sr).start();
        }
    }
}
