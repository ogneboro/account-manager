public class AccountRemoval {
    public static String email(String[] fio){
        String status = new String();
        try {
            String login = RuToTranslit.change(fio); //транслитом сделать фамилию и инициалы



            status = "Почта" + login + "sps38.pro успешно заблокирована";
        }catch (Exception e){
            status = "Не удалось заблокировать почту";
        }
        return status;
    }
    public static String ad(String[] fio){
        String status = new String();
        try{
            String login = RuToTranslit.change(fio); //транслитом сделать фамилию и инициалы



            status = "Учетная запись" + login + "успешно заблокирована";
        }catch (Exception e){
            status = "Не удалось заблокировать учетную запись";
        }
        return status;
    }
}
