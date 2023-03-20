package com.horqian.api.config.mvc;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.horqian.api.util.RSAUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author bz
 * @date 2023/03/02
 * @description
 */
public class RequestWrapperAES
//        extends HttpServletRequestWrapper
{

//    private Map<String, String[]> params = new HashMap<>();
//
//    /**
//     * 存储body数据的容器
//     */
//    private byte[] body;
//
//    /**
//     * Constructs a request object wrapping the given request.
//     *
//     * @param request
//     * @throws IllegalArgumentException if the request is null
//     */
//    public RequestWrapperAES(HttpServletRequest request) {
//        super(request);
//        //将参数表，赋予给当前的Map以便于持有request中的参数
//        this.params.putAll(request.getParameterMap());
//    }
//
//    /**
//     * 重载构造方法
//     */
//
//    public RequestWrapperAES(HttpServletRequest request, Map<String, Object> extendParams) {
//        this(request);
//        //这里将扩展参数写入参数表
//        addAllParameters(extendParams);
//    }
//
//    public RequestWrapperAES(HttpServletRequest request, Integer o) {
//        this(request);
//        //接下来的request使用这个
//        String bodyStr = getBodyString(request);
//        body = bodyStr.getBytes(Charset.defaultCharset());
//    }
//
//
////    public ParameterRequestWrapper(HttpServletRequest request, Map<String, String[]> extendParams) {
////        this(request);
////        //这里将扩展参数写入参数表
////        addAllParameters(extendParams);
////    }
//
//    /**
//     * 在获取所有的参数名,必须重写此方法，否则对象中参数值映射不上
//     *
//     * @return
//     */
//    @Override
//    public Enumeration<String> getParameterNames() {
//        return new Vector(params.keySet()).elements();
//    }
//
//    /**
//     * 重写getParameter方法
//     *
//     * @param name 参数名
//     * @return 返回参数值
//     */
//    @Override
//    public String getParameter(String name) {
//        String[] values = params.get(name);
//        if (values == null || values.length == 0) {
//            return null;
//        }
//        return values[0];
//    }
//
//    @Override
//    public String[] getParameterValues(String name) {
//        String[] values = params.get(name);
//        if (values == null || values.length == 0) {
//            return null;
//        }
//        return values;
//    }
//
//    /**
//     * 增加多个参数
//     *
//     * @param otherParams 增加的多个参数
//     */
//    public void addAllParameters(Map<String, Object> otherParams) {
//        for (Map.Entry<String, Object> entry : otherParams.entrySet()) {
//            addParameter(entry.getKey(), entry.getValue());
//        }
//    }
//
//    /**
//     * 增加参数
//     *
//     * @param name  参数名
//     * @param value 参数值
//     */
//    public void addParameter(String name, Object value) {
//        if (value != null) {
//            if (value instanceof String[]) {
//                params.put(name, (String[]) value);
//            } else if (value instanceof String) {
//                params.put(name, new String[]{(String) value});
//            } else {
//                params.put(name, new String[]{String.valueOf(value)});
//            }
//        }
//    }
//
//
//
//    /**
//     * 获取请求Body
//     *
//     * @param request request
//     * @return String
//     */
//    public String getBodyString(final ServletRequest request) {
//        try {
//            return inputStream2String(request.getInputStream());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * 获取请求Body
//     *
//     * @return String
//     */
//    public String getBodyString() {
//        final InputStream inputStream = new ByteArrayInputStream(body);
//
//        return inputStream2String(inputStream);
//    }
//
//    /**
//     * 将inputStream里的数据读取出来并转换成字符串
//     *
//     * @param inputStream inputStream
//     * @return String
//     */
//    private String inputStream2String(InputStream inputStream) {
//        StringBuilder sb = new StringBuilder();
//        BufferedReader reader = null;
//
//        try {
//            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//        } catch (IOException e) {
//
//            throw new RuntimeException(e);
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//
//                }
//            }
//        }
////        final String decode = rsaUtils.decode(sb.toString());
//        JSONObject jsonObject = JSONObject.parseObject(sb.toString());
//        if (jsonObject != null ) {
////            jsonObject.keySet().stream().forEach(x -> {
////
////            });
//            for (String x : jsonObject.keySet()) {
//                try {
//                    final String s = jsonObject.get(x).toString();
//                    final String[] split = s.split(",");
//                    AtomicReference<String> str = new AtomicReference<>("");
//                    Arrays.stream(split).forEachOrdered(rsaStr -> str.set(str + RSAUtils.decrypt(rsaStr)));
//                    JSONArray jsonArray = null;
//                    try {
//                        jsonArray = JSONUtil.parseArray(str.get());
//                    }catch (Exception e){
//
//                    }
//
//                    if (jsonArray != null){
//                        JSONArray jsonArray1 = new JSONArray();
//                        jsonArray.stream().forEach(json -> {
//                            JSONObject jsonObject1 = new JSONObject();
//                            JSONUtil.parseObj(json).keySet().stream().forEach(time -> {
//                                if (time.equals("createTime") || time.equals("updateTime")) {
//                                    String longNum = JSONUtil.parseObj(json).get(time).toString();
//                                    Long aLong = Long.valueOf(longNum);
//                                    LocalDateTime localDateTime =
//                                            LocalDateTime.ofEpochSecond(aLong / 1000, 0, ZoneOffset.ofHours(8));
//                                    jsonObject1.put(time, "");
//                                }else
//                                    jsonObject1.put(time, JSONUtil.parseObj(json).get(time));
//
//                            });
//                            jsonArray1.add(jsonObject1);
//                        });
//                        jsonObject.put(x, jsonArray1);
//                    }else {
//                        jsonObject.put(x, str);
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }
//            return jsonObject.toJSONString();
//        }
//        return sb.toString();
//    }
//
//    @Override
//    public BufferedReader getReader() throws IOException {
//        return new BufferedReader(new InputStreamReader(getInputStream()));
//    }
//
//    @Override
//    public ServletInputStream getInputStream() throws IOException {
//
//        final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);
//
//        return new ServletInputStream() {
//            @Override
//            public int read() throws IOException {
//                return inputStream.read();
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public boolean isReady() {
//                return false;
//            }
//
//            @Override
//            public void setReadListener(ReadListener readListener) {
//            }
//        };
//    }

}
