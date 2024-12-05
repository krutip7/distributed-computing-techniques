package ElectionAlgorithms;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class RunningThread implements Runnable {

    private Process process;
    private final int total_processes;
    private static boolean[] messageFlag;
    ServerSocket[] sock;
    Random r;

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public RunningThread(Process process, int total_processes) {
        this.process = process;
        this.total_processes = total_processes;
        this.r = new Random();
        this.sock = new ServerSocket[total_processes];
        RunningThread.messageFlag = new boolean[total_processes];
        for (int i = 0; i < total_processes; i++) {
            RunningThread.messageFlag[i] = false;
        }
    }

    synchronized private void recovery() {
        while (BullyElection.isElectionFlag()) ;//if election is going on then wait
        System.out.println("Process[" + this.process.getPid() + "]: -> Recovered from Crash");
        //Find current co-ordinator.
        try {
            BullyElection.pingLock.lock();
            BullyElection.setPingFlag(false);
            Socket outgoing = new Socket(InetAddress.getLocalHost(), 12345);
            Scanner scan = new Scanner(outgoing.getInputStream());
            PrintWriter out = new PrintWriter(outgoing.getOutputStream(), true);
            System.out.println("Process[" + this.process.getPid() + "]:-> Who is the co-ordinator?");
            out.println("Who is the co-ordinator?");
            out.flush();
            String pid = scan.nextLine();
            String priority = scan.nextLine();
            if (this.process.getPriority() > Integer.parseInt(priority)) { //Bully Condition
                out.println("Resign");
                out.flush();
                System.out.println("Process[" + this.process.getPid() + "]: Resign -> Process[" + pid + "]");
                String resignStatus = scan.nextLine();
                if (resignStatus.equals("Successfully Resigned")) {
                    this.process.setCoOrdinatorFlag(true);
                    sock[this.process.getPid() - 1] = new ServerSocket(10000 + this.process.getPid());
                    System.out.println("Process[" + this.process.getPid() + "]: -> Bullyed current co-ordinator Process[" + pid + "]");
                }
            } else {
                out.println("Don't Resign");
                out.flush();
            }
            BullyElection.pingLock.unlock();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    synchronized private void pingCoOrdinator() {
        try {
            BullyElection.pingLock.lock();
            if (BullyElection.isPingFlag()) {
                System.out.println("Process[" + this.process.getPid() + "]: Are you alive?");
                Socket outgoing = new Socket(InetAddress.getLocalHost(), 12345);
                outgoing.close();
            }
        } catch (Exception ex) {
            BullyElection.setPingFlag(false);
            BullyElection.setElectionFlag(true);
            BullyElection.setElectionDetector(this.process);
            //Initiate Election
            System.out.println("process[" + this.process.getPid() + "]: -> Co-Ordinator is down\n" + "process[" + this.process.getPid() + "]: ->Initiating Election");
        } finally {
            BullyElection.pingLock.unlock();
        }
    }

    private void executeJob() {
        int temp = r.nextInt(20);
        for (int i = 0; i <= temp; i++) {
            try {
                Thread.sleep((temp + 1) * 100);
            } catch (InterruptedException e) {
                System.out.println("Error Executing Thread:" + process.getPid());
                System.out.println(e.getMessage());
            }
        }
    }

    synchronized private boolean sendMessage() {
        boolean response = false;
        try {
            BullyElection.electionLock.lock();
            if (BullyElection.isElectionFlag() && !RunningThread.isMessageFlag(this.process.getPid() - 1) && this.process.priority >= BullyElection.getElectionDetector().getPriority()) {

                for (int i = this.process.getPid() + 1; i <= this.total_processes; i++) {
                    try {
                        Socket electionMessage = new Socket(InetAddress.getLocalHost(), 10000 + i);
                        System.out.println("Process[" + this.process.getPid() + "] -> Process[" + i + "]  responded to election message successfully");
                        electionMessage.close();
                        response = true;
                    } catch (IOException ex) {
                        System.out.println("Process[" + this.process.getPid() + "] -> Process[" + i + "] did not respond to election message");
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                setMessageFlag(true, this.process.getPid() - 1);//My message sending is done
                BullyElection.electionLock.unlock();
                return response;
            } else {
                throw new Exception();
            }
        } catch (Exception ex1) {
            BullyElection.electionLock.unlock();
            return true;
        }
    }

    public static boolean isMessageFlag(int index) {
        return RunningThread.messageFlag[index];
    }

    public static void setMessageFlag(boolean messageFlag, int index) {
        RunningThread.messageFlag[index] = messageFlag;
    }

    synchronized private void serve() {
        try {
            boolean done = false;
            Socket incoming = null;
            ServerSocket s = new ServerSocket(12345);
            BullyElection.setPingFlag(true);
            int temp = this.r.nextInt(5) + 5;// min 5 requests and max 10 requests
            for (int counter = 0; counter < temp; counter++) {
                incoming = s.accept();
                if (BullyElection.isPingFlag()) {
                    System.out.println("Process[" + this.process.getPid() + "]:Yes");
                }
                Scanner scan = new Scanner(incoming.getInputStream());
                PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);
                while (scan.hasNextLine() && !done) {
                    String line = scan.nextLine();
                    switch (line) {
                        case "Who is the co-ordinator?":
                            System.out.println("Process[" + this.process.getPid() + "]:-> " + this.process.getPid());
                            out.println(this.process.getPid());
                            out.flush();
                            out.println(this.process.getPriority());
                            out.flush();
                            break;
                        case "Resign":
                            this.process.setCoOrdinatorFlag(false);
                            out.println("Successfully Resigned");
                            out.flush();
                            incoming.close();
                            s.close();
                            System.out.println("Process[" + this.process.getPid() + "]:-> Successfully Resigned");
                            return;
                        case "Don't Resign":
                            done = true;
                            break;
                    }
                }
            }
            //after serving 5-10 requests go down for random time
            this.process.setCoOrdinatorFlag(false);
            this.process.setDownflag(true);
            try {
                incoming.close();
                s.close();
                sock[this.process.getPid() - 1].close();
                Thread.sleep(15000);//(this.r.nextInt(10) + 1) * 10000);//going down
                recovery();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            sock[this.process.getPid() - 1] = new ServerSocket(10000 + this.process.getPid());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        while (true) {
            if (process.isCoOrdinatorFlag()) {
                //serve other processes
                serve();
            } else {
                while (true) {
                    //Execute some task
                    executeJob();
                    //Ping the co-ordinator
                    pingCoOrdinator();
                    //Do Election
                    if (BullyElection.isElectionFlag()) {
                        if (!sendMessage()) {//elect self as co-ordinator
                            BullyElection.setElectionFlag(false);//Election is Done
                            System.out.println("New Co-Ordinator: Process[" + this.process.getPid() + "]");
                            this.process.setCoOrdinatorFlag(true);
                            for (int i = 0; i < total_processes; i++) {
                                RunningThread.setMessageFlag(false, i);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
