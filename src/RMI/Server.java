package RMI;

import java.rmi.Naming;


public class Server {
    public static void main(String[] args) throws Exception {
        WelcomeInterface welcome=new Welcome();
        Naming.rebind("Greet",welcome);	//addService object is hosted with name AddService

    }
}