package com.horqian.api.controller;

import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.util.CommonUtils;
import com.horqian.api.util.CosUtil;
import com.horqian.api.util.ObsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 文件相关
 *
 * @author bz
 * @date 2021/07/02
 * @description
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final ObsUtil obsUtil;

    private final CosUtil cosUtil;


    /**
     * 临时访问秘钥获取
     *
     * @return
     */
    @GetMapping("/getTemporarySecretKey")
    public Result getTemporarySecretKey() {
        return ResultFactory.success(obsUtil.createTemporaryAccessKeyByToken());
    }

    /**
     * 上传文件
     *
     * @param file   文件
     * @param type   文件夹层级
     * @param prefix 前缀
     * @return 图片路径
     * @throws IOException
     */
    @PostMapping("/upload/file")
    public Result upload(MultipartFile file, String type, String prefix, String suffix) throws IOException {
        String url = cosUtil.uploadFile(file, "41s-meeting-1312282690", type, prefix, suffix);
        return ResultFactory.success(url, "");
    }


}
