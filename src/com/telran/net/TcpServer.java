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
        executorService = Executors.newCachedThreadPool();
    }

    public void run() {
        System.out.println("Server listening port: " + port);
        try {
            while (true) {
                System.out.println("Waiting for clients ...");
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(15000);
                System.out.println("Client: " + socket.getRemoteSocketAddress());
                executorService.execute(new HttpSessionHandler(socket, protocol));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
