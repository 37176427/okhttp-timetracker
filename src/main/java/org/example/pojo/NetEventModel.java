package org.example.pojo;

import lombok.Data;

@Data
public class NetEventModel {

    public long fetchDuration; //总耗时 请求发出到拿到数据，不包括本地排队时间
    public long dnsDuration; //dns解析时间
    public long connectDuration; // 创建socket通道时间
    public long requestDuration; // writeBytes的时间

    public long serveDuration; // 服务器处理时间 相当于responseStart - requestEnd

    public long responseDuration; // readBytes的时间


    public long secureDuration;
}
