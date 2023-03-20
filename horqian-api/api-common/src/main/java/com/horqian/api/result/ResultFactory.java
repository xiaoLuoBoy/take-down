package com.horqian.api.result;


import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.horqian.api.constant.CodeConstants;
import com.horqian.api.constant.TextConstants;
import com.horqian.api.util.AESUtil;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bz
 */
public class ResultFactory {

    public static <T> JsonResult<T> success() {
//        return new JsonResult<T>(CodeConstants.STATUS_SUCCEED, TextConstants.STATUS_SUCCEED_MSG);
        return  aes(new JsonResult<T>(CodeConstants.STATUS_SUCCEED, TextConstants.STATUS_SUCCEED_MSG));
    }

    public static <T> JsonResult<T> success(String message) {
//        return new JsonResult<T>(CodeConstants.STATUS_SUCCEED, message);
        return  aes(new JsonResult<T>(CodeConstants.STATUS_SUCCEED, message));
    }

    public static <T> JsonResult<T> success(T bean) {
//        return new JsonResult<T>(CodeConstants.STATUS_SUCCEED, bean, TextConstants.STATUS_SUCCEED_MSG);
        return  aes(new JsonResult<T>(CodeConstants.STATUS_SUCCEED, bean, TextConstants.STATUS_SUCCEED_MSG));
    }

    public static <T> JsonResult<T> success(T bean, String message) {
//        return new JsonResult<T>(CodeConstants.STATUS_SUCCEED, bean, message);
        return  aes(new JsonResult<T>(CodeConstants.STATUS_SUCCEED, bean, message));
    }

    public static <T> JsonResult<T> success(T bean, long total) {
//        return new JsonResult<T>(CodeConstants.STATUS_SUCCEED, bean, total, TextConstants.STATUS_SUCCEED_MSG);
        return  aes(new JsonResult<T>(CodeConstants.STATUS_SUCCEED, bean, total, TextConstants.STATUS_SUCCEED_MSG));
    }

    public static <T> JsonResult<T> success(T bean, long total, String message) {
//        return new JsonResult<T>(CodeConstants.STATUS_SUCCEED, bean, total, message);
        return  aes(new JsonResult<T>(CodeConstants.STATUS_SUCCEED, bean, total, message));
    }


    public static <T> JsonResult<T> error(String message) {
//        return new JsonResult<T>(CodeConstants.STATUS_ERROR, message);
        return  aes(new JsonResult<T>(CodeConstants.STATUS_ERROR, message));
    }

    public static <T> JsonResult<T> unLogin() {
//        return new JsonResult<T>(CodeConstants.STATUS_UN_LOGIN, TextConstants.STATUS_UN_LOGIN_MSG);
        return  aes(new JsonResult<T>(CodeConstants.STATUS_UN_LOGIN, TextConstants.STATUS_UN_LOGIN_MSG));
    }

    public static <T> JsonResult<T> noPerm() {
//        return new JsonResult<T>(CodeConstants.STATUS_NO_PERM, TextConstants.STATUS_NO_PERM_MSG);
        return aes(new JsonResult<T>(CodeConstants.STATUS_NO_PERM, TextConstants.STATUS_NO_PERM_MSG));
    }

    private static <T> JsonResult<T> aes(JsonResult jsonResult){
        JSONConfig jsonConfig = JSONConfig.create().setDateFormat("yyyy-MM-dd HH:mm:ss");
        final JSONObject jsonObject = JSONUtil.parseObj(jsonResult, jsonConfig);
        String data = jsonObject.toString();
//        String str = "";
        String str1 = "";
//        List<String> list = Arrays.asList(data.split(""));
//        Integer num = (list.size() - (list.size() % 39)) / 39;
//        if (list.size() % 39 != 0)
//            num += 1;
//        if (list.size() >= 39)
//            for (int i = 0; i < num; i++ ){
//                final String encode =list.subList(i * 39, num - 1 == i ? i * 39 + list.size() % 39 : i * 39 + 39).stream().collect(Collectors.joining(""));
//                if (str.length() == 0){
//                    str = str + AESUtil.encryptCBC(encode);
//                    str1 = str1 + encode;
//                }
//
//                else{
//                    str = str + "," + AESUtil.encryptCBC(encode);
//                    str1 = str1 + "," + encode;
//                }
//
//            }
//        else {
//            str = str + AESUtil.encryptCBC(list.stream().collect(Collectors.joining("")));
//        }

//        final String s = str.split(",")[0];
//        jsonObject.keySet().stream()
        return new JsonResult<T>(AESUtil.encryptCBC(data));
    }
}