package MultiThreading;

public class ThreadTask extends Thread{
    private final int sleepTime;
    private final String msg;

    ThreadTask(String str, int duration){
        msg = str;
        sleepTime = duration;
    }

    public void run(){
        long tid = Thread.currentThread().getId();
        while(true) {
            System.out.println("Thread " + tid + ": " + msg);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ThreadTask t1 = new ThreadTask("How Are You", 2000);
        ThreadTask t2 = new ThreadTask("HELLO WORLD", 1000);
        t1.start();
        t2.start();
    }
}
