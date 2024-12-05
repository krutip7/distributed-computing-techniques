package RMI;
import java.rmi.RemoteException;
import java.rmi.server.*;

public class Welcome extends UnicastRemoteObject implements WelcomeInterface
{
    Welcome() throws RemoteException {
        super();
    }
    public String greet(String name)
    {
        return "Welcome to DC LAB, " + name + "!\nHave a Good Time implementing RMI :)";
    }
}