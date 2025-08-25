import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        // Получаем адрес сервера из переменной окружения или используем по умолчанию
        String serverHost = System.getenv("SERVER_HOST");
        if (serverHost == null || serverHost.isEmpty()) {
            serverHost = "calculation-server"; // Имя сервиса в Docker сети
        }

        int serverPort = 8080;

        System.out.println("Запуск клиентского приложения...");
        System.out.println("Попытка подключения к " + serverHost + ":" + serverPort);

        try {
            Socket socket = new Socket(serverHost, serverPort);
            System.out.println("Подключено к серверу " + serverHost + ":" + serverPort);
            System.out.println("Ожидание данных от сервера...");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            // Поток для чтения сообщений от сервера
            Thread readerThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println(serverResponse);
                    }
                } catch (IOException e) {
                    System.out.println("Соединение с сервером прервано");
                }
            });
            readerThread.setDaemon(true);
            readerThread.start();

            // Главный поток для чтения ввода пользователя
            System.out.println("Для выхода введите 'q' в любой момент");
            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                out.println(userInput);

                // Если пользователь ввел 'q', выходим
                if ("q".equals(userInput)) {
                    System.out.println("Завершение работы клиента...");
                    break;
                }
            }

            // Закрываем соединение
            socket.close();
            System.out.println("Клиентское приложение завершено.");

        } catch (IOException e) {
            System.out.println("Не удалось подключиться к серверу: " + e.getMessage());
            System.out.println("Убедитесь, что сервер запущен на " + serverHost + ":" + serverPort);
            System.out.println("Для локального тестирования используйте: SERVER_HOST=localhost");
        }
    }
}