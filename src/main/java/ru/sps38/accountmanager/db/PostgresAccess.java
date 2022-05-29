package ru.sps38.accountmanager.db;

import ru.sps38.accountmanager.service.TelegramBot;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class PostgresAccess {

    private static class SingletonHolder {
        public static final PostgresAccess instance = new PostgresAccess();
    }

    public static PostgresAccess getInstance() {
        return SingletonHolder.instance;
    }

    private String url;
    private String user;
    private String password;
    private final TelegramBot telegramBot;

    private PostgresAccess() {
        Properties props = new Properties();

        // переделать с getResource чтобы путь не писать полный
        try (InputStream in = Files.newInputStream(Paths.get("src/main/resource/database.properties"))) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.url = props.getProperty("url");
        this.user = props.getProperty("username");
        this.password = props.getProperty("password");
        this.telegramBot = TelegramBot.getInstance();
    }

    public String createEmployee(String[] commands, String number) {
        //"http://localhost:8000/newEmployee/Кудякова/Красняк Андрей Борисович/Диспетчер/вахта/89041450037";
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            try {
                String request;
                if (number.length() == 0) {
                    number = "null";
                    request = "INSERT INTO public.employees(id, name, post, location, email, ad, portal, one_c_uat, one_c_buh, one_c_zup, one_c_doc, phone) " +
                            "VALUES (DEFAULT, '" + commands[2] + "', '" + commands[3] + "', '" + commands[4] + "', null, null, null, null, null, null, null, " + number + ")";
                } else {
                    request = "INSERT INTO public.employees(id, name, post, location, email, ad, portal, one_c_uat, one_c_buh, one_c_zup, one_c_doc, phone) " +
                            "VALUES (DEFAULT, '" + commands[2] + "', '" + commands[3] + "', '" + commands[4] + "', null, null, null, null, null, null, null, '" + number + "')";
                }

                System.out.println(request);
                Statement statement = connection.createStatement();
                statement.execute(request);
                return "Сотрудник создан";
            } catch (Exception e) {
                e.printStackTrace();
                telegramBot.requestDBErr();
                System.out.println("Не удалось добавить сотрудника");
                return "";
            }
        } catch (Exception e) {
            //send msg "Не удалось подключится к БД" in telegram
            telegramBot.connDBErr();
            System.out.println("Не подключится к БД");
            return "";
        }
    }

    public String updateStatusRequestToDB(String fio, String type, String date, String email, String ad, String cStatus, String portalStatus, String cZUPStatus) {
        String status = new String();
        //обновить учетки пользователя, статус задачи


        return status;
    }

    public String[] sendReqADPosts(String request, String[] data) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            try {
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(request);
                System.out.println("Запрос отправлен");

                while (result.next()) {
                    data[0] = result.getString("email");
                    data[1] = result.getString("ad");
                    data[2] = result.getString("portal");
                    data[3] = result.getString("one_c_uat");
                    data[4] = result.getString("one_c_buh");
                    data[5] = result.getString("one_c_zup");
                    data[6] = result.getString("one_c_doc");
                }
                System.out.println("Запрос успешно обработан");
                return data;
            } catch (Exception e) {
                System.out.println("Не удалось отправить запрос");
                //send msg "Не удалось обработать запрос data[]" in telegram
                telegramBot.requestDBErr();
                return data;
            }
        } catch (Exception e) {
            //send msg "Не удалось подключится к БД" in telegram
            telegramBot.connDBErr();
            System.out.println("Не удалось подключится к БД");
            return data;
        }
    }

    public String sendReqEmpExists(String fio) {
        String request = "SELECT name FROM public.employees WHERE name = " + "'" + fio + "'";
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            try {
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(request);
                System.out.println("Запрос отправлен");
                String name = "";
                while (result.next()) {
                    name = result.getString("name");
                    System.out.println("Name: " + name);
                }
                if (name.length() == 0) {
                    connection.close();
                    return "";
                } else {
                    connection.close();
                    return result.getString("name");
                }

            } catch (Exception e) {
                //send msg "Не удалось обработать запрос data[]" in telegram
                telegramBot.requestDBErr();
                System.out.println("Не удалось обработать запрос");
                return "Не удалось обработать запрос";
            }
        } catch (Exception e) {
            //send msg "Не удалось подключится к БД" in telegram
            telegramBot.connDBErr();
            System.out.println("Не подключиться к БД");
            return "Не подключиться к БД";
        }
    }

    public String[] sendReqExists(String request, String[] data) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            try {
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(request);
                System.out.println("Запрос отправлен");
                while (result.next()) {
                    data[0] = result.getString("email");
                    data[1] = result.getString("ad");
                    data[2] = result.getString("portal");
                    data[3] = result.getString("one_c_uat");
                    data[4] = result.getString("one_c_buh");
                    data[5] = result.getString("one_c_zup");
                    data[6] = result.getString("one_c_doc");
                }
                System.out.println("Запрос успешно обработан");
                return data;
            } catch (Exception e) {
                //send msg "Не удалось обработать запрос data[]" in telegram
                telegramBot.requestDBErr();
                System.out.println("Не удалось обработать запрос");
                for (int i = 0; i < data.length; i++) {
                    data[i] = "Не удалось обработать запрос";
                }
                return data;
            }
        } catch (Exception e) {
            //send msg "Не удалось подключится к БД" in telegram
            telegramBot.connDBErr();
            System.out.println("Не удалось подключится к БД");
            for (int i = 0; i < data.length; i++) {
                data[i] = "Не удалось обработать запрос";
            }
            return data;
        }
    }
}
