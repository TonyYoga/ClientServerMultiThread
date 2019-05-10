package com.telran.net;

import com.telran.protocol.Protocol;
import com.telran.protocol.RawHttpRequest;
import com.telran.protocol.RawHttpResponse;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HttpSessionHandler  implements Runnable {
    Protocol protocol;
    Socket socket;

    public HttpSessionHandler(Protocol protocol, Socket socket) {
        this.protocol = protocol;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (Socket socket = this.socket;
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            RawHttpResponse response;
            try {
                RawHttpRequest request = readRequest(br);
                System.out.println("----------");
                System.out.println("Request: \n" + request);
                response = protocol.getResponse(request);
            } catch (Exception ex) {
                response = protocol.getErrorResponse(RawHttpResponse.ResponseCode.BAD_REQUEST, "Bad Request", ex.getMessage());
            }
            System.out.println("----------");
            System.out.println("Response: \n");
            System.out.println("Response");
            bw.write(response.toString());
            bw.flush();
        } catch (SocketException ex) {

            System.out.println("Client closed connection");
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private RawHttpRequest readRequest(BufferedReader br) throws IOException {
        try {
            //Starting lines
            String startingLine = br.readLine();
            String[] startLineArgs = startingLine.split(" ");
            RawHttpRequest.Method method = RawHttpRequest.Method.valueOf(startLineArgs[0]);
            URI path = URI.create(startLineArgs[1]);

            //Headers
            Map<String, String> headers = new HashMap<>();
            String header = br.readLine();
            while (header.length() > 0) {
                String[] arr = header.split(":");
                headers.put(arr[0].trim(), arr[1].trim());
                header = br.readLine();
            }

            //Body
            StringBuilder bodyBuilder = new StringBuilder();
            try {
                int totalLenght = 1;
                if (headers.containsKey("Content-Length")) {
                    totalLenght = Integer.parseInt(headers.get("Content-Length"));
                }
                if (method != RawHttpRequest.Method.GET && totalLenght > 0) {
                    String bodyLine = br.readLine();
                    bodyBuilder.append(bodyLine);
                    int readCount = bodyLine.length();
//                System.out.println(readCount);
                    while (readCount < totalLenght) {
                        bodyLine = br.readLine();
                        if (bodyLine == null) {
                            break;
                        }
                        readCount += bodyLine.length();
                    }
                }
            } catch (IOException ex) {
                System.out.println("Client closed input");
            }

            return new RawHttpRequest(method, path, headers, bodyBuilder.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new HttpFormatException(ex.getMessage());
        }
    }

}
