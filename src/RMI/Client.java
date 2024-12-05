package RMI;
import java.rmi.*;

public class Client {
    public static void main(String[] args) throws Exception {

        WelcomeInterface st = (WelcomeInterface)Naming.lookup("Greet");
        System.out.println(st.greet(args[0]));

    }
}