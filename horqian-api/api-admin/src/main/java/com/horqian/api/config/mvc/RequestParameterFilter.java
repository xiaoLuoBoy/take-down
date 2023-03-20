package com.horqian.api.config.mvc;

import com.horqian.api.util.AESUtil;
import com.horqian.api.util.RSAUtils;
import com.horqian.api.util.RSAUtils3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bz
 * @date 2023/03/02
 * @description
 */
@Component
public class RequestParameterFilter extends OncePerRequestFilter {

    /**
     * 过滤路径
     */
    private final String AUTH_PATH = "/sys/";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


//        final String decode = RSAUtils.decrypt("A1qYWFS+ZUlP10Ww64uVMlPlWj2RaJpeyCYHD1dkBgitBmjpvpRdMjC1psfUPOrThgfN+amRYcfJ/hovTB7W1jfxiy47PLUbn76Km21+rXgpg7207vmI8DSEfK/zg97mNEhBMTBHyFiK2oVrNzKZGxhnM89srey6HxM1E2VnZkSzOgCt1X0oA5wTl9Z6aBM7IAdV6l6VP7UblyU8CbpOF5HvDqs7ZbCoS6zlFHGwcYTKiVtGTydzsjjoBhzZmJPOLtOWcB8EGJAMnPFILmtutE79GB2LHSH6E2BSAXuCeBDlFt9tEQot5WDN/7MExgnpgaj2Yq9Oe+6m5LpFjwBidQ==");
        /*如果请求路径是为app,进行过滤对参数parameter内容解密，放入request.parameter中*/
        final String header = request.getContentType();

        final Enumeration<String> headerNames = request.getHeaderNames();
//        if (StringUtils.hasText(header) && header.indexOf("application/json") != -1)
        if (StringUtils.hasText(header) && header.indexOf("application/json") != -1){
            RequestWrapper wrapper = new RequestWrapper(request, 1);
            filterChain.doFilter(wrapper, response);
            return;
        }
//            final String requestPostStr = getRequestPostStr(request);
        /*1.获取加密串,进行解密*/
        final Map<String, String[]> parameterMap = request.getParameterMap();
        Map paramter = new HashMap(16);

        parameterMap.keySet().stream().forEach(x -> {

            String str = parameterMap.get(x)[0];
            if (parameterMap.get(x)[0].contains(" ")) {
                str = str.replaceAll(" ", "+");
            }
            parameterMap.get(x)[0] = AESUtil.decryptCBC(str);
            paramter.put(x, parameterMap.get(x));
        });


        RequestWrapper wrapper = new RequestWrapper(request, paramter);

        filterChain.doFilter(wrapper, response);

    }

    public static String getRequestPostStr(HttpServletRequest request)
            throws IOException {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }

    public static byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        if(contentLength<0){
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {

            int readlen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

}

