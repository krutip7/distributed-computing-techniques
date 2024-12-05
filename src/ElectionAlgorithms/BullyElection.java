package ElectionAlgorithms;

import java.util.concurrent.locks.ReentrantLock;

public class BullyElection {

    public static ReentrantLock pingLock = new ReentrantLock();
    public static ReentrantLock electionLock = new ReentrantLock();
    private static boolean electionFlag = false; //By default no election is going on
    private static boolean pingFlag = true; //By default I am allowed to ping
    public static Process electionDetector;

    public static Process getElectionDetector() {
        return electionDetector;
    }

    public static void setElectionDetector(Process electionDetector) {
        BullyElection.electionDetector = electionDetector;
    }

    public static boolean isPingFlag() {
        return pingFlag;
    }

    public static void setPingFlag(boolean pingFlag) {
        BullyElection.pingFlag = pingFlag;
    }

    public static boolean isElectionFlag() {
        return electionFlag;
    }

    public static void setElectionFlag(boolean electionFlag) {
        BullyElection.electionFlag = electionFlag;
    }

    public static void initialElection(RunningThread[] t) {
        Process temp = new Process(-1, -1);
        for (RunningThread runningThread : t) {
            if (temp.getPriority() < runningThread.getProcess().getPriority()) {
                temp = runningThread.getProcess();
            }
        }
        t[temp.pid - 1].getProcess().CoOrdinatorFlag = true;
    }
}
