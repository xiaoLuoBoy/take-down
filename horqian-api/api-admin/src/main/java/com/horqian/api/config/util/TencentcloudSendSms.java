package com.horqian.api.config.util;

import com.horqian.api.config.wx.WXConfig;
import com.horqian.api.entity.sys.SysMessage;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;


import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.springframework.stereotype.Component;


import java.util.List;

/**
 * @author: 孟
 * @date: 2023/2/17 15:20
 * @Version 1.0
 */
public  class TencentcloudSendSms {

    private final static String appId = "1400688355";

    /**
     * 腾讯云短信发送
     *
     * @param sysMessage    短信模板
     * @param phoneList     手机号集合
     */
    public static void sendSms(SysMessage sysMessage, List<String> phoneList) {
        try {
            /*
             * 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
             */
            Credential cred = new Credential(WXConfig.SecretId, WXConfig.SecretKey);
            /*
             * 实例化要请求产品(以sms为例)的client对象
             * 第二个参数是地域信息，可以直接填写字符串ap-guangzhou，支持的地域列表参考 https://cloud.tencent.com/document/api/382/52071#.E5.9C.B0.E5.9F.9F.E5.88.97.E8.A1.A8
             */
            SmsClient client = new SmsClient(cred, "ap-beijing");

            /*
             * 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
             * 你可以直接查询SDK源码确定接口有哪些属性可以设置
             * 属性可能是基本类型，也可能引用了另一个数据结构
             * 推荐使用IDE进行开发，可以方便的跳转查阅各个接口和数据结构的文档说明
             */
            SendSmsRequest req = new SendSmsRequest();
            /*
             * 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId，示例如1400006666
             * 应用 ID 可前往 [短信控制台](https://console.cloud.tencent.com/smsv2/app-manage) 查看
             */
            req.setSmsSdkAppId(appId);

            /*
             * 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名
             * 签名信息可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-sign) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-sign) 的签名管理查看
             */
            // 签名: 电科思仪 中电科思仪科技股份有限公
            req.setSignName(sysMessage.getSignature());

            /*
             * 模板 ID: 必须填写已审核通过的模板 ID
             * 模板 ID 可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-template) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-template) 的正文模板管理查看
             */
            req.setTemplateId(sysMessage.getTemplateId());

            /*
             * 模板参数: 模板参数的个数需要与 TemplateId 对应模板的变量个数保持一致，若无模板参数，则设置为空
             */
            // String[] templateParamSet = {"1234"};
            if (sysMessage.getData() != null) {
                // 数组长度
                String[] templateParamSet = sysMessage.getData().toArray(new String[sysMessage.getData().size()]);
                req.setTemplateParamSet(templateParamSet);
            }

            /*
             * 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
             * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号
             */
            // String[] phoneNumberSet = {"+8621212313123", "+8612345678902", "+8612345678903"};
            String[] phoneNumberSet = phoneList.toArray(new String[phoneList.size()]);
            req.setPhoneNumberSet(phoneNumberSet);

            /*
             * 用户的 session 内容（无需要可忽略）: 可以携带用户侧 ID 等上下文信息，server 会原样返回
             */

            // String sessionContext = "";
            // req.setSessionContext(sessionContext);

            /*
             * 短信码号扩展号（无需要可忽略）: 默认未开通，如需开通请联系 [腾讯云短信小助手]
             */

            // String extendCode = "";
            // req.setExtendCode(extendCode);

            /*
             * 国际/港澳台短信 SenderId（无需要可忽略）: 国内短信填空，默认未开通，如需开通请联系 [腾讯云短信小助手]
             */

            // String senderid = "";
            // req.setSenderId(senderid);

            /*
             * 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
             * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应
             */
            SendSmsResponse res = client.SendSms(req);

            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(res));

            // 也可以取出单个值，你可以通过官网接口文档或跳转到response对象的定义处查看返回字段的定义
            // System.out.println(res.getRequestId());

            /* 当出现以下错误码时，快速解决方案参考
             * [FailedOperation.SignatureIncorrectOrUnapproved](https://cloud.tencent.com/document/product/382/9558#.E7.9F.AD.E4.BF.A1.E5.8F.91.E9.80.81.E6.8F.90.E7.A4.BA.EF.BC.9Afailedoperation.signatureincorrectorunapproved-.E5.A6.82.E4.BD.95.E5.A4.84.E7.90.86.EF.BC.9F)
             * [FailedOperation.TemplateIncorrectOrUnapproved](https://cloud.tencent.com/document/product/382/9558#.E7.9F.AD.E4.BF.A1.E5.8F.91.E9.80.81.E6.8F.90.E7.A4.BA.EF.BC.9Afailedoperation.templateincorrectorunapproved-.E5.A6.82.E4.BD.95.E5.A4.84.E7.90.86.EF.BC.9F)
             * [UnauthorizedOperation.SmsSdkAppIdVerifyFail](https://cloud.tencent.com/document/product/382/9558#.E7.9F.AD.E4.BF.A1.E5.8F.91.E9.80.81.E6.8F.90.E7.A4.BA.EF.BC.9Aunauthorizedoperation.smssdkappidverifyfail-.E5.A6.82.E4.BD.95.E5.A4.84.E7.90.86.EF.BC.9F)
             * [UnsupportedOperation.ContainDomesticAndInternationalPhoneNumber](https://cloud.tencent.com/document/product/382/9558#.E7.9F.AD.E4.BF.A1.E5.8F.91.E9.80.81.E6.8F.90.E7.A4.BA.EF.BC.9Aunsupportedoperation.containdomesticandinternationalphonenumber-.E5.A6.82.E4.BD.95.E5.A4.84.E7.90.86.EF.BC.9F)
             * 更多错误，可咨询[腾讯云助手](https://tccc.qcloud.com/web/im/index.html#/chat?webAppId=8fa15978f85cb41f7e2ea36920cb3ae1&title=Sms)
             */

        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
    }
}
