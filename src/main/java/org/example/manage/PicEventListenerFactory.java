package org.example.manage;

import okhttp3.Call;
import okhttp3.EventListener;
import org.example.pojo.NetEventModel;
import org.jetbrains.annotations.NotNull;

public class PicEventListenerFactory implements EventListener.Factory {
    @NotNull
    @Override
    public EventListener create(@NotNull Call call) {
        NetEventModel tag = call.request().tag(NetEventModel.class);
        return tag == null ? new NetEventListener() : new NetEventListener(tag);
//        return tag != null ? new NetEventListener(tag) : EventListener.NONE;
    }
}
