package com.horqian.api.entity.trtc;

import lombok.Data;

@Data
public class TencentVod {


    /**
     * 本录制文件对应的用户 ID（当录制模式为合流模式时，此字段为空）
     */
    private String UserId;

    /**
     * audio/video/audio_video
     */
    private String TrackType;


    /**
     * main/aux（main 代表主流，aux 代表辅流）
     */
    private String MediaId;

    /**
     * 本录制文件在点播平台的唯一 ID
     */
    private String FileId;

    /**
     * 本录制文件在点播平台的播放地址
     */
    private String VideoUrl;

    /**
     * 本录制文件对应的 MP4 文件名
     */
    private String CacheFile;


    /**
     * 本录制文件开始的 UNIX 时间戳（毫秒)
     */
    private Long StartTimeStamp;

    /**
     * 本录制文件结束的 UNIX 时间戳（毫秒)
     */
    private Long EndTimeStamp;
}
