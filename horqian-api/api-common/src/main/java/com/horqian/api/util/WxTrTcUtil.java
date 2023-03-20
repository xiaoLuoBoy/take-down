package com.horqian.api.util;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.horqian.api.config.wx.WXConfig;
import com.horqian.api.exception.BaseException;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.trtc.v20190722.TrtcClient;
import com.tencentcloudapi.trtc.v20190722.models.*;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author ：kk
 * @date ：Created 2022/8/18
 * @description： 微信TRTC工具类
 */
@Component
@AllArgsConstructor
public class WxTrTcUtil {

//    // trtc appid
//    private final long sdkAppId = 1400734295;
//
//    // trtc key
//    private final String key = "e0bf2201d2cd7200e54df1fa5e00754e9a1d6b9e0297999bcb7ff0460fe64f09";


    // trtc appid 41所
    private final long sdkAppId = 1400792368;

    // trtc key 41所
    private final String key = "883b0ea41eba74a193a03a448dd5347cfd4ab32a5de1818a340b7f6c5ea173c5";


    /**
     * 根据用户id获取usersig
     *
     * @param userId 用户id
     */
    public String getUserSig(String userId) {
        com.tencentyun.TLSSigAPIv2 api = new com.tencentyun.TLSSigAPIv2(sdkAppId, key);
        // 有效期 15天
        String userSig = api.genUserSig(userId, 1296000);
        return userSig;
    }

    /**
     * 根据用户id获取usersig
     *
     * @param userId 用户id
     * @param expire 有效期
     */
    public String getUserSig(String userId, Long expire) {
        com.tencentyun.TLSSigAPIv2 api = new com.tencentyun.TLSSigAPIv2(sdkAppId, key);
        // 有效期 15天
        String userSig = api.genUserSig(userId, expire);
        return userSig;
    }


