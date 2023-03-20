package com.horqian.api.util;


import com.obs.services.ObsClient;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

/**
 * @author: 孟
 * @date: 2023/2/13 13:45
 * @Version 1.0
 */
@Component
public class CosUtil {
    // 地址
    private static final String endPoint = "https://41s-meeting-1312282690.cos.ap-beijing.myqcloud.com/41s-video/";

    private static final String secretId = "AKID51mKdk8KHzsa5NSN5gBZlkWAmFWr6C6u";

    private static final String secretKey = "jLtBUeaLsAE3dN3Xs44wqeLqKk6QixCQ";

    private static final String bucketName = "41s-meeting-1312282690";


    /**
     * 获取client对象
     *
     * @return
     */
    public COSClient getClient() {
        // 1 初始化用户身份信息（secretId, secretKey）。
        // SECRETID 和 SECRETKEY 请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
        // String secretId = System.getenv("secretId");//用户的 SecretId，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
        // String secretKey = System.getenv("secretKey");//用户的 SecretKey，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的地域, COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region("ap-beijing");
        ClientConfig clientConfig = new ClientConfig(region);
        // 这里建议设置使用 https 协议
        // 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
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
     * @param bucketName 桶名称
     * @param type       文件夹层级
     * @param prefix     前缀
     * @return
     * @throws IOException
     */
    public String upload(MultipartFile file, String bucketName, String type, String prefix) throws IOException {

        COSClient cosClient = getClient();
        // 指定文件上传到 COS 上的路径，即对象键。例如对象键为 folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        // 数据流类型需要额外定义一个参数
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
        // 如果确实没办法获取到，则下面这行可以省略，但同时高级接口也没办法使用分块上传了
        //objectMetadata.setContentLength(inputStreamLength);
        // 文件存储路径
        String path = file.getOriginalFilename() + System.currentTimeMillis();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, "/" + "41s-video" + "/" + path , file.getInputStream(), objectMetadata);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        return putObjectResult.getRequestId();
    }

