package ElectionAlgorithms;

public class Bully {

    public static void main(String[] args) {
        int total_processes = 6;
        RunningThread[] t = new RunningThread[total_processes];
        for (int i = 0; i < total_processes; i++) {
            t[i] = new RunningThread(new Process(i+1, i+1), total_processes);//passing process id, priority, total no. of processes to running thread
        }
        try {
            BullyElection.initialElection(t);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        for (int i = 0; i < total_processes; i++) {
            new Thread(t[i]).start();//start every thread
        }
    }
}



