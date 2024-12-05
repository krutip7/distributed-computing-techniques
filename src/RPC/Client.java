package RPC;

import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {

        Socket echoSocket = new Socket("localhost", 9999);

        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);

        BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\nConnected with the Server\n");

        System.out.print("Enter Data: ");
        String userInput = stdIn.readLine();

        System.out.println("\nSending Data to Server ...");
        out.println(userInput);
        System.out.println("Data Sent Successfully.\n");

        System.out.println("Receiving Data from Server ... ");
        String receivedData = in.readLine();
        System.out.println("Received Data: " + receivedData);

        System.out.println("\nClosing connection with Server ...");
        echoSocket.close();
        System.out.println("Connection closed.\n");

    }
}
