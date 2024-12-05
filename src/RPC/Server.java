package RPC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("\nServer Socket Created.");
        Socket clientSocket =  serverSocket.accept();
        System.out.println("\nConnected with Client " + clientSocket.toString());

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        System.out.println("\nReceiving Data from Client ...");
        String inputLine = in.readLine();
        System.out.println("Sending Back the same data to Client ...");
        out.println(inputLine);
        System.out.println("Data Sent Successfully.");

        clientSocket.close();
        System.out.println("\nClosed connection with Client " + clientSocket.toString());

    }
}
