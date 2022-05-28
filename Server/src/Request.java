import java.sql.*;

public class Request {
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "HankTest1";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/test2_db";

    //public static String createTask(String fio, String type){

    //}
    public static String createEmployee(String[] commands, String number) {
        //"http://localhost:8000/newEmployee/Кудякова/Красняк Андрей Борисович/Диспетчер/вахта/89041450037";
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            try {
                String request = new String();
                if (number.length() == 0){
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
                connection.close();
                return "Сотрудник создан";
            } catch (Exception e){
                e.printStackTrace();
                TelegramBot.requestDBErr();
                connection.close();
                System.out.println("Не удалось добавить сотрудника");
                return "";
            }
        } catch (Exception e){
            //send msg "Не удалось подключится к БД" in telegram
            TelegramBot.connDBErr();
            System.out.println("Не подключится к БД");
            return "";
        }
    }

    public static String updateStatusRequestToDB(String fio, String type, String date, String email, String ad, String cStatus, String portalStatus, String cZUPStatus){
        String status = new String();
        //обновить учетки пользователя, статус задачи


        return status;
    }

    public static String[] sendReqADPosts(String request, String[] data){
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
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
            } catch (Exception e){
                System.out.println("Не удалось отправить запрос");
                connection.close();
                //send msg "Не удалось обработать запрос data[]" in telegram
                TelegramBot.requestDBErr();
                return data;
            }
        }catch (Exception e){
            //send msg "Не удалось подключится к БД" in telegram
            TelegramBot.connDBErr();
            System.out.println("Не удалось подключится к БД");
            return data;
        }
    }

    public static String sendReqEmpExists(String fio){
        String request = "SELECT name FROM public.employees WHERE name = " + "'" + fio + "'";
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            try {
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(request);
                System.out.println("Запрос отправлен");
                String name = "";
                while (result.next()){
                    name = result.getString("name");
                    System.out.println("Name: " + name);
                }
                if(name.length() == 0) {
                    connection.close();
                    return "";
                }
                else{
                    connection.close();
                    return result.getString("name");
                }

            } catch (Exception e){
                //send msg "Не удалось обработать запрос data[]" in telegram
                TelegramBot.requestDBErr();
                System.out.println("Не удалось обработать запрос");
                connection.close();
                return "Не удалось обработать запрос";
            }
        }catch (Exception e){
            //send msg "Не удалось подключится к БД" in telegram
            TelegramBot.connDBErr();
            System.out.println("Не подключиться к БД");
            return "Не подключиться к БД";
        }
    }

    public static String[] sendReqExists(String request, String[] data){
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
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

                connection.close();
                System.out.println("Запрос успешно обработан");
                return data;

            } catch (Exception e){
                //send msg "Не удалось обработать запрос data[]" in telegram
                TelegramBot.requestDBErr();
                System.out.println("Не удалось обработать запрос");
                connection.close();
                for (int i = 0; i < data.length; i++) {
                    data[i] = "Не удалось обработать запрос";
                }
                return data;
            }
        }catch (Exception e){
            //send msg "Не удалось подключится к БД" in telegram
            TelegramBot.connDBErr();
            System.out.println("Не удалось подключится к БД");
            for (int i = 0; i < data.length; i++) {
                data[i] = "Не удалось обработать запрос";
            }
            return data;
        }
    }
}
