package GroupCommunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Member {

    static BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
    static Socket groupCoordinator;

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.print("\nEnter your name: ");
        String name = console.readLine();
        System.out.println("\n");

        groupCoordinator = new Socket("localhost", 9999);

        PrintWriter writer = new PrintWriter(groupCoordinator.getOutputStream(), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(groupCoordinator.getInputStream()));

        writer.println(name);

        var getMessages = new DisplayMessages(reader);
        var sendMessages = new SendMessage(writer);

        getMessages.start();
        sendMessages.start();

        getMessages.join();
        sendMessages.interrupt();

        groupCoordinator.close();

    }



    private static class DisplayMessages extends Thread {
        private final BufferedReader groupReader;

        public DisplayMessages(BufferedReader groupReader) {
            this.groupReader = groupReader;
        }

        @Override
        public void run() {
            while (true){
                try {
                    String message = groupReader.readLine();
                    if (message.equals("/QUIT")) return;
                    System.out.println(message + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

            }
        }
    }


    private static class SendMessage extends Thread {
        private final PrintWriter groupWriter;

        public SendMessage(PrintWriter groupWriter) {
            this.groupWriter = groupWriter;
        }

        @Override
        public void run() {
            while (true) {
                String message = "";
                try {
                    message = console.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } finally {
                    if (message.length() > 0)
                        groupWriter.println(message);
                }
            }
        }
    }


}
