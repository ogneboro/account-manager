import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Server {
    //"http://localhost:8000/newEmployee/Кудякова/Красняк Андрей Борисович/Диспетчер/вахта/89041450037";
    //ответ
    private static final String OUTPUT = "<html><head><title>Example</title></head><body><p>Request accept</p></body></html>";
    private static final String OUTPUT_HEADERS = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html\r\n" +
            "Content-Length: ";
    private static final String OUTPUT_END_OF_HEADERS = "\r\n\r\n";
    //типы запросов
    private static final String createEmployeeRequest = "newEmployee";
    private static final String blockEmployeeRequest = "blockEmployee";
    private static final String accCreatingStatusRequest = "status";

    public static void main(String[] args) {
        //очередь запросов
        BlockingQueue queue = new BlockingQueue();

        //обработчик запросов
        new Thread(() -> {
            while (true){
                String task = queue.get();
                task = task.replaceAll("GET /", "");
                task = task.replaceAll(" HTTP/1.1", "");
                String[] commands = task.split("/");
                for (int i = 0; i < commands.length; i++) {
                    try {
                        commands[i] = URLDecoder.decode(commands[i], StandardCharsets.UTF_8.name());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
                //обработка запроса
                if (commands[0].equals(createEmployeeRequest) && (commands.length == 5 || commands.length == 6)) {
                    System.out.println("Осуществляется запрос на внесение");
                    AccountCreation.accCreation(commands);
                } else if (commands[0].equals(blockEmployeeRequest)) {
                    System.out.println("Осуществляется запрос на блокировку");
                } else if (commands[0].equals(accCreatingStatusRequest)) {
                    System.out.println("Осуществляется запрос состояния");
                } else {
                    TelegramBot.badRequest(commands);
                    System.out.println("Неизвестная команда");
                }

                System.out.println("Задача выполнена");
            }

        }).start();

        //сервер
        try(ServerSocket server = new ServerSocket(8000);)
        {
            System.out.println("server start");

            while (true)
                try{
                    //Создаем сокет
                    Socket socket = server.accept();
                    System.out.println("Получен запрос на сервер");
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            socket.getInputStream()));
                    BufferedWriter writer =
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            socket.getOutputStream()));

                    //прослушать запрос и внести его в список
                    String line = null;
                    int y = 0;
                    while ((line = reader.readLine()) != null) {
                        if (y == 0){
                            String request = line;
                            queue.put(request);
                            System.out.println(request);
                            y++;
                        }

                        if (line.isEmpty()) {
                            break;
                        }
                    }

                    writer.write(OUTPUT_HEADERS + OUTPUT.length() + OUTPUT_END_OF_HEADERS + OUTPUT);
                    writer.flush();
                    writer.close();

                    socket.close();

                } catch (NullPointerException e){
                    throw new RuntimeException(e);
                }
        } catch (IOException e){
            throw new RuntimeException(e);
        }

    }
}
class BlockingQueue {
    ArrayList<String> requests = new ArrayList<>();

    public synchronized String get(){
        while (requests.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        String task = requests.get(0);
        requests.remove(task);
        return task;
    }

    public synchronized void put(String request){
        requests.add(request);
        notify();
    }
}