package es.udc.psi.tt_ps.data.network.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ApiClient  {

    public void NotifyUpdate(String uuid, String name) throws InterruptedException {
        //make an http post request to the server
        OkHttpClient client = new OkHttpClient();
        Gson gson = new GsonBuilder().create();
        ReqBody body = new ReqBody();
        body.uuid = uuid;
        body.name = name;
        String json = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url("https://ttpsfcmclient-production.up.railway.app/update")
                .post(requestBody)
                .build();
        //launch the request in a new thread
        Thread t = new Thread(() -> {
            try {
                client.newCall(request).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
        t.interrupt();
    }
    public void NotifyDelete(String uuid, String name) throws InterruptedException {
        //make an http post request to the server
        OkHttpClient client = new OkHttpClient();
        Gson gson = new GsonBuilder().create();
        ReqBody body = new ReqBody();
        body.uuid = uuid;
        body.name = name;
        String json = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url("https://ttpsfcmclient-production.up.railway.app/delete")
                .post(requestBody)
                .build();
        //launch the request in a new thread
        Thread t = new Thread(() -> {
            try {
                client.newCall(request).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
        t.interrupt();
    }
    public void NotifyRate(String id, String name,String uuid,int count) throws InterruptedException {
        //make an http post request to the server
        OkHttpClient client = new OkHttpClient();
        Gson gson = new GsonBuilder().create();
        ReqBodyExtend body = new ReqBodyExtend();
        body.uuid = uuid;
        body.name = name;
        body.count = count;
        body.id = id;
        String json = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/rating")
                .post(requestBody)
                .build();
        //launch the request in a new thread
        Thread t = new Thread(() -> {
            try {
                client.newCall(request).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
        t.interrupt();
    }
}
