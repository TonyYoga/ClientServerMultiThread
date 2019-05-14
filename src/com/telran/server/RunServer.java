package com.telran.server;

import com.telran.data.AdvertController;
import com.telran.data.AdvertRepositoryImpl;
import com.telran.net.TcpServer;

import java.io.IOException;

public class RunServer {
    public static void main(String[] args) throws IOException {
        new TcpServer(3000, new AdvertController(new AdvertRepositoryImpl())).run();
    }
}
