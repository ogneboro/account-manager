package ru.sps38.accountmanager.service;

public class AD {
    public static String[] createAD(String fio, String position, String location){
        String login = "krasnyak_ab"; //
        String password = "Gl1k4"; //


        String[] adAccount = new String[2];
        adAccount[0] = login;
        adAccount[1] = password;
        return adAccount;
    }
}