    /**
     * cos上传文件
     *
     * @param file       文件
     * @param bucketName 桶名称
     * @param type       文件夹层级
     * @param prefix     前缀
     * @return
     * @throws IOException
     */
    public String uploadFile(MultipartFile file, String bucketName, String type, String prefix, String suffix) throws IOException {
        COSClient cosClient = getClient();
        // 指定文件上传到 COS 上的路径，即对象键。例如对象键为 folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        // 数据流类型需要额外定义一个参数
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
        // 如果确实没办法获取到，则下面这行可以省略，但同时高级接口也没办法使用分块上传了
        //objectMetadata.setContentLength(inputStreamLength);
        // 文件存储路径
        String path = System.currentTimeMillis() + file.getOriginalFilename();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, "/" + "41s-video" + "/" + path, file.getInputStream(), objectMetadata);
        // 上传文件
        cosClient.putObject(putObjectRequest);
        cosClient.shutdown();
        return endPoint + path;
    }

    /**
     * obs上传视频文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public String uploadVideo(MultipartFile file, String suffix) throws IOException {
        COSClient cosClient = getClient();
        // 指定文件上传到 COS 上的路径，即对象键。例如对象键为 folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        // 数据流类型需要额外定义一个参数
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
        // 如果确实没办法获取到，则下面这行可以省略，但同时高级接口也没办法使用分块上传了
        //objectMetadata.setContentLength(inputStreamLength);
        // 文件存储路径
        String path = System.currentTimeMillis() + file.getOriginalFilename();
        PutObjectRequest putObjectRequest = new PutObjectRequest("41s-meeting-1312282690", "/" + "41s-video" + "/" + path, file.getInputStream(), objectMetadata);
        cosClient.shutdown();
        return endPoint + path;
    }

    // 上传大文件
    public void blockUpload(@RequestParam("file") MultipartFile file) throws Exception {
        // 文件的完整名字
        String fileName = file.getOriginalFilename();
        String type = fileName.substring(fileName.lastIndexOf(".") + 1);
        Random rand = new Random();
        int random = rand.nextInt();
        // 上传后的名称
        String key = System.currentTimeMillis() + (random > 0 ? random : (-1) * random) + "." + type;
        // 获取分块上传的 uploadId
        String uploadId = InitMultipartUploadDemo(key);
        // 获取上传的所有分块信息
        List<PartETag> partETags = UploadPartDemo(uploadId, key, file);
        // 完成分片上传
        completePartDemo(uploadId, partETags, key);
    }


    /**
     * 获取分块上传的 uploadId
     *
     * @param key
     * @return
     */
    public String InitMultipartUploadDemo(String key) {
        // 获取客户端
        COSClient cosClient = getClient();
        // 初始化分块上传任务
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, key);
        //// 设置存储类型, 默认是标准(Standard), 低频(Standard_IA), 归档(Archive)
        //request.setStorageClass(StorageClass.Standard);
        try {
            InitiateMultipartUploadResult initResult = cosClient.initiateMultipartUpload(request);
            // 获取uploadid
            String uploadId = initResult.getUploadId();
            return uploadId;
        } catch (CosServiceException e) {
            throw e;
        } catch (CosClientException e) {
            throw e;
        } finally {
            cosClient.shutdown();
        }
    }

    /**
     * 获取上传的所有分块信息
     *
     * @param uploadId
     * @param key
     * @param file
     * @return
     * @throws IOException
     */
    public List<PartETag> UploadPartDemo(String uploadId, String key, MultipartFile file) throws IOException {
        // 获取客户端
        COSClient cosClient = getClient();
        // 是否进行流量控制
        boolean userTrafficLimit = false;
        // 所有分块
        List<PartETag> partETags = new LinkedList<>();
        // 每片分块的大小
        int partSize = 1024 * 1024 * 100;
        // 上传文件的大小
        long fileSize = file.getSize();
        // 分块片数
        int partCount = (int) (fileSize / partSize);
        if (fileSize % partSize != 0) {
            partCount++;
        }
        // 生成要上传的数据, 这里初始化一个10M的数据
        for (int i = 0; i < partCount; i++) {
            // 起始位置
            long startPos = i * partSize;
            // 分片大小
            long curPartSize = (i + 1 == partCount) ? (fileSize - startPos) : partSize;
            // 大文件
            InputStream instream = file.getInputStream();
            // 跳过已经上传的分片。
            instream.skip(startPos);
            // 用于上传分块请求
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            // 存储桶命名
            uploadPartRequest.setBucketName(bucketName);
            // 指定分块上传到 COS 上的路径
            uploadPartRequest.setKey(key);
            // 标识指定分块上传的 uploadId
            uploadPartRequest.setUploadId(uploadId);
            // 设置分块的数据来源输入流
            uploadPartRequest.setInputStream(instream);
            // 设置分块的长度
            uploadPartRequest.setPartSize(curPartSize);
            // 上传分片编号
            uploadPartRequest.setPartNumber(i + 1);
            // 是否进行流量控制
            if (userTrafficLimit) {
                // 用于对上传对象进行流量控制,单位：bit/s，默认不进行流量控制
                uploadPartRequest.setTrafficLimit(8 * 1024 * 1024);
            }
            try {
                // 上传分片数据的输入流
                UploadPartResult uploadPartResult = cosClient.uploadPart(uploadPartRequest);
                // 获取上传分块信息
                PartETag partETag = uploadPartResult.getPartETag();
                // 把上传返回的分块信息放入到分片集合中
                partETags.add(partETag);
                // 获取复制生成对象的CRC64
                String crc64 = uploadPartResult.getCrc64Ecma();
            } catch (CosServiceException e) {
                throw e;
            } catch (CosClientException e) {
                throw e;
            }
        }
        cosClient.shutdown();
        return partETags;
    }

    /**
     * 完成分片上传
     *
     * @param uploadId
     * @param partETags
     * @param key
     */
    public void completePartDemo(String uploadId, List<PartETag> partETags, String key) {
        // 分片上传结束后，调用complete完成分片上传
        COSClient cosClient = getClient();
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, key, uploadId, partETags);
        try {
            CompleteMultipartUploadResult completeResult = cosClient.completeMultipartUpload(completeMultipartUploadRequest);
            // 返回结果打印
            System.out.println(completeResult);
        } catch (CosServiceException e) {
            throw e;
        } catch (CosClientException e) {
            throw e;
        }
        cosClient.shutdown();
    }



}
