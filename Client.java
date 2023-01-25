import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) throws IOException {
        String serverAddress = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(serverAddress, port)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            //Interazione con utente per inviare comandi al server
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Inserisci comando (get/set nomeProdotto quantità): ");
                String command = scanner.nextLine();
                //esempio "get banana" o "set banana 20"
                out.println(command);
                String response = in.readLine();
                System.out.println("Server: " + response);
            }
        }
    }
}
