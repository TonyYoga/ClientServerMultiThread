package client;


import com.telran.dto.AdvertDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TestClient {
    private static String[] dates = {
            "13/05/2019 12:00",
            "14/05/2019 12:00",
            "15/05/2019 12:00",
            "16/05/2019 12:00",
            "17/05/2019 12:00"
    };
    private static final CopyOnWriteArrayList<String> ids = new CopyOnWriteArrayList<>();
    private static final AtomicInteger count = new AtomicInteger();

    public static void main(String[] args) throws IOException, InterruptedException {
        for (int i = 0; i < 10; i++){
            new Thread(()->{
                for(int j = 0; j < 100; j++){
                    try {
                        addAdvert();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        Thread.sleep(3000);
        new Thread(()->{
            while (true){
                try {
                    remove();
                    Thread.sleep(1000);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        new Thread(()->{
            while (true){
                try {
                    getOwner();
                    Thread.sleep(50);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        new Thread(()->{
            while (true){
                try {
                    getByDate();
                    Thread.sleep(50);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        new Thread(()->{
            while (true){
                try {
                    getPeriod();
                    Thread.sleep(50);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static void addAdvert() throws IOException, InterruptedException {
        Random random = new Random();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/advert"))
                .header("Content-Type","*/*; charset=UTF-8")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString("owner=owner"+count.incrementAndGet()+"&dateTime="+dates[random.nextInt(10_000_000)%dates.length]+"&content=content", StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        int code = response.statusCode();
        if(code == 200){
            ids.add(body.split("=")[1]);
        }
        System.out.println(code + " " + body);
    }

    public static void remove()throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/advert?id="+ids.remove(10)))
                .header("Content-Type","*/*; charset=UTF-8")
                .timeout(Duration.ofSeconds(15))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        int code = response.statusCode();
        System.out.println("---------remove\n" + code + " " + body + "\n"+"--------");
    }

    public static void getOwner() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/advert?owner=owner10"))
                .header("Content-Type","*/*; charset=UTF-8")
                .timeout(Duration.ofSeconds(15))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        int code = response.statusCode();
        System.out.println("---------get by owner\n" + code + " " + body + "\n"+"--------");
    }

    public static void getByDate() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/advert?date=14/05/2019"))
                .header("Content-Type","*/*; charset=UTF-8")
                .timeout(Duration.ofSeconds(15))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        int code = response.statusCode();
        System.out.println("---------get by date\n" + code + " " + body + "\n"+"--------");
    }

    public static void getPeriod() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/advert?start=14/05/2019&end=16/05/2019"))
                .header("Content-Type","*/*; charset=UTF-8")
                .timeout(Duration.ofSeconds(15))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        int code = response.statusCode();
        System.out.println("---------get by date\n" + code + " " + body + "\n"+"--------");
    }


}
