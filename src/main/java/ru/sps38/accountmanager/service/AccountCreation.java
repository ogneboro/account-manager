package ru.sps38.accountmanager.service;

import ru.sps38.accountmanager.db.PostgresAccess;

import java.text.SimpleDateFormat;
import java.util.Date;

import static ru.sps38.accountmanager.utils.Constants.acc;

public class AccountCreation {

    private static class SingletonHolder {
        public static final AccountCreation instance = new AccountCreation();
    }

    public static AccountCreation getInstance() {
        return AccountCreation.SingletonHolder.instance;
    }

    private final TelegramBot telegramBot;
    private final PostgresAccess db;


    public AccountCreation() {
        this.db = PostgresAccess.getInstance();
        this.telegramBot = TelegramBot.getInstance();
    }

    public void accCreation(String[] param) {
        //"http://localhost:8000/newEmployee/Кудякова/Красняк Андрей Борисович/Диспетчер/вахта/89041450037";

        if (requestCheck(param).equals("bad request")) {
            //если запрос неверный send telegram msg "bad request"
            telegramBot.badRequest(param);
        } else {
            telegramBot.requestAccept(param);
            String creator = param[1];
            String fio = param[2];
            String post = param[3];
            String location = param[4];
            String number = new String();
            try {
                param[5].length();
                number = param[5];
            } catch (Exception e) {
                number = "";
            }

            //Проверяем существует ли такой работник
            String empExCheck = db.sendReqEmpExists(fio);
            System.out.println("Существование сотрудника: " + empExCheck);

            //Запрашиваем какие учетные записи нужны для данной должности
            String[] accNeed = accDistribution(post, location);

            String[] accToCreate = new String[acc.length];

            //после запроса остальных учеток дополнить запись сотрудника

            if (empExCheck.length() != 0) {
                //создать запись задачи


                String[] accExists = accCheck(param);
                for (int i = 0; i < acc.length; i++) {
                    //если учетная запись нуджна и её нету в базе данных - добавляем в массив для создания
                    if (accNeed[i].length() != 0 && accExists[i] == null) {
                        accToCreate[i] = accNeed[i];
                        //если учетная запись нужна и она существует - добавляем существующую
                    } else if (accNeed[i].length() != 0 && accExists[i] != null) {
                        accToCreate[i] = accExists[i];
                        //если учетная запись не нужна, но существует - добавляем существующую
                    } else if (accNeed[i].length() == 0 && accExists[i] != null) {
                        accToCreate[i] = accExists[i];
                        //если не нужна и нету - не требуется
                    } else {
                        accToCreate[i] = "Учетная запись не требуется";
                    }
                }
            } else {
                //Создать сотрудника
                db.createEmployee(param, number);
                //Создать запись задачи
                for (int i = 0; i < acc.length; i++) {
                    accToCreate[i] = accNeed[i];
                }
            }
            //creating
            //Транслитом сделать ФИО, сгенерировать пароль основываясь на фамилии, передавать транслит и пароль в создание учеток
            String[] email = new String[2];
            String[] ad = new String[2];
            String cStatus = new String();
            String portalStatus = new String();
            String cZUPStatus = new String();
            //create email
            if (accToCreate[0].equals(acc[0])) {
                email = Email.createEmail(param[2], param[3], param[4]);
                System.out.println(email[0] + " " + email[1]);
            } else {
                System.out.println("\nMail: " + accToCreate[0]);
                email[0] = accToCreate[0];
                email[1] = " учетная запись уже есть";
            }
            //create AD
            if (accToCreate[1].equals(acc[1])) {
                ad = AD.createAD(param[2], param[3], param[4]);
            } else {
                ad[0] = accToCreate[1];
                ad[1] = " учетная AD запись уже есть";
            }
            //sending msgPortal
            if (accToCreate[2].equals(acc[2])) {
                portalStatus = telegramBot.sendMsgPortal(param[2], param[3]);
                System.out.println(portalStatus);
            }
            if (accToCreate[3].equals(acc[3]) || accToCreate[4].equals(acc[4]) || accToCreate[6].equals(acc[6])) {
                //cStatus = java.java.server.TelegramBot.sendMsgC(param[2], param[3], accToCreate[3], accToCreate[4], accToCreate[6], email[0]);
                System.out.println(cStatus);
            }
            Date date = telegramBot.sendCreationResult(param, email, ad, cStatus, portalStatus, cZUPStatus);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            System.out.println(formatter.format(date));
            //      Куда отправлять????
            //     if (accToCreate[5].equals(acc[5])){
            //         cZUPStatus = java.java.server.TelegramBot.sendMsgCZup(param[2], param[3]);
            //     }
            //
            //         //После создания и отправки отправить данные от почты и java.java.server.AD в it отдел
            //         остальные отправить на почту внести данные в БД об аккаунте и заявке
            //         //?
            //     Date date = new Date();
            //     java.java.server.Request.updateStatusRequestToDB(param[2],param[0],date.toString(),email[0],ad[0],cStatus,portalStatus,cZUPStatus);
            //  }
            //  else {

        }
    }

    public String requestCheck(String[] commands) {
        //проверяем, что все необходимые поля заполнены
        for (int i = 1; i < 4; i++) {
            if (commands[i].length() == 0) {
                return "bad request";
            }
        }
        return "ok";
    }

    public String[] accDistribution(String post, String location) {
        //request by post which account are needed and send on "acc" type in accNeed
        String[] accNeed = new String[acc.length];
        String request = "SELECT email, ad, portal, one_c_uat, one_c_buh, one_c_zup, one_c_doc FROM public.\"postsAD\" where name = '" + post + "'";

        //Смотрим какие аккаунты необходимы
        accNeed = db.sendReqADPosts(request, accNeed);
        for (int i = 0; i < accNeed.length; i++) {
            if (accNeed[i].equals("t")) {
                accNeed[i] = acc[i];
            } else {
                accNeed[i] = "";
            }
            System.out.println(accNeed[i]);
        }
        return accNeed;
    }

    public String[] accCheck(String[] param) {
        String[] accExists = new String[acc.length];
        //checking needed accounts in db, if exists send name of acc to alreadyCreated as in "acc"

        String request = "SELECT email, ad, portal, one_c_uat, one_c_buh, one_c_zup, one_c_doc FROM public.employees WHERE name = " + "'" + param[2] + "'";
        System.out.println(request);
        accExists = db.sendReqExists(request, accExists);
        for (int i = 0; i < acc.length; i++) {
            System.out.println(accExists[i] + " Существующий аккаунт");
        }
        return accExists;
    }

}
