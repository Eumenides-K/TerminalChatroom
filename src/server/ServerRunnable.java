package server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class ServerRunnable implements Runnable {
    Socket socket;
    Properties props;

    public ServerRunnable(Socket socket, Properties props) {
        this.socket = socket;
        this.props = props;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String choice = br.readLine();
                switch (choice) {
                    case "1":
                        signIn(br);
                    case "2":
                        signUp(br);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void signIn(BufferedReader br) throws IOException {
        System.out.println(socket.getInetAddress().getHostName() + ":" + socket.getPort() + " is trying to sign in...");
        String userinfo = br.readLine();
        String[] strArr = userinfo.split("&");
        String username = strArr[0];
        String password = strArr[1];

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        if (props.containsKey(username)) {
            if (props.getProperty(username).equals(password)) {
                System.out.println(socket.getInetAddress().getHostName() + ":" + socket.getPort() + " (" + username + ") " + "has successfully signed in...");
                bw.write("1");
                bw.newLine();
                bw.flush();
                Server.list.add(socket);
                boardCast(br, username);
            } else {
                System.out.println(socket.getInetAddress().getHostName() + ":" + socket.getPort() + " (" + username + ") " + " entered a wrong password...");
                bw.write("2");
            }
        } else {
            System.out.println(socket.getInetAddress().getHostName() + ":" + socket.getPort() + " entered an invalid username: " + username);
            bw.write("3");
        }
        bw.newLine();
        bw.flush();
    }

    public void boardCast(BufferedReader br, String username) throws IOException {
        while (true) {
            String comingMessage = br.readLine();
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String message = "(" + time + ") " + username + " sent a message: " + comingMessage;
            System.out.println(message);

            for (Socket s : Server.list) {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                bw.write(message);
                bw.newLine();
                bw.flush();
            }
        }
    }

    public void signUp(BufferedReader br) throws IOException {
        System.out.println(socket.getInetAddress().getHostName() + ":" + socket.getPort() + " is trying to sign up...");
        String userinfo = br.readLine();
        String[] strArr = userinfo.split("&");
        String username = strArr[0];
        String password = strArr[1];
        FileOutputStream fos = new FileOutputStream("assets/userinfo.txt", true);
        props.setProperty(username, password);
        props.store(fos, "");
        fos.close();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bw.write("1");
        bw.newLine();
        bw.flush();
    }
}
