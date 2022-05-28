public class Email {
    public static String[] createEmail(String fio, String position, String location){
        String login = "213"; //транслитом сделать фамилию и инициалы
        String password = "123"; //генерировать
        //Создать учетку, с рассылками, зависящими от должности и местоположения


        String[] emailAccount = new String[2];
        emailAccount[0] = login;
        emailAccount[1] = password;
        return emailAccount;
    }
}
