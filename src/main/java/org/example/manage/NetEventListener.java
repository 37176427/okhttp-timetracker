package org.example.manage;

import okhttp3.*;
import org.example.pojo.NetEventModel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Locale;

public class NetEventListener extends EventListener {
    private long callStart;
    //建立连接
    private long dnsStart;
    private long connectStart;
    //连接已经建立
    private long requestStart;
    private long responseStart;

    private long alreadySend;


    private final NetEventModel model;
    private long secureConnectStart;

    public NetEventListener() {
        this.model = new NetEventModel();
    }

    public NetEventListener(NetEventModel model) {
        this.model = model;
    }

    public NetEventModel getModel() {
        return model;
    }



    @Override
    public void callStart(Call call) {
        callStart = System.currentTimeMillis();
        recordEventLog("callStart");
    }

    @Override
    public void connectionAcquired(@NotNull Call call, @NotNull Connection connection) {
        recordEventLog("connectionAcquired");

    }

    @Override
    public void connectionReleased(@NotNull Call call, @NotNull Connection connection) {
        recordEventLog("connectionReleased");
    }

    @Override
    public void requestBodyStart(@NotNull Call call) {
        if (requestStart == 0){
            requestStart = System.currentTimeMillis();
        }
        recordEventLog("requestBodyStart");
    }

    @Override
    public void requestFailed(@NotNull Call call, @NotNull IOException ioe) {
        recordEventLog("requestFailed");
    }

    @Override
    public void requestHeadersStart(@NotNull Call call) {
        if (requestStart == 0){
            requestStart = System.currentTimeMillis();
        }
        recordEventLog("requestHeadersStart");
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        if (dnsStart == 0){
            dnsStart = System.currentTimeMillis();
        }
        recordEventLog("dnsStart");
    }

    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        model.dnsDuration += System.currentTimeMillis() - dnsStart;
        dnsStart = 0;
        recordEventLog("dnsEnd");
    }

    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        if (connectStart == 0){
            connectStart = System.currentTimeMillis();
        }
        recordEventLog("connectStart");
    }

    @Override
    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol) {
        long now = System.currentTimeMillis();
        model.connectDuration += now - connectStart;
        connectStart = 0;
        //因为connectionAcquired可能会有多次，那么请求从此处开始计时
//        requestStart = now;
//        model.requestDuration = 0;
        recordEventLog("connectEnd");
    }

    @Override
    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol, IOException ioe) {
        model.connectDuration+= System.currentTimeMillis() - connectStart;
        recordEventLog("connectFailed");
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        requestEnd();
        recordEventLog("requestHeadersEnd");
        alreadySend = System.currentTimeMillis();
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        requestEnd();
        recordEventLog("requestBodyEnd");
        alreadySend = System.currentTimeMillis();
    }

    private void requestEnd() {
        long l = System.currentTimeMillis();
        model.requestDuration +=  l - requestStart;
        requestStart = l;
        responseStart = 0;
    }

    private void responseEnd(){
        long l = System.currentTimeMillis();
        model.responseDuration += l - responseStart;
        responseStart = l;
    }
    @Override
    public void responseHeadersStart(Call call) {
        if (responseStart == 0) {
            responseStart = System.currentTimeMillis();
        }
        recordEventLog("responseHeadersStart");
        if (alreadySend != 0) {
            model.serveDuration += System.currentTimeMillis() - alreadySend;
            alreadySend = 0;
        }
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        responseEnd();
        recordEventLog("responseHeadersEnd");
    }

    @Override
    public void responseBodyStart(Call call) {
        if (responseStart == 0) {
            responseStart = System.currentTimeMillis();
        }
        recordEventLog("responseBodyStart");
        if (alreadySend != 0) {
            model.serveDuration += System.currentTimeMillis() - alreadySend;
            alreadySend = 0;
        }
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        responseEnd();
        recordEventLog("responseBodyEnd");
    }

    @Override
    public void callEnd(Call call) {
        endAndRecord();
        recordEventLog("callEnd");
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        endAndRecord();
        recordEventLog("callFailed");

    }

    private void endAndRecord() {
        model.fetchDuration +=System.currentTimeMillis() - callStart;
        //todo 记录
        System.out.println("spring 记录了本次请求");
    }


    private StringBuilder sbLog = new StringBuilder();

    private void recordEventLog(String name) {
        long elapseNanos = System.currentTimeMillis() - callStart;
        sbLog.append(String.format(Locale.CHINA, "%d-%s", elapseNanos, name)).append(";\r\n");
        if (name.equalsIgnoreCase("callEnd") || name.equalsIgnoreCase("callFailed")) {
            //打印出每个步骤的时间点
            System.out.println(sbLog);
        }
    }
}