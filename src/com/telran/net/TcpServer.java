package com.telran.net;

import com.telran.protocol.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer {
    int port;
    Protocol protocol;
    ServerSocket serverSocket;
    private ExecutorService executorService;

    public TcpServer(int port, Protocol protocol) throws IOException {
        this.port = port;
        this.protocol = protocol;
        serverSocket = new ServerSocket(port);
        executorService = Executors.newFixedThreadPool(5);
    }

    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.execute(new HttpSessionHandler(protocol, socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
