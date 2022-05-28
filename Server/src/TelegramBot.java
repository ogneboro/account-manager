import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TelegramBot {
    private static final String botToken = "5373512098:AAHBW_qae9Sp4RQR9EsVi5M88mEFZtOIaXc";
    private static final String chatPortal = "1045472645";
    private static final String chatChiin = "1045472645";
    private static final String chatIT = "-608647614";

    //898478727 Vlad
    public static Date sendCreationResult(String[] param, String[] email, String[] ad, String cStatus, String portalStatus, String cZUPStatus){
        String status = "";
        try{
            String message = "Созданы учетные записи для сотрудника: " + param[2]
                    + "%0AДолжность: " + param[3]
                    + "%0AПочта: " + email[0] + " " + email[1]
                    + "%0AAD: " + ad[0] + " " + ad[1]
                    + "%0AПортал: " + portalStatus
                    + "%0A1С: " + cStatus
                    + "%0A1C ZUP: " + cZUPStatus;
            message = message.replaceAll(" ", "%20");
            String query = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatIT + "&text=" + message;
            conn(query);
            status = "Сообщение отправлено";
        }catch (Exception e){
            status = "Не удалось отправить сообщение";
        }
        System.out.println(status);
        Date date = new Date(System.currentTimeMillis());
        return date;
    }
    public static void connDBErr(){
        String message = "Не%20удалось%20подключиться%20к%20БД";
        String query = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatIT + "&text=" + message;
        conn(query);
    }
    public static void requestDBErr(){
        String message = "Не%20удалось%20обработать%20запрос%20к%20БД";
        String query = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatIT + "&text=" + message;
        conn(query);
    }
    public static void requestAccept(String[] param){
        String message = "Запрос%20принят:%20";
        for (int i = 0; i < param.length; i++) {
            message = message + param[i] + "/";
        }
        String query = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatIT + "&text=" + message;
        conn(query);
    }
    public static void badRequest(String[] param){
        String message = "Bad%20request:%20";
        for (int i = 0; i < param.length; i++) {
            message = message + param[i] + "/";
        }
        String query = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatIT + "&text=" + message;
        conn(query);
    }
    public static String sendMsgPortal(String fio, String position){
        //отправить сообщение Константину
        String status = "";
        try{
            //https://api.telegram.org/bot5373512098:AAHBW_qae9Sp4RQR9EsVi5M88mEFZtOIaXc/sendMessage?chat_id=1045472645&text=Привет%20мир
            String message = "Прошу%20создать%20учетную%20запись%20портал%20сотруднику:%20" + fio + "%0AДолжность:%20" + position;
            String query = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatPortal + "&text=" + message;
            conn(query);
            status = "Сообщение отправлено";
        }catch (Exception e){
            status = "Не удалось отправить сообщение";
        }
        return status;
    }

    /*
    public static String sendMsgC(String fio, String position, String uat, String buh, String doc, String email){
        //в зависимости от должности отправить сообщение Чиину

        if (uat.equals("1cUAT")){
            uat = "УАТ, ";
        } else {
            uat = "";
        }

        if (buh.equals("1cBUH")){
            buh = "Бухгалтерия, ";
        } else {
            buh = "";
        }

        if (doc.equals("1cDOC")){
            doc = "Документооборот, ";
        } else {
            doc = "";
        }

        String status = "";
        try{
            String message = "Прошу создать учетную запись 1С: " + doc + uat + buh + "%0AСотруднику: " + fio + "%0AДолжность: " + position + "%0AУчетные данные отправить на: " + email;
            message = message.replaceAll(" ", "%20");
            String query = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatChiin + "&text=" + message;
            conn(query);
            status = "Сообщение отправлено";
        }catch (Exception e){
            status = "Не удалось отправить сообщение";
        }
        return status;
    }
     */

    private static void conn(String query){
        HttpsURLConnection connection = null;
        String otvet = "";
        try{
            connection = (HttpsURLConnection) new URL(query).openConnection();

            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.connect();
            StringBuilder sb = new StringBuilder();
            if (HttpsURLConnection.HTTP_OK == connection.getResponseCode()){
                /*
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while((line = in.readLine()) != null){
                    sb.append(line);
                    sb.append("\n");
                    otvet = line;
                }
                System.out.println(otvet);
                //System.out.println(sb.toString());

                 */
            }
            else{
                System.out.println("fail:" + connection.getResponseCode() + ", " + connection.getResponseMessage());
            }
        }
        catch (Throwable cause){
            cause.printStackTrace();
        }
        if(connection != null){
            connection.disconnect();
        }
    }
}
