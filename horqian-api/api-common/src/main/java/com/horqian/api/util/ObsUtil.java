package com.horqian.api.util;


import com.huaweicloud.sdk.core.auth.GlobalCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.iam.v3.IamClient;
import com.huaweicloud.sdk.iam.v3.model.*;
import com.huaweicloud.sdk.iam.v3.region.IamRegion;
import com.obs.services.ObsClient;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectResult;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.util.*;


@Component
public class ObsUtil {
    private static final String endPoint = "obs.cn-north-4.myhuaweicloud.com";
    private static final String ak = "E36RAKKAVTC1BSDOM3RQ";
    private static final String sk = "eKMChmxZwyRVZm1h2dNgA9AxttLLGCAOvGZgqP3m";

    /**
     * 获取client对象
     *
     * @return
     */
    public ObsClient getClient() {
        return new ObsClient(ak, sk, endPoint);
    }


    /**
     * url转变为 MultipartFile对象
     * @param url
     * @return
     * @throws Exception
     */
    public static File downloadFile(String url, String contentType) {
        File file = null;

        URL urlfile;
        InputStream inputStream = null;
        OutputStream outputStream= null;
        try {
            file = File.createTempFile("file" + UUID.randomUUID() , contentType);
            //下载
            urlfile = new URL(url);
            inputStream = urlfile.openStream();
            outputStream= new FileOutputStream(file);

            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    /**
     * obs上传图片
     *
     * @param file       文件
     * @param bucketname 桶名称
     * @param type       文件夹层级
     * @param prefix     前缀
     * @return
     * @throws IOException
     */
    public String upload(MultipartFile file, String bucketname, String type, String prefix) throws IOException {
        ObsClient client = getClient();
        boolean bool = client.headBucket(bucketname);
        if (!bool)
            createBucket(client, bucketname);
        PutObjectResult putObjectResult = client.putObject(bucketname, "/" + type + "/" + prefix + CommonUtils.getTimeStr() + ".jpg", file.getInputStream());
        return putObjectResult.getObjectUrl();
    }

    /**
     * obs上传文件
     *
     * @param file       文件
     * @param bucketname 桶名称
     * @param type       文件夹层级
     * @param prefix     前缀
     * @return
     * @throws IOException
     */
    public String upload(MultipartFile file, String bucketname, String type, String prefix, String suffix) throws IOException {
        ObsClient client = getClient();
        boolean bool = client.headBucket(bucketname);
        if (!bool)
            createBucket(client, bucketname);
        PutObjectResult putObjectResult = client.putObject(bucketname, "/" + type + "/" + prefix + CommonUtils.getTimeStr() + suffix, file.getInputStream());
        return putObjectResult.getObjectUrl();
    }



    /**
     * obs上传文件
     *
     * @param file       文件
     * @param bucketname 桶名称
     * @param type       文件夹层级
     * @param prefix     前缀
     * @return
     * @throws IOException
     */
    public String uploadStream(MultipartFile file, String bucketname, String type, String prefix, String suffix) throws IOException {
        ObsClient client = getClient();
        boolean bool = client.headBucket(bucketname);
        if (!bool)
            createBucket(client, bucketname);
        PutObjectResult putObjectResult = client.putObject(bucketname, "/" + type + "/" + prefix + CommonUtils.getTimeStr() + suffix, file.getInputStream());
        return putObjectResult.getObjectUrl();
    }


    /**
     * obs上传文件
     *
     * @param file       文件
     * @param type       文件夹层级
     * @return
     * @throws IOException
     */
    public String uploadFile(MultipartFile file, String type, String suffix) throws IOException {
        ObsClient client = getClient();
        boolean bool = client.headBucket("41s-meeting");
        if (!bool)
            createBucket(client, "41s-meeting");
        PutObjectResult putObjectResult = client.putObject("41s-meeting", "/" + type + "/" + CommonUtils.getTimeStr() + "/"  + suffix, file.getInputStream());
        return putObjectResult.getObjectUrl();
    }

    public void createBucket(ObsClient client, String bucketname) {
        client.createBucket(bucketname);
    }

    /**
     * 获取临时凭证秘钥
     *
     * @return
     */
    public Map<String, String> createTemporaryAccessKeyByToken() {
        ICredential auth = new GlobalCredentials()
                .withAk(ak)
                .withSk(sk);
        IamClient client = IamClient.newBuilder()
                .withCredential(auth)
                .withRegion(IamRegion.valueOf("cn-east-2"))
                .build();
        CreateTemporaryAccessKeyByTokenRequest request = new CreateTemporaryAccessKeyByTokenRequest();
        CreateTemporaryAccessKeyByTokenRequestBody body = new CreateTemporaryAccessKeyByTokenRequestBody();
        List<TokenAuthIdentity.MethodsEnum> listIdentityMethods = new ArrayList<>();
        listIdentityMethods.add(TokenAuthIdentity.MethodsEnum.fromValue("token"));
        TokenAuthIdentity identityAuth = new TokenAuthIdentity();
        identityAuth.withMethods(listIdentityMethods);
        TokenAuth authbody = new TokenAuth();
        authbody.withIdentity(identityAuth);
        body.withAuth(authbody);
        request.withBody(body);
        CreateTemporaryAccessKeyByTokenResponse response = client.createTemporaryAccessKeyByToken(request);
        Map<String, String> map = new HashMap<>();
        map.put("expireAt", response.getCredential().getExpiresAt());
        map.put("access", response.getCredential().getAccess());
        map.put("secret", response.getCredential().getSecret());
        map.put("securitytoken", response.getCredential().getSecuritytoken());
        return map;
    }

    /**
     * 获取输入流
     *
     * @param fileName
     * @return
     */
    public InputStream getInputStream(String bucketname, String fileName) {
        ObsClient obsClient = getClient();
        InputStream input = null;
        try {
            ObsObject obsObject = obsClient.getObject(bucketname, fileName);
            input = obsObject.getObjectContent();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return input;
    }



}