    /**
     * 开启屏幕录制
     *
     * @param roomId               房间号
     * @param recordExamineeUserId 录制用户id
     * @param userSig              根据用户id生成的秘钥
     * @param interviewerNum       面试官人数 默认3-4
     * @param examineeUserId       面试时第一个框录制的用户id
     * @return TaskId    String	云录制服务分配的任务 ID。任务 ID 是对一次录制生命周期过程的唯一标识，结束录制时会失去意义。任务 ID需要业务保存下来，作为下次针对这个录制任务操作的参数。
     * RequestId	String	唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
     */
    public CreateCloudRecordingResponse startRecord(String roomId, String recordExamineeUserId, String userSig, Integer interviewerNum, String examineeUserId, String sysUserId) {

        // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
        // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
        Credential cred = new Credential(WXConfig.SecretId, WXConfig.SecretKey);
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("trtc.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        TrtcClient client = new TrtcClient(cred, "ap-beijing", clientProfile);
        // 实例化一个请求对象,每个接口都会对应一个request对象
        CreateCloudRecordingRequest req = new CreateCloudRecordingRequest();
        req.setSdkAppId(sdkAppId);
        req.setRoomId(roomId);
        req.setRoomIdType(0L);
        req.setUserId(recordExamineeUserId);
        req.setUserSig(userSig);
        RecordParams recordParams1 = new RecordParams();
        recordParams1.setMaxIdleTime(10L);
        recordParams1.setRecordMode(2L);
        recordParams1.setStreamType(0L);
        req.setRecordParams(recordParams1);


        StorageParams storageParams1 = new StorageParams();
        CloudVod cloudVod1 = new CloudVod();
        TencentVod tencentVod1 = new TencentVod();
        tencentVod1.setExpireTime(0L);
        cloudVod1.setTencentVod(tencentVod1);

        storageParams1.setCloudVod(cloudVod1);

        req.setStorageParams(storageParams1);

        // MixTranscodeParams mixTranscodeParams1 = new MixTranscodeParams();
//        VideoParams videoParams1 = new VideoParams();
//        videoParams1.setWidth(640L);
//        videoParams1.setHeight(480L);
//        videoParams1.setFps(15L);
//        videoParams1.setBitRate(550000L);
//        videoParams1.setGop(10L);
//        mixTranscodeParams1.setVideoParams(videoParams1);

        // req.setMixTranscodeParams(mixTranscodeParams1);

        MixLayoutParams mixLayoutParams1 = new MixLayoutParams();

        MixLayout[] mixLayouts1 = new MixLayout[3];
        MixLayout mixLayout1 = new MixLayout();
        mixLayout1.setTop(10L);
        mixLayout1.setLeft(20L);
        mixLayout1.setWidth(320L);
        mixLayout1.setHeight(200L);
        mixLayout1.setRenderMode(2L);
        mixLayouts1[0] = mixLayout1;

        MixLayout mixLayout2 = new MixLayout();
        mixLayout2.setTop(220L);
        mixLayout2.setLeft(20L);
        mixLayout2.setWidth(320L);
        mixLayout2.setHeight(200L);
        mixLayout2.setRenderMode(2L);
        mixLayouts1[1] = mixLayout2;


        MixLayout mixLayout3 = new MixLayout();
        mixLayout3.setTop(430L);
        mixLayout3.setLeft(20L);
        mixLayout3.setWidth(320L);
        mixLayout3.setHeight(200L);
        mixLayout3.setRenderMode(2L);
        mixLayouts1[2] = mixLayout3;


        mixLayoutParams1.setMixLayoutList(mixLayouts1);
        mixLayoutParams1.setMixLayoutMode(4L);
        req.setMixLayoutParams(mixLayoutParams1);

//        MixLayout[] mixLayouts1 = getProportion(interviewerNum != null ? interviewerNum : 3, penOrInterview, examineeUserId, sysUserId);
//        for (MixLayout mixLayout : mixLayouts1) {
//            System.out.println("各个房间个xp--------------------------------------------------------------------" + mixLayout.toString());
//        }
//
//
//        mixLayoutParams1.setMixLayoutList(mixLayouts1);
//        mixLayoutParams1.setMixLayoutMode(4L);
//        req.setMixLayoutParams(mixLayoutParams1);

        // 返回的resp是一个CreateCloudRecordingResponse的实例，与请求对象对应
        CreateCloudRecordingResponse resp = null;
        try {
            resp = client.CreateCloudRecording(req);
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
        // 输出json格式的字符串回包
        // System.out.println(CreateCloudRecordingResponse.toJsonString(resp));
        return resp;
    }


    /**
     * 开启面试屏幕录制
     *
     * @param roomId                房间号
     * @param recordExamineeUserId  录制用户id
     * @param userSig               根据用户id生成的秘钥
     * @param type                  会议类型
     * @param subscribeAudioUserIds 订阅视频白名单
     * @param interviewerNum        面试官人数 默认3-4
     * @param penOrInterview        0笔试 1面试
     * @param examineeUserId        面试时第一个框录制的用户id
     * @return TaskId    String	云录制服务分配的任务 ID。任务 ID 是对一次录制生命周期过程的唯一标识，结束录制时会失去意义。任务 ID需要业务保存下来，作为下次针对这个录制任务操作的参数。
     * RequestId	String	唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
     */
    public CreateCloudRecordingResponse startInterviewRecord(String roomId, String recordExamineeUserId, String userSig, Integer type, List<String> subscribeAudioUserIds, Integer interviewerNum, Integer penOrInterview, String examineeUserId, String sysUserId) {

        // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
        // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
        Credential cred = new Credential(WXConfig.SecretId, WXConfig.SecretKey);
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("trtc.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        TrtcClient client = new TrtcClient(cred, "ap-beijing", clientProfile);
        // 实例化一个请求对象,每个接口都会对应一个request对象
        CreateCloudRecordingRequest req = new CreateCloudRecordingRequest();
        // trtc appid
        req.setSdkAppId(sdkAppId);
        // trtc 房间id
        req.setRoomId(roomId);
        // trtc房间号类型 0 字符串类型 1 32位整型的房间id
        req.setRoomIdType(0L);
        // trtc房间用户id
        req.setUserId(recordExamineeUserId);
        // 录制密钥
        req.setUserSig(userSig);
        // 录制类型
        RecordParams recordParams1 = new RecordParams();
        // 录制模式 1 单流录制 2 混流录制
        recordParams1.setRecordMode(2L);
        // 房间内没有用户超过多少秒自动停止录制  该值需大于等于 5秒，且小于等于 86400秒(24小时)
        recordParams1.setMaxIdleTime(15L);
        // 录制媒体流类型 0 录制音频 + 视频流
        recordParams1.setStreamType(0L);
        // 添加订阅流白名单
        if (subscribeAudioUserIds != null && !subscribeAudioUserIds.isEmpty()) {
            SubscribeStreamUserIds subscribeStreamUserIds = new SubscribeStreamUserIds();
            // 订阅视频流
            String[] subscribeVideoUserIds = subscribeAudioUserIds.toArray(new String[subscribeAudioUserIds.size()]);
            subscribeStreamUserIds.setSubscribeVideoUserIds(subscribeVideoUserIds);
            // 订阅音频流
            subscribeStreamUserIds.setSubscribeAudioUserIds(subscribeVideoUserIds);
            // 添加userId指定视频流
            recordParams1.setSubscribeStreamUserIds(subscribeStreamUserIds);
        }
        req.setRecordParams(recordParams1);

        // 第三方云点播参数
        StorageParams storageParams1 = new StorageParams();
        CloudVod cloudVod1 = new CloudVod();
        TencentVod tencentVod1 = new TencentVod();
        // 视频保存时间0默认永久保存
        // 媒体文件过期时间，为当前时间的绝对过期时间；保存一天，就填"86400"，永久保存就填"0"，默认永久保存。
        tencentVod1.setExpireTime(20000L);
        cloudVod1.setTencentVod(tencentVod1);
        storageParams1.setCloudVod(cloudVod1);
        req.setStorageParams(storageParams1);

        // 设置视频长宽 单位像素
        MixTranscodeParams mixTranscodeParams1 = new MixTranscodeParams();
        VideoParams videoParams1 = new VideoParams();
        // 设置视频宽度
        videoParams1.setWidth(1920L);
        // 设置视频高度
        videoParams1.setHeight(1080L);
        // 设置视频的帧率
        videoParams1.setFps(30L);
        // 视频的码率 单位是bps
        videoParams1.setBitRate(550000L);
        // 视频关键帧间隔 单位秒 默认十秒
        videoParams1.setGop(10L);
        mixTranscodeParams1.setVideoParams(videoParams1);
        req.setMixTranscodeParams(mixTranscodeParams1);

        // 分割屏幕录制
        MixLayoutParams mixLayoutParams1 = new MixLayoutParams();
        //
         // MixLayout[] mixLayouts1 = getProportion(interviewerNum != null ? interviewerNum : 3, penOrInterview, examineeUserId, sysUserId);
         // for (MixLayout mixLayout : mixLayouts1) {
         //     System.out.println("各个房间个xp--------------------------------------------------------------------" + mixLayout.toString());
         // }
         //
         // mixLayoutParams1.setMixLayoutList(mixLayouts1);
        // 选择录制模式 1：悬浮布局； 2：屏幕分享布局； 3：九宫格布局（默认） 4：自定义布局
        // 会议类型 10内部会议 20外部会议 30在线问诊
        //if (type == 30) {
        mixLayoutParams1.setMixLayoutMode(2L);
        if (subscribeAudioUserIds != null && !subscribeAudioUserIds.isEmpty()) {
            String[] subscribeVideoUserIds = subscribeAudioUserIds.toArray(new String[subscribeAudioUserIds.size()]);
            mixLayoutParams1.setMaxResolutionUserId(subscribeVideoUserIds[0]);
        }
        //} else {
        //    mixLayoutParams1.setMixLayoutMode(3L);
        //}

        //// 背景画面宽高比不一致的时候处理方案，与MixLayoufList定义的RenderMode一致
        //mixLayoutParams1.setRenderMode(2L);

        req.setMixLayoutParams(mixLayoutParams1);

        // 返回的resp是一个CreateCloudRecordingResponse的实例，与请求对象对应
        CreateCloudRecordingResponse resp = null;
        try {
            resp = client.CreateCloudRecording(req);
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
        // 输出json格式的字符串回包
        // System.out.println(CreateCloudRecordingResponse.toJsonString(resp));
        return resp;

    }

    /**
     * 根据人数获取trtc录制比例
     *
     * @param num 人数
     * @return 比例
     */
    private static MixLayout[] getProportion(int num, Integer penOrInterview, String examineeUserId, String sysUserId) {
        MixLayout[] mixLayouts1 = new MixLayout[num];
        if (num > 0) {
            Integer sign = 1;
            for (int i = 0; i < num; i++) {
                if (num == 1) {
                    // 如果一个人，占满整个屏幕
                    MixLayout mixLayout1 = new MixLayout();
                    mixLayout1.setTop(10L);
                    mixLayout1.setLeft(20L);
                    mixLayout1.setWidth(1240L);
                    mixLayout1.setHeight(700L);
                    mixLayout1.setRenderMode(2L);
                    mixLayouts1[i] = mixLayout1;
                }
                // 如果是2个人，平均分隔
                else if (num == 2) {
                    // 得到top
                    Long top = 10L;
                    // 得到left
                    Long left = 20L;
                    // 得到高
                    Long height = 700L;
                    // 得到宽
                    Long width = (1280L - (left * (2 + 1L))) / 2;
                    // 创建框
                    sign = createMixLayOut(top, left, width, height, i, mixLayouts1, sign, 2, penOrInterview, examineeUserId, sysUserId, num);
                }
                // 在 3 - 4 人数的
                else if (num == 3 || num == 4) {
                    // 得到top
                    Long top = 10L;
                    // 得到left
                    Long left = 20L;
                    // 得到高
                    Long height = (720L - (top * (2 + 1L))) / 2;
                    // 得到宽
                    Long width = (1280L - (left * (2 + 1L))) / 2;
                    // 创建框
                    sign = createMixLayOut(top, left, width, height, i, mixLayouts1, sign, 2, penOrInterview, examineeUserId, sysUserId, num);

                }
                // 在 5 - 6 人数的
                else if (num == 5 || num == 6) {
                    // 得到top
                    Long top = 10L;
                    // 得到left
                    Long left = 10L;
                    // 得到高
                    Long height = (720L - (top * (2 + 1L))) / 2;
                    // 得到宽
                    Long width = (1280L - (left * (3 + 1L))) / 3;

                    // 创建框
                    sign = createMixLayOut(top, left, width, height, i, mixLayouts1, sign, 3, penOrInterview, examineeUserId, sysUserId, num);
                }
                // 在 7 - 8 人数的
                else if (num == 7 || num == 8) {
                    // 得到top
                    Long top = 10L;
                    // 得到left
                    Long left = 10L;
                    // 得到高
                    Long height = (720L - (top * (2 + 1L))) / 2;
                    // 得到宽
                    Long width = (1280L - (left * (4 + 1L))) / 4;
                    // 创建框
                    sign = createMixLayOut(top, left, width, height, i, mixLayouts1, sign, 4, penOrInterview, examineeUserId, sysUserId, num);
                }
                // 在 9 - 12 人数的
                else if (num <= 12) {
                    // 得到top
                    Long top = 10L;
                    // 得到left
                    Long left = 10L;
                    // 得到高
                    Long height = (720L - (top * (3 + 1L))) / 3;
                    // 得到宽
                    Long width = (1280L - (left * (4 + 1L))) / 4;

                    // 创建框
                    sign = createMixLayOut(top, left, width, height, i, mixLayouts1, sign, 4, penOrInterview, examineeUserId, sysUserId, num);
                }
                // 在 13 - 16 人数的
                else if (num <= 16) {
                    // 得到top
                    Long top = 10L;
                    // 得到left
                    Long left = 10L;
                    // 得到高
                    Long height = (720L - (top * (4 + 1L))) / 4;
                    // 得到宽
                    Long width = (1280L - (left * (4 + 1L))) / 4;

                    // 创建框
                    sign = createMixLayOut(top, left, width, height, i, mixLayouts1, sign, 4, penOrInterview, examineeUserId, sysUserId, num);
                }
                // 在 17 - 20 人数的
                else if (num <= 20) {
                    // 得到top
                    Long top = 10L;
                    // 得到left
                    Long left = 10L;
                    // 得到高
                    Long height = (720L - (top * (4 + 1L))) / 4;
                    // 得到宽
                    Long width = (1280L - (left * (5 + 1L))) / 5;

                    // 创建框
                    sign = createMixLayOut(top, left, width, height, i, mixLayouts1, sign, 5, penOrInterview, examineeUserId, sysUserId, num);
                }
                // 在 21 - 25 人数的
                else if (num <= 25) {
                    // 得到top
                    Long top = 10L;
                    // 得到left
                    Long left = 10L;
                    // 得到高
                    Long height = (720L - (top * (5 + 1L))) / 5;
                    // 得到宽
                    Long width = (1280L - (left * (5 + 1L))) / 5;

                    // 创建框
                    sign = createMixLayOut(top, left, width, height, i, mixLayouts1, sign, 5, penOrInterview, examineeUserId, sysUserId, num);
                }
                // 在 26 - 30 人数的
                else if (num <= 30) {
                    // 得到top
                    Long top = 10L;
                    // 得到left
                    Long left = 10L;
                    // 得到高
                    Long height = (720L - (top * (5 + 1L))) / 5;
                    // 得到宽
                    Long width = (1280L - (left * (6 + 1L))) / 6;

                    // 创建框
                    sign = createMixLayOut(top, left, width, height, i, mixLayouts1, sign, 6, penOrInterview, examineeUserId, sysUserId, num);
                }
            }
        }
        return mixLayouts1;
    }


    /**
     * 创建trtc框
     *
     * @param top    top
     * @param left   left
     * @param width  宽
     * @param height 高
     * @param i      循环索引
     * @param sign   位移数
     * @param offset 偏移量
     */
    private static Integer createMixLayOut(Long top, Long left, Long width, Long height, int i,
                                           MixLayout[] mixLayouts1,
                                           Integer sign,
                                           Integer offset,
                                           Integer penOrInterview,
                                           String examineeUserId,
                                           String sysUserId,
                                           Integer num) {
        // 如果取余为0则代表换行
        Integer surplus = i % offset;
        // 得到层级
        Integer hierarchy = (i + offset) / offset;

        if (i != 0 && hierarchy == 1) {
            // 第一层级 top不变 left该怎么变怎么变
            left = (width * sign) + (left * (sign + 1));
            sign += 1;
        } else if (hierarchy > 1) {
            top = (height * (hierarchy - 1)) + (top * ((hierarchy - 1) + 1));
            if (surplus != 0) {
                // 第二行第一个left也不变
                left = (width * sign) + (left * (sign + 1));
                sign += 1;
            }

        }


        MixLayout mixLayout1 = new MixLayout();
        if (penOrInterview == 1 && i == 0 && examineeUserId != null) {
            // 面试，第一个框放入考生
            mixLayout1.setUserId(examineeUserId);
        }
        if (penOrInterview == 1 && i == num - 1 && StringUtils.isNotBlank(sysUserId)) {
            mixLayout1.setUserId(sysUserId);
        }
        mixLayout1.setTop(top);
        mixLayout1.setLeft(left);
        mixLayout1.setWidth(width);
        mixLayout1.setHeight(height);
        mixLayout1.setRenderMode(2L);
        mixLayouts1[i] = mixLayout1;
        if (sign.equals(offset)) {
            // 下一层级，sign归1
            sign = 1;
        }

        return sign;
    }


    /**
     * 开启屏幕录制
     *
     * @param roomId         房间号
     * @param examineeUserId 用户id
     * @param userSig        根据用户id生成的秘钥
     * @return TaskId    String	云录制服务分配的任务 ID。任务 ID 是对一次录制生命周期过程的唯一标识，结束录制时会失去意义。任务 ID需要业务保存下来，作为下次针对这个录制任务操作的参数。
     * RequestId	String	唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
     */
    public CreateCloudRecordingResponse startRecordInt(String roomId, String examineeUserId, String userSig) {

        // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
        // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
        Credential cred = new Credential(WXConfig.SecretId, WXConfig.SecretKey);
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("trtc.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        TrtcClient client = new TrtcClient(cred, "ap-beijing", clientProfile);
        // 实例化一个请求对象,每个接口都会对应一个request对象
        CreateCloudRecordingRequest req = new CreateCloudRecordingRequest();
        req.setSdkAppId(sdkAppId);
        req.setRoomId(roomId);
        req.setRoomIdType(0L);
        req.setUserId(examineeUserId);
        req.setUserSig(userSig);
        RecordParams recordParams1 = new RecordParams();
        recordParams1.setMaxIdleTime(10L);
        recordParams1.setRecordMode(2L);
        recordParams1.setStreamType(0L);
        req.setRecordParams(recordParams1);

        StorageParams storageParams1 = new StorageParams();
        CloudVod cloudVod1 = new CloudVod();
        TencentVod tencentVod1 = new TencentVod();
        tencentVod1.setExpireTime(0L);
        cloudVod1.setTencentVod(tencentVod1);

        storageParams1.setCloudVod(cloudVod1);

        req.setStorageParams(storageParams1);

        MixTranscodeParams mixTranscodeParams1 = new MixTranscodeParams();
        VideoParams videoParams1 = new VideoParams();
        videoParams1.setWidth(1280L);
        videoParams1.setHeight(720L);
        videoParams1.setFps(15L);
        videoParams1.setBitRate(550000L);
        videoParams1.setGop(10L);
        mixTranscodeParams1.setVideoParams(videoParams1);

        req.setMixTranscodeParams(mixTranscodeParams1);

        MixLayoutParams mixLayoutParams1 = new MixLayoutParams();
        mixLayoutParams1.setMixLayoutMode(3L);
        req.setMixLayoutParams(mixLayoutParams1);

        // 返回的resp是一个CreateCloudRecordingResponse的实例，与请求对象对应
        CreateCloudRecordingResponse resp = null;
        try {
            resp = client.CreateCloudRecording(req);
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
        return resp;
    }


    /**
     * 更新屏幕录制
     *
     * @param taskId 录制任务id
     * @return TaskId    String	云录制服务分配的任务 ID。任务 ID 是对一次录制生命周期过程的唯一标识，结束录制时会失去意义。
     * RequestId	String	唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
     */
    public ModifyCloudRecordingResponse updateRecord(String taskId, List<String> subscribeAudioUserIds) {

        // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
        // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
        Credential cred = new Credential(WXConfig.SecretId, WXConfig.SecretKey);
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("trtc.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        TrtcClient client = new TrtcClient(cred, "ap-beijing", clientProfile);
        // 实例化一个请求对象,每个接口都会对应一个request对象
        ModifyCloudRecordingRequest req = new ModifyCloudRecordingRequest();
        req.setSdkAppId(sdkAppId);
        req.setTaskId(taskId);
        MixLayoutParams mixLayoutParams1 = new MixLayoutParams();

        // 添加订阅流白名单
        if (subscribeAudioUserIds != null && !subscribeAudioUserIds.isEmpty()) {
             SubscribeStreamUserIds subscribeStreamUserIds = new SubscribeStreamUserIds();
            // 订阅视频流
            String[] subscribeVideoUserIds = subscribeAudioUserIds.toArray(new String[subscribeAudioUserIds.size()]);
             subscribeStreamUserIds.setSubscribeVideoUserIds(subscribeVideoUserIds);
             // 订阅音频流
             subscribeStreamUserIds.setSubscribeAudioUserIds(subscribeVideoUserIds);
             // 添加userId指定视频流
             req.setSubscribeStreamUserIds(subscribeStreamUserIds);
            //  指定主播
            mixLayoutParams1.setMaxResolutionUserId(subscribeVideoUserIds[0]);
        }
        // 布局模式: 1：悬浮布局； 2：屏幕分享布局； 3：九宫格布局（默认）； 4：自定义布局
        mixLayoutParams1.setMixLayoutMode(2L);
        req.setMixLayoutParams(mixLayoutParams1);


        // 返回的resp是一个ModifyCloudRecordingResponse的实例，与请求对象对应
        ModifyCloudRecordingResponse resp = null;
        try {
            resp = client.ModifyCloudRecording(req);
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
        // 输出json格式的字符串回包
        // System.out.println(ModifyCloudRecordingResponse.toJsonString(resp));
        return resp;
    }


    /**
     * 查询录制状态
     *
     * @param taskId 录制任务id
     * @return TaskId
     * String	录制任务的唯一Id。
     * Status	String	云端录制任务的状态信息。
     * Idle：表示当前录制任务空闲中
     * InProgress：表示当前录制任务正在进行中。
     * Exited：表示当前录制任务正在退出的过程中。
     * StorageFileList	Array of StorageFile	录制文件信息。
     * 注意：此字段可能返回 null，表示取不到有效值。
     * RequestId	String	唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
     */
    public DescribeCloudRecordingResponse getRecordStatus(String taskId) {

        // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
        // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
        Credential cred = new Credential(WXConfig.SecretId, WXConfig.SecretKey);
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("trtc.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        TrtcClient client = new TrtcClient(cred, "ap-beijing", clientProfile);
        // 实例化一个请求对象,每个接口都会对应一个request对象
        DescribeCloudRecordingRequest req = new DescribeCloudRecordingRequest();
        req.setSdkAppId(sdkAppId);
        req.setTaskId(taskId);
        // 返回的resp是一个DescribeCloudRecordingResponse的实例，与请求对象对应
        DescribeCloudRecordingResponse resp = null;
        try {
            resp = client.DescribeCloudRecording(req);
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
        // 输出json格式的字符串回包
        //System.out.println(DescribeCloudRecordingResponse.toJsonString(resp));
        return resp;
    }


    /**
     * 停止云端录制
     *
     * @param taskId 任务id
     * @return TaskId String    云录制服务分配的任务 ID。任务 ID 是对一次录制生命周期过程的唯一标识，结束录制时会失去意义。
     * RequestId	String	唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
     */
    public DeleteCloudRecordingResponse stopRecord(String taskId) {
        // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
        // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
        Credential cred = new Credential(WXConfig.SecretId, WXConfig.SecretKey);
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("trtc.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        TrtcClient client = new TrtcClient(cred, "ap-beijing", clientProfile);
        // 实例化一个请求对象,每个接口都会对应一个request对象
        DeleteCloudRecordingRequest req = new DeleteCloudRecordingRequest();
        req.setSdkAppId(sdkAppId);
        req.setTaskId(taskId);
        // 返回的resp是一个DeleteCloudRecordingResponse的实例，与请求对象对应
        DeleteCloudRecordingResponse resp = null;
        try {
            resp = client.DeleteCloudRecording(req);
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
        // 输出json格式的字符串回包
        // System.out.println(DeleteCloudRecordingResponse.toJsonString(resp));
        return resp;
    }

    /**
     * 根据文件id删除文件
     * @param fileId
     */
    public void deleteFile(String fileId) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(WXConfig.SecretId, WXConfig.SecretKey);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("vod.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            VodClient client = new VodClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DeleteMediaRequest req = new DeleteMediaRequest();
            req.setFileId(fileId);
            // 返回的resp是一个DeleteMediaResponse的实例，与请求对象对应
            DeleteMediaResponse resp = client.DeleteMedia(req);
            // 输出json格式的字符串回包
            System.out.println(DeleteMediaResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }



    public static String videoRBI(String fileId, Integer index, Long timer, String content, Integer nonce) {
        Map<String, Object> map = new HashMap<>();
        map.put("fileId", fileId);
        map.put("keyFrameDesc." + index + ".timeOffset", timer);
        map.put("keyFrameDesc." + index + ".content", content);
        //map.put("Region", "bj");
        //map.put("Timestamp", System.currentTimeMillis());
        //map.put("Nonce", nonce);
        //map.put("SecretId", WXConfig.SecretId);
        //map.put("Signature", 1);
        Collection<String> keyset = map.keySet();
        List<String> list = new ArrayList<String>(keyset);
        Collections.sort(list);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i) + "=" + map.get(list.get(i))).append("&");
        }
        // 得到排序串
        String substring = builder.toString().substring(0, builder.length() - 1);

        return substring;

//        String result = HttpUtil.get("https://miniprogram-kyc.tencentcloudapi.com/api/oauth2/access_token", map);
//        String code = (String) JSONUtil.parseObj(result).get("code");
//        if (!"0".equals(code)) {
//            throw new BaseException("获取token失败，失败原因：" + JSONUtil.parseObj(result).get("msg"));
//        } getSignature(WXConfig.SecretId,WXConfig.SecretKey)
    }


    /**
     * 将对应房间的user移出房间
     *
     * @param roomId  房间号
     * @param userIds 用户id数组
     */
    public void removeUser(String roomId, String[] userIds) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(WXConfig.SecretId, WXConfig.SecretKey);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("trtc.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            TrtcClient client = new TrtcClient(cred, "ap-beijing", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            RemoveUserByStrRoomIdRequest req = new RemoveUserByStrRoomIdRequest();
            req.setSdkAppId(sdkAppId);
            req.setRoomId(roomId);

            req.setUserIds(userIds);

            // 返回的resp是一个RemoveUserByStrRoomIdResponse的实例，与请求对象对应
            RemoveUserByStrRoomIdResponse resp = client.RemoveUserByStrRoomId(req);
            // 输出json格式的字符串回包
            System.out.println(RemoveUserByStrRoomIdResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }


    /**
     * 获取群组
     *
     * @return interviewName 面试间名称
     */
    public String createGroup(String interviewAdminMasterIdSysUserId) {

        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Public");
        map.put("Name", interviewAdminMasterIdSysUserId);
        // 设置为自由加入
        map.put("ApplyJoinOption", "FreeAccess");
        // im 地址请求群组
        String url = "https://console.tim.qq.com/v4/group_open_http_svc/create_group?sdkappid=" +
                sdkAppId +
                "&identifier=administrator&usersig=" + getUserSig("administrator", 7776000L) +
                "&random=" + (int) (Math.random() * 9 + 1) * 100000 +
                "&contenttype=json";

        String result = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/json")
                .body(JSON.toJSONString(map))
                .timeout(6000)
                .execute().body();

        Integer code = (Integer) JSONUtil.parseObj(result).get("ErrorCode");
        if (!(0 == code)) {
            throw new BaseException("获取群组失败，失败原因：" + JSONUtil.parseObj(result).get("ErrorInfo"));
        }

        // 得到im群组id
        String groupId = (String) JSONUtil.parseObj(result).get("GroupId");

        return groupId;
    }


    /**
     * 解散群组
     *
     * @return groupId 群组id
     */
    public void dissolveGroup(String groupId) {
        String url = "https://console.tim.qq.com/v4/group_open_http_svc/destroy_group?sdkappid=" +
                sdkAppId +
                "&identifier=administrator&usersig=" + getUserSig("administrator", 7776000L) +
                "&random=" + (int) (Math.random() * 9 + 1) * 100000 +
                "&contenttype=json";

        Map<String, Object> map = new HashMap<>();
        map.put("GroupId", groupId);

        String result = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/json")
                .body(JSON.toJSONString(map))
                .timeout(6000)
                .execute().body();

        Integer code = (Integer) JSONUtil.parseObj(result).get("ErrorCode");
        if (!(0 == code)) {
            throw new BaseException("获取群组失败，失败原因：" + JSONUtil.parseObj(result).get("ErrorInfo"));
        }
    }


    /**
     * getUserStatus 查询参会人员在线状态
     *
     * @return groupId 群组id
     */
    public String getUserStatus(String userId) {
        String url = "https://console.tim.qq.com/v4/openim/query_online_status?sdkappid=" +
                sdkAppId +
                "&identifier=administrator&usersig=" + getUserSig("administrator", 7776000L) +
                "&random=" + (int) (Math.random() * 9 + 1) * 100000 +
                "&contenttype=json";

        Map<String, Object> map = new HashMap<>();
        String[] userIds = {userId};
        map.put("To_Account", userIds);

        String result = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/json")
                .body(JSON.toJSONString(map))
                .timeout(6000)
                .execute().body();
        System.out.println(result);
        return result;
    }

    /**
     * 获取群组内成员的在线状态
     *
     * @return userIds 用户群组id
     */
    public String groupUserStatus(List<String> userIds) {
        String url = "https://console.tim.qq.com/v4/openim/query_online_status?sdkappid=" +
                sdkAppId +
                "&identifier=administrator&usersig=" + getUserSig("administrator", 7776000L) +
                "&random=" + (int) (Math.random() * 9 + 1) * 100000 +
                "&contenttype=json";

        Map<String, Object> map = new HashMap<>();
        // 用户成员id
        String[] str = userIds.toArray(new String[userIds.size()]);
        // String[] userIds = {userId};
        map.put("To_Account", str);

        String result = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/json")
                .body(JSON.toJSONString(map))
                .timeout(6000)
                .execute().body();
        System.out.println(result);
        return result;
    }


    /**
     * getUserStatus 查询参会人员信息
     *
     * @return groupId 群组id
     */
    public String getUserList(String groupId) {
        String url = "https://console.tim.qq.com/v4/group_open_http_svc/get_group_member_info?sdkappid=" +
                sdkAppId +
                "&identifier=administrator&usersig=" + getUserSig("administrator", 7776000L) +
                "&random=" + (int) (Math.random() * 9 + 1) * 100000 +
                "&contenttype=json";

        Map<String, Object> map = new HashMap<>();
        map.put("GroupId", groupId);
        String result = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/json")
                .body(JSON.toJSONString(map))
                .timeout(6000)
                .execute().body();
        System.out.println(result);
        Integer code = (Integer) JSONUtil.parseObj(result).get("ErrorCode");
        return result;
    }

    /**
     * 获取群在线人数
     *
     * @return groupId 群组id
     */
    public String getUserNum(String groupId) {
        String url = "https://console.tim.qq.com/v4/group_open_http_svc/get_online_member_num?sdkappid=" +
                sdkAppId +
                "&identifier=administrator&usersig=" + getUserSig("administrator", 7776000L) +
                "&random=" + (int) (Math.random() * 9 + 1) * 100000 +
                "&contenttype=json";

        Map<String, Object> map = new HashMap<>();
        map.put("GroupId", groupId);
        String result = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/json")
                .body(JSON.toJSONString(map))
                .timeout(6000)
                .execute().body();
        System.out.println(result);
        Integer code = (Integer) JSONUtil.parseObj(result).get("ErrorCode");
        return result;
    }

    /**
     * 批量删除群组成员
     *
     * @return groupId 群组id
     */
    public String batchRemoveGroupUser(String groupId, List<String> userIds) {
        String url = "https://console.tim.qq.com/v4/group_open_http_svc/delete_group_member?sdkappid=" +
                sdkAppId +
                "&identifier=administrator&usersig=" + getUserSig("administrator", 7776000L) +
                "&random=" + (int) (Math.random() * 9 + 1) * 100000 +
                "&contenttype=json";

        Map<String, Object> map = new HashMap<>();
        map.put("GroupId", groupId);
        // String[] userIds = {username};
        String[] str = userIds.toArray(new String[userIds.size()]);
        map.put("MemberToDel_Account", str);

        String result = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/json")
                .body(JSON.toJSONString(map))
                .timeout(6000)
                .execute().body();
        System.out.println(result);
        Integer code = (Integer) JSONUtil.parseObj(result).get("ErrorCode");
        return result;
    }

    /**
     * 删除群组成员
     *
     * @return groupId 群组id
     */
    public String removeGroupUser(String groupId, String username) {
        String url = "https://console.tim.qq.com/v4/group_open_http_svc/delete_group_member?sdkappid=" +
                sdkAppId +
                "&identifier=administrator&usersig=" + getUserSig("administrator", 7776000L) +
                "&random=" + (int) (Math.random() * 9 + 1) * 100000 +
                "&contenttype=json";

        Map<String, Object> map = new HashMap<>();
        map.put("GroupId", groupId);
        String[] userIds = {username};
        map.put("MemberToDel_Account", userIds);

        String result = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/json")
                .body(JSON.toJSONString(map))
                .timeout(6000)
                .execute().body();
        System.out.println(result);
        Integer code = (Integer) JSONUtil.parseObj(result).get("ErrorCode");
        return result;
    }

    /**
     * 发送离线消息
     *
     * @param GroupId 群组id
     * @param json    消息串
     * @return
     */
    public void sendMsgGroupId(String GroupId, String json) {
        String url = "https://console.tim.qq.com/v4/group_open_http_svc/send_group_msg?sdkappid=" +
                sdkAppId +
                "&identifier=administrator&usersig=" + getUserSig("administrator", 7776000L) +
                "&random=" + Math.round((Math.random() + 1) * 10000) +
                "&contenttype=json";

        JSONObject result = new JSONObject();
        result.set("GroupId", GroupId);
        result.set("Random", Math.round((Math.random() + 1) * 10000));

        JSONArray MsgBodyArray = new JSONArray();
        JSONObject DataArray = new JSONObject();
        DataArray.set("MsgType", "TIMCustomElem");
        JSONObject DataArrayMsg = new JSONObject();
        DataArrayMsg.set("Ext", json);
        DataArray.set("MsgContent", DataArrayMsg);
        MsgBodyArray.put(0, DataArray);
        result.set("MsgBody", MsgBodyArray);


        String result1 = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/json")
                .body(result.toString())
                .timeout(6000)
                .execute().body();
        System.out.println(result1);
    }


    /**
     * 生成封面
     *
     * @param fileId 文件id
     */
    public void getCoverUrl(String fileId) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(WXConfig.SecretId, WXConfig.SecretKey);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("vod.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            VodClient client = new VodClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            ProcessMediaRequest req = new ProcessMediaRequest();
            req.setFileId(fileId);

            MediaProcessTaskInput mediaProcessTaskInput1 = new MediaProcessTaskInput();

            SampleSnapshotTaskInput[] sampleSnapshotTaskInputs1 = new SampleSnapshotTaskInput[1];
            SampleSnapshotTaskInput sampleSnapshotTaskInput1 = new SampleSnapshotTaskInput();
            sampleSnapshotTaskInput1.setDefinition(10L);
            sampleSnapshotTaskInputs1[0] = sampleSnapshotTaskInput1;

            mediaProcessTaskInput1.setSampleSnapshotTaskSet(sampleSnapshotTaskInputs1);

            req.setMediaProcessTask(mediaProcessTaskInput1);

            // 返回的resp是一个ProcessMediaResponse的实例，与请求对象对应
            ProcessMediaResponse resp = client.ProcessMedia(req);
            // 输出json格式的字符串回包
            System.out.println(ProcessMediaResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }


}