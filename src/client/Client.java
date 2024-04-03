package client;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 6666);
        System.out.println("Successfully connected with the server...");

        while (true) {
            System.out.println("=========== Welcome to Terminal Chatroom :-) =============");
            System.out.println("1 Sign in");
            System.out.println("2 Sign up");
            System.out.println("Please enter your choice:");
            Scanner sc = new Scanner(System.in);
            String choice = sc.nextLine();
            if ("1".equals(choice)) {
                signIn(socket);
            } else if ("2".equals(choice)) {
                signUp(socket);
            } else {
                System.out.println("Please enter 1 for sign in or 2 for sign up.");
            }
        }
    }

    public static void signIn(Socket socket) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your username:");
        String username = sc.nextLine();
        System.out.println("Please enter your password:");
        String password = sc.nextLine();


        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        bw.write("1"); // sign in indicator
        bw.newLine();
        bw.flush();
        bw.write(username + "&" + password);
        bw.newLine();
        bw.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        try {
            socket.setSoTimeout(5000);
            String flag = br.readLine();
            socket.setSoTimeout(0);
            if ("1".equals(flag)) {
                System.out.println("Successfully signed in, enjoy your chat!");
                MessageReceiver mr = new MessageReceiver(socket);
                mr.start();
                chat(bw);
            } else if ("2".equals(flag)) {
                System.out.println("The password is wrong, please try again!");
            } else if ("3".equals(flag)) {
                System.out.println("Invalid username, please sign up first!");
            } else {
                System.out.println("Unknown error happens. Please try again...");
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Request timed out. Please try again...");
        }

    }

    public static void chat(BufferedWriter bw) throws IOException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Please type your message...");
            String message = sc.nextLine();
            bw.write(message);
            bw.newLine();
            bw.flush();
        }
    }

    public static void signUp(Socket socket) throws IOException {
        Scanner sc = new Scanner(System.in);

        String username;
        while (true) {
            System.out.println("Please enter a username consisting only of uppercase and lowercase English letters (case-sensitive):");
            username = sc.nextLine();
            if (!username.matches("^[a-zA-Z]+$")) {
                System.out.println("The username is invalid. It can only be made up of uppercase and lowercase English letters (case-sensitive)...");
            } else if (username.length() > 18) {
                System.out.println("The username is too long...");
            } else if (username.length() < 6) {
                System.out.println("The username is too short...");
            } else {
                break;
            }
        }

        String password;
        while (true) {
            System.out.println("Please enter a password consisting only numbers:");
            password = sc.nextLine();
            if (!password.matches("^[0-9]+$")) {
                System.out.println("The username is invalid. It can only be made up of numbers...");
            } else if (password.length() > 18) {
                System.out.println("The password is too long...");
            } else if (password.length() < 6) {
                System.out.println("The password is too short...");
            } else {
                break;
            }
        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bw.write("2");
        bw.newLine();
        bw.flush();

        bw.write(username + "&" + password);
        bw.newLine();
        bw.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        try {
            socket.setSoTimeout(5000);
            String response = br.readLine();
            socket.setSoTimeout(0);
            if ("1".equals(response)) {
                System.out.println("You have successfully signed up. Please sign in.");
            } else {
                System.out.println("Unknown error happens. Please try again...");
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Request timed out. Please try again...");
        }

    }

}
