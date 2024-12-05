package IPC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Consumer {

    public static void main(String[] args) throws IOException {

        Socket producer = new Socket("localhost", 9999);

        PrintWriter request = new PrintWriter(producer.getOutputStream(), true);
        BufferedReader receive = new BufferedReader(new InputStreamReader(producer.getInputStream()));
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("\nName: ");
        String name = console.readLine();
        request.println(name);

        while (true) {
            System.out.print("\nPlace request for Product Id: ");
            String pdt_id = console.readLine();

            request.println(pdt_id);
            System.out.println("\nOrder request sent to Producer.");

            String product = receive.readLine().replaceAll("<br>", "\n");
            if (product.equals("QUIT")) {
                System.out.println("Closing Connection.");
                producer.close();
                return;
            }

            System.out.println("\nReceived Product: \n" + product);
        }

    }

}
