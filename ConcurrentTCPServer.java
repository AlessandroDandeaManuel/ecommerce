import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentTCPServer {
    //mappa per memorizzare i prodotti e le relative quantità
    private static ConcurrentHashMap<String, AtomicInteger> products = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        //caricamento dei prodotti da file
        loadProductsFromFile("products.txt");

        //creazione del server
        int port = 12345;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server in ascolto sulla porta " + port);
            while (true) {
                //gestione delle connessioni dei client
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket));
            }
        }
    }

    private static void loadProductsFromFile(String fileName) throws IOException {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String productName = parts[0];
                int quantity = Integer.parseInt(parts[1]);
                products.put(productName, new AtomicInteger(quantity));
            }
        }
    }
    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                //gestione delle richieste del client
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                String request = in.readLine();
                String[] parts = request.split(" ");
                String command = parts[0];
                String productName = parts[1];

                if (command.equalsIgnoreCase("get")) {
                    int quantity = products.get(productName).get();
                    out.println(quantity);
                } else if (command.equalsIgnoreCase("set")) {
                    int newQuantity = Integer.parseInt(parts[2]);
                    products.get(productName).set(newQuantity);
                    out.println("Quantità modificata con successo");
                } else {
                    out.println("Comando non riconosciuto");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
