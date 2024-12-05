package RMI;
import java.rmi.Remote;

public interface WelcomeInterface extends Remote
{
    public String greet(String name) throws Exception;
}