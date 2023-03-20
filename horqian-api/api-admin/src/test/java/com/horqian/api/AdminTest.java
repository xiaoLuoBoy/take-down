package com.horqian.api;

import com.horqian.api.config.wx.WXConfig;
import com.horqian.api.mapper.sys.SysRoleMapper;
import com.horqian.api.util.CommonUtils;
import com.horqian.api.util.ObsUtil;
import com.horqian.api.util.WxTrTcUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

;import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author bz
 * @date 2021/07/01
 * @description
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdminApplication.class)
public class AdminTest {


    @Autowired
    private WxTrTcUtil wxTrTcUtil;

    @Autowired
    private ObsUtil obsUtil;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Test
    public void obsUtils() throws IOException {

        String url = "http://1305395829.vod2.myqcloud.com/0c26f52fvodcq1305395829/db00fcd1243791579005513915/f0.mp4";


        File file = obsUtil.downloadFile(url, ".mp4");
        try {
            String obs = obsUtil.uploadFile(new MockMultipartFile("file", CommonUtils.getTimeStr() + ".mp4", "mp4",
                    new FileInputStream(file)), "video", ".mp4");
            System.out.println(obs);
        } finally {
            file.delete();
        }

    }

    @Test
    public void cosSend() throws TencentCloudSDKException {
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
        req.setSmsSdkAppId("1400688355");

        /*
         * 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名
         * 签名信息可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-sign) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-sign) 的签名管理查看
         */
        // 签名: 电科思仪 中电科思仪科技股份有限公
        req.setSignName("电科思仪");

        /*
         * 模板 ID: 必须填写已审核通过的模板 ID
         * 模板 ID 可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-template) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-template) 的正文模板管理查看
         */
        req.setTemplateId("1448581");

        /*
         * 模板参数: 模板参数的个数需要与 TemplateId 对应模板的变量个数保持一致，若无模板参数，则设置为空
         */
         String[] templateParamSet = {"1234","5"};
        //if (sysMessage.getData() != null) {
            // 数组长度
            //String[] templateParamSet = sysMessage.getData().toArray(new String[sysMessage.getData().size()]);
            req.setTemplateParamSet(templateParamSet);
        //}

        /*
         * 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
         * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号
         */
         String[] phoneNumberSet = {"+8615965302910"};
        //String[] phoneNumberSet = phoneList.toArray(new String[phoneList.size()]);
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

    }

    @Test
    public void a1() {
        List<Map<String, Object>> select = sysRoleMapper.select("select * from sys_user");

    }





}
