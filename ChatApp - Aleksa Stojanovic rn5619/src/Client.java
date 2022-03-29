import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");
    Date date = new Date();
    public Client(Socket socket, String username) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        this.username = username;
    }

    public void sendMsg(){
        String ans;
        String x;
        boolean flag = true;
        Scanner scanner = new Scanner(System.in);

        while(socket.isConnected()){
                String msg = scanner.nextLine();
                String now = formatter.format(date);
                msg = "[" + now + "]" + "[" + username + "]: " + msg;

                out.println(msg);
                Main.history.add(msg);

            }
        }


    private void Discconect(Socket socket, BufferedReader in, PrintWriter out) {
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
    //pravi se novi thread koji ce da ceka novu poruku od drugi klijenata koju server prosledjuje
    //Pravi se novi thread jer su akcije cekanja i slanja poruka obe blokirajuce
    public void waitForReplay(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg;
                while(socket.isConnected()){
                    try{
                        msg = in.readLine();
                        System.out.println(msg);

                    } catch (IOException e) {
                        Discconect(socket, in, out);
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        String ans;

        boolean flag = true;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your username: ");
        String username = scanner.nextLine();
        String altUsername = username;
        Socket socket = new Socket("localhost", Main.PORT);
        Client client = new Client(socket, username);

        client.out.println(client.username);
        ans = client.in.readLine();
        if(!ans.contains("Welcome")){
            while(flag) {
                if (!ans.contains("Welcome")) {
                    System.out.println(ans);
                    altUsername = scanner.nextLine();
                    client.out.println(altUsername);
                    ans = client.in.readLine();
                }else{
                    flag = true;
                    System.out.println(ans);
                    client.setUsername(altUsername);
                    break;
                }
            }
        }else{
            System.out.println(ans);
        }

        client.waitForReplay();
        client.sendMsg();
    }

    public void validateUsername(String username){

    }

    public void setUsername(String username) {
        this.username = username;
    }
}
