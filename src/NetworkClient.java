import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class NetworkClient {
    private static Socket clientSocket;
    private static BufferedReader reader;
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;

    public static void main(String[] args) {
        while (true) {
            try {
                reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Для подключения к серверу укажите IP и port в формате '\\connect\\{IP}:{port}'");
                String request = reader.readLine();
                if(request.equals("\\exit")) {
                    break;
                } else if (request.matches("\\\\connect\\\\.*:.*")) {
                    request = request.replaceAll("\\\\connect\\\\", "");
                    String[] ip_port = request.split(":");
                    clientSocket = new Socket(ip_port[0], Integer.parseInt(ip_port[1]));
                    bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    new Thread(() -> sendMessage(bufferedWriter)).start();
                    new Thread(() -> receiveMessage(bufferedReader)).start();
                    break;
                } else if (!request.isBlank()) {
                    System.out.println("Введён неизвестный запрос, повторите попытку или введите '\\exit' для завершения работы программы");
                }
            } catch (Exception e) {
                System.out.println("Соединение не установлено, повторите попытку или введите '\\exit' для завершения работы программы");
            }
        }
    }

    private static void sendMessage(BufferedWriter bufferedWriter) {
        while (true) {
            try {
                String request = reader.readLine();
                if (!request.isBlank()) {
                    bufferedWriter.write(request + "\n");
                    bufferedWriter.flush();
                }
            } catch (Exception e) {
                System.out.println("Сервер закрыл соединение");
                System.exit(0);
                break;
            }
        }
    }

    private static void receiveMessage(BufferedReader bufferedReader) {
        while (true) {
            String message;
            try {
                message = bufferedReader.readLine();
                if (message == null) {
                    System.out.println("Сервер закрыл соединение");
                    System.exit(0);
                }
                System.out.println(message);
            } catch (Exception e) {
                System.out.println("Сервер закрыл соединение");
                System.exit(0);
                break;
            }
        }
    }
}
