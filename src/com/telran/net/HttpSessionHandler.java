package com.telran.net;

import com.telran.protocol.Protocol;
import com.telran.protocol.RawHttpRequest;
import com.telran.protocol.RawHttpResponse;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpSessionHandler implements Runnable{
    Protocol protocol;
    Socket socket;

    public HttpSessionHandler(Socket socket, Protocol protocol) {
        this.protocol = protocol;
        this.socket = socket;
    }

    public void run() {
        try (Socket socket = this.socket;
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
            RawHttpResponse response;
            try {

                RawHttpRequest request = readHeader(br);
                int bodyLength = 0;
                if(request.method != RawHttpRequest.Method.GET) {
                    try {
                        bodyLength = Integer.parseInt(request.headers.get("Content-Length"));
                    } catch (NumberFormatException ex) {
                        throw new RuntimeException("Content-Length is empty");
                    }
                }

                String body = readBody(br,bodyLength);
                request.body = body;
                System.out.println("----------------");
                System.out.println("Request\n" + request);

                response = protocol.getResponse(request);

            }catch (Exception ex){
                response = protocol.getErrorResponse(RawHttpResponse.ResponseCode.BAD_REQUEST,"Bad Request", ex.getMessage());
            }

            System.out.println("-----------------");
            System.out.println("Response");
            System.out.println(response);
            bw.write(response.toString());
            bw.flush();
        } catch (SocketException e) {
            System.out.println("Client closed connection!");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private RawHttpRequest readHeader(BufferedReader br) {
        try {
            String startingLine = br.readLine();
            String[] startingLineArgs = startingLine.split(" ");
            RawHttpRequest.Method method = RawHttpRequest.Method.valueOf(startingLineArgs[0]);
            URI uri = URI.create(startingLineArgs[1]);

            Map<String, String> headers = new HashMap<>();
            String header = br.readLine();
            while (header.length() > 0) {
                String[] args = header.split(":");
                headers.put(args[0].trim(), args[1].trim());
                header = br.readLine();
            }
            return new RawHttpRequest(method, uri, headers, null);
        } catch (Exception ex) {
            throw new HttpFormatException("Wrong http format: " + ex.getMessage());
        }
    }

    private String readBody(BufferedReader br, int length) throws IOException {
        if(length <= 0){
            return null;
        }

        char[] buff = new char[length];
        int offset = 0;

        while (offset < length) {
            int count = br.read(buff, offset, length - offset);
            if (count == -1) {
                break;
            }
            offset += count;
        }
        return new String(buff);
    }
}
