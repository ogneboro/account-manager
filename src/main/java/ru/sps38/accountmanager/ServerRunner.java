package ru.sps38.accountmanager;

import ru.sps38.accountmanager.server.HttpServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ServerRunner {
    public static void main(String[] args) {
        HttpServer server = HttpServer.getInstance();
        server.runServer();
    }
}

