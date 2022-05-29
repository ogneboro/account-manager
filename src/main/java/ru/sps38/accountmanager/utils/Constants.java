package ru.sps38.accountmanager.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Constants {

    public static final String[] acc = new String[]{"почта", "ad", "portal", "1cUAT", "1cBUH", "1cZUP", "1cDOC"};

    //ответ
    public static final String OUTPUT = "<html><head><title>Example</title></head><body><p>Request accept</p></body></html>";
    public static final String OUTPUT_HEADERS = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html\r\n" +
            "Content-Length: ";
    public static final String OUTPUT_END_OF_HEADERS = "\r\n\r\n";
    //типы запросов
    public static final String createEmployeeRequest = "newEmployee";
    public static final String blockEmployeeRequest = "blockEmployee";
    public static final String accCreatingStatusRequest = "status";
}
