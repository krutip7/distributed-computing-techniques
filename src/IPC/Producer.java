package IPC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class Producer {

    public static void main(String[] args) throws IOException {
        ServerSocket producer = new ServerSocket(9999);
        var pool = Executors.newFixedThreadPool(10);
        while (true)
            pool.execute(new ConsumerHandler(producer.accept()));
    }

    static class ConsumerHandler implements Runnable{
        private final String name;
        private final Socket consumer;
        private final PrintWriter writer;
        private final BufferedReader reader;

        public ConsumerHandler(Socket consumer) throws IOException {
            this.consumer = consumer;
            this.writer = new PrintWriter(consumer.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(consumer.getInputStream()));
            this.name = this.reader.readLine();
            System.out.println("\nCONNECTED TO CONSUMER " + this.name + " : " + this.consumer.toString());
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String pdt_id = this.reader.readLine();
                    if (pdt_id.equals("NULL")){
                        this.writer.println("QUIT");
                        System.out.println("\nDISCONNECTED WITH CONSUMER " + this.name + " : "  + this.consumer.toString());
                        this.consumer.close();
                        return;
                    }
                    System.out.println("\nReceived request for Product No. " + pdt_id + " from consumer " + this.name);

                    Product pdt = new Product(pdt_id);
                    System.out.println("\nProducing requested product " + pdt_id + " for " + this.name);

                    this.writer.println(pdt.toString().replaceAll("\n", "<br>"));
                    System.out.println("\nProduct delivered to consumer " + this.name);

                } catch (IOException e) {
                    return;
                }
            }
        }
    }
}
