package com.horqian.api.util;

import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author bz
 */
public class CommonUtils {

    /**
     * 转换为下划线
     *
     * @param camelCaseName
     * @return
     */
    public static String camelCaseToUnderline(String camelCaseName) {
        StringBuilder result = new StringBuilder();
        if (camelCaseName != null && camelCaseName.length() > 0) {
            result.append(camelCaseName.substring(0, 1).toLowerCase());
            for (int i = 1; i < camelCaseName.length(); i++) {
                char ch = camelCaseName.charAt(i);
                if (Character.isUpperCase(ch)) {
                    result.append("_");
                    result.append(Character.toLowerCase(ch));
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }

    /**
     * 转换为驼峰
     *
     * @param underlineName
     * @return
     */
    public static String underlineToCamelCase(String underlineName) {
        StringBuilder result = new StringBuilder();
        if (underlineName != null && underlineName.length() > 0) {

            boolean flag = false;
            for (int i = 0; i < underlineName.length(); i++) {
                char ch = underlineName.charAt(i);
                if ("_".charAt(0) == ch) {
                    flag = true;
                } else {
                    if (flag) {
                        result.append(Character.toUpperCase(ch));
                        flag = false;
                    } else {
                        result.append(ch);
                    }
                }
            }
        }
        return result.toString();
    }

    public static Object getFieldValue(String fieldName, Object o) throws Exception {

        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String getter = "get" + firstLetter + fieldName.substring(1);
        Method method = o.getClass().getMethod(getter);
        return method.invoke(o);

    }

    public static String getTodayStr() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static String getTimeStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    public static String getRealPath(String relativePath) throws IOException {

        String uploadPath = null;
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if (!path.exists()) {
            path = new File("");
        }
        File upload = new File(path.getAbsolutePath(), relativePath);
        if (!upload.exists()) {
            upload.mkdirs();
        }
        uploadPath = upload.getAbsolutePath();
        return uploadPath;
    }

    public static String uploadFile(MultipartFile multipartFile, String path) throws IOException {

        // String relativePath = "files" + File.separator + path + File.separator +
        // System.currentTimeMillis();
        String relativePath = "files" + "/" + path + "/" + System.currentTimeMillis();

        String uploadUrl = getRealPath(relativePath);

        String fileName = multipartFile.getOriginalFilename();
        File file = new File(uploadUrl, fileName);
        // 判断路径是否存在，如果不存在就创建一个
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        multipartFile.transferTo(new File(uploadUrl + File.separator + fileName));

        return relativePath + "/" + fileName;

    }

    public static String exportPath() throws IOException {
        String relativePath = "files/export/" + System.currentTimeMillis();

        return getRealPath(relativePath);
    }


    public static void download(HttpServletRequest request, HttpServletResponse response, String filePath)
            throws IOException {

        File file = new File(getRealPath(filePath));
        downloadFile(request, response, file);

    }

    public static void download(HttpServletRequest request, HttpServletResponse response, File file)
            throws IOException {
        downloadFile(request, response, file);
    }


    public static void downloadFile(HttpServletRequest request, HttpServletResponse response, File file)
            throws IOException {


        try {

            InputStream is = new FileInputStream(file);
            response.reset();
            String userAgent = request.getHeader("User-Agent");
            byte[] bytes = userAgent.contains("MSIE") ? file.getName().getBytes() : file.getName().getBytes(StandardCharsets.UTF_8);
            String fileName = new String(bytes, StandardCharsets.ISO_8859_1);
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
            response.setContentType("application/octet-stream; charset=UTF-8");
            response.setHeader("Content-Length", String.valueOf(file.length()));

            OutputStream out = response.getOutputStream();
            byte[] content = new byte[1024];
            int length = 0;
            while ((length = is.read(content)) != -1) {
                out.write(content, 0, length);
            }
            out.write(content);
            out.flush();
            out.close();

            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}