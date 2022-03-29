import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    public static final int PORT = 9001;
    public static List<ServerThread> clients = new CopyOnWriteArrayList<>();
    public static List<String> censored = new ArrayList<>();
    public static List<String> history = new CopyOnWriteArrayList<>();
    public static int historySize = 100;
    public static int counter = 0;
    public static void main(String[] args) {
        fillCensoredWords();
        try {
            ServerSocket serverSocket = new ServerSocket(Main.PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("A Client has connected!");
                Thread serverThread = new Thread(new ServerThread(socket));
                serverThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fillCensoredWords(){
        censored.add("fuck");
        censored.add("covid");
        censored.add("corona");
        censored.add("shit");
    }
}
