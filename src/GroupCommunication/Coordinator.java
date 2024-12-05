package GroupCommunication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Coordinator {

    static String group_name;
    static HashSet<PrintWriter> group = new HashSet<>();

    public static void main(String[] args) throws IOException {

        ServerSocket groupCoordinator = new ServerSocket(9999);
        Scanner sc = new Scanner(System.in);
        System.out.print("\nGroup Name: ");
        group_name = sc.nextLine();
        System.out.println("\nGroup Coordinator for '" + group_name + "' has been set up.");

        var pool = Executors.newFixedThreadPool(10);
        while (true)
            pool.execute(new MemberHandler(groupCoordinator.accept()));

    }


    private static class MemberHandler implements Runnable {
        private final Socket member;
        private final PrintWriter writer;
        private final BufferedReader reader;
        private final String name;

        MemberHandler(Socket member) throws IOException {
            this.member = member;
            this.writer = new PrintWriter(member.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(member.getInputStream()));
            this.name = this.reader.readLine();
            joinGroup();
        }

        @Override
        public void run(){

            while (true) {
                String message;
                try {

                    message = this.reader.readLine();

                    if (message.length() < 1) continue;

                    if (message.toUpperCase().equals("/LEAVE")){
                        leaveGroup();
                        return;
                    }

                    this.writer.println("<sent " + new SimpleDateFormat("HH:mm").format(new Date()) + ">");

                    for (PrintWriter receiver : group)
                        if (receiver != this.writer)
                            receiver.println(this.name + ": " + message);

                }

                catch (IOException e) {
                    leaveGroup();
                    return;
                }

            }

        }

        public void joinGroup(){

            System.out.println("\n<" + this.name + "> " + this.member.toString() + " JOINED GROUP '"  + group_name + "'");

            for (PrintWriter receiver : group)
                receiver.println("\t\t\t\t" + this.name.toUpperCase() + " JOINED GROUP '"  + group_name + "'");

            group.add(this.writer);
            this.writer.println("\t\t\t\tYOU JOINED GROUP '"  + group_name + "'");

        }


        public void leaveGroup(){

            if (this.writer != null) {
                group.remove(this.writer);
                this.writer.println("\t\t\t\tYOU LEFT GROUP '"  + group_name + "'");
                this.writer.println("/QUIT");
            }

            for (PrintWriter receiver : group)
                receiver.println("\t\t\t\t" + this.name.toUpperCase() + " LEFT GROUP '"  + group_name + "'");

            System.out.println("\n<" + this.name + "> " + this.member.toString() + " LEFT GROUP '"  + group_name + "'");

            try {
                this.member.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
