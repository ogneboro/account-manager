package ru.sps38.accountmanager.server;

import ru.sps38.accountmanager.service.AccountCreation;
import ru.sps38.accountmanager.service.TelegramBot;
import ru.sps38.accountmanager.utils.BlockingQueue;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static ru.sps38.accountmanager.utils.Constants.*;

public class HttpServer {
    //"http://localhost:8000/newEmployee/Кудякова/Красняк Андрей Борисович/Диспетчер/вахта/89041450037";

    private static class SingletonHolder {
        public static final HttpServer instance = new HttpServer();
    }

    public static HttpServer getInstance() {
        return HttpServer.SingletonHolder.instance;
    }

    private final AccountCreation accountCreation;
    private final TelegramBot telegramBot;

    private HttpServer() {
        this.accountCreation = AccountCreation.getInstance();
        this.telegramBot = TelegramBot.getInstance();
    }

    private final BlockingQueue queue = new BlockingQueue();

    private void runThreadForRequestHandler() {
        final Runnable requestHandler = () -> {
            while (true) {
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
                    accountCreation.accCreation(commands);
                } else if (commands[0].equals(blockEmployeeRequest)) {
                    System.out.println("Осуществляется запрос на блокировку");
                } else if (commands[0].equals(accCreatingStatusRequest)) {
                    System.out.println("Осуществляется запрос состояния");
                } else {
                    telegramBot.badRequest(commands);
                    System.out.println("Неизвестная команда");
                }

                System.out.println("Задача выполнена");
            }

        };

        Thread thread = new Thread(requestHandler);
        thread.setDaemon(true);
        thread.start();
    }

    public void runServer() {
        runThreadForRequestHandler();

        try (ServerSocket server = new ServerSocket(8000)) {
            System.out.println("server start");

            while (true)
                try {
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
                        if (y == 0) {
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

                } catch (NullPointerException e) {
                    throw new RuntimeException(e);
                }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
