package org.example.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.manage.PicEventListenerFactory;
import org.example.pojo.NetEventModel;

public class HttpUtil {

    static final OkHttpClient client;
    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.eventListenerFactory(new PicEventListenerFactory());
        client = builder.build();
    }

    public static String get(String url){
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        NetEventModel model = new NetEventModel();
        builder.tag(NetEventModel.class,model);
        Request request = builder.build();
        try ( Response execute = client.newCall(request).execute()){
            String string = execute.body().string();
            return string;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            System.out.println(model);
        }
    }

    public static void main(String[] args) {
        String url = "https://x.threatbook.cn";
        get(url);
    }
}
