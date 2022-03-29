import java.io.*;
import java.net.Socket;

public class ServerThread implements Runnable{

    private Socket socket;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String username;
    private int brojac = 0;

    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
    }

    @Override
    public void run() {
        String msg;
        try {
            CheckUsername();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println("Welcome to the chat, " + username + "!");
        Main.clients.add(this);
        Main.counter++;
        System.out.println(Main.clients);
        System.out.println(Main.counter + " clients online!");
        DisplayHistory();
        displayMsg(username + " has joined the chat!");
        while(socket.isConnected()){
            try{
                msg = in.readLine();
                msg = proccesMsg(msg);

                historyHandler(msg);
                displayMsg(msg);
            } catch (IOException e) {
                Discconect(socket, in, out);
                break;
            }
        }
    }


    public void CheckUsername() throws IOException {
        boolean flag = true;
        brojac = 0;
        try {
            this.username = in.readLine();
            for (ServerThread serverThread : Main.clients) {
                if (serverThread.username.equals(username)) {
                    out.println("This username already exits! Please enter a new username: ");
                    break;
                }
                brojac++;
            }
            if (!(brojac == Main.counter)) {
                brojac = 0;
                while (flag) {
                    username = in.readLine();
                    for (ServerThread serverThread : Main.clients) {
                        if (serverThread.username.equals(username)) {
                            out.println("This username already exits! Please enter a new username: ");
                            break;
                        }
                        brojac++;
                    }
                    if (brojac == Main.counter) {
                        break;
                    }
                    brojac = 0;
                }
            }
        } catch (IOException e) {
            specialDiscconect(socket, in, out);
        }
    }


    public void historyHandler(String msg){
        if(Main.history.size()>Main.historySize){
            Main.history.remove(0);
        }else{
            Main.history.add(msg);
        }
    }


    public void DisplayHistory(){
        for(ServerThread serverThread : Main.clients){
            try{
                if(serverThread.username.equals(username)) {
                    for (String history : Main.history) {
                        serverThread.out.println(history);
                    }
                }
            } catch (Exception e) {
                Discconect(socket, in, out);
            }
        }
    }
    private void specialDiscconect(Socket socket, BufferedReader in, PrintWriter out){
        Main.clients.remove(this);
        System.out.println(Main.clients);
        System.out.println(Main.counter + " clients online!");
        try{
            if(socket != null){
                socket.close();
            }
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Discconect(Socket socket, BufferedReader in, PrintWriter out) {
        removeUser();
        System.out.println(Main.clients);
        System.out.println(Main.counter + " clients online!");
        try{
            if(socket != null){
                socket.close();
            }
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayMsg(String string) {
        string = proccesMsg(string);
        for(ServerThread serverThread : Main.clients){
            try{
                if(!serverThread.username.equals(username)){
                    serverThread.out.println(string);
                }
            } catch (Exception e) {
                Discconect(socket, in, out);
            }
        }
    }

    private String proccesMsg(String message) {
        for (String rec : Main.censored){
            if(message.toLowerCase().contains(rec)){
                String s = createString(rec);
                message = message.replaceAll(rec,s);
                //System.out.println(message);
            }
        }
        return message;
    }

    private String createString(String rec) {
        char rez[] = rec.toCharArray();
        for(int i = 1; i<rec.length()-1; i++){
            rez[i] = '*';
        }
        return String.valueOf(rez);
    }


    private void removeUser(){
        Main.clients.remove(this);
        Main.counter--;
        displayMsg(username + " has left the chat!");
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username;
    }
}
