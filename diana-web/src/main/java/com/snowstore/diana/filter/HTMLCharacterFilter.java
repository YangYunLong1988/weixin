package com.snowstore.diana.filter;
import java.io.IOException;  
  

import javax.servlet.Filter;  
import javax.servlet.FilterChain;  
import javax.servlet.FilterConfig;  
import javax.servlet.ServletException;  
import javax.servlet.ServletRequest;  
import javax.servlet.ServletResponse;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletRequestWrapper;  
import javax.servlet.http.HttpServletResponse;  

import org.springframework.stereotype.Component;
  
/** 
 * 类说明：HTML特殊字符过滤器 
 *  
 * @author 作者: LiuJunGuang 
 * @version 创建时间：2011-11-18 下午07:36:44 
 */  
@Component
public class HTMLCharacterFilter implements Filter {  
  
    public void init(FilterConfig filterConfig) throws ServletException {  
  
    }  
  
    public void doFilter(ServletRequest req, ServletResponse resp,  
            FilterChain chain) throws IOException, ServletException {  
        HttpServletRequest request = (HttpServletRequest) req;  
        HttpServletResponse response = (HttpServletResponse) resp;  
        chain.doFilter(new HTMLCharacterRequest(request), response);  
    }  
  
    public void destroy() {  
  
    }  
}  
  
// html特殊字符处理类   
class HTMLCharacterRequest extends HttpServletRequestWrapper {  
  
    public HTMLCharacterRequest(HttpServletRequest request) {  
        super(request);  
    }  
  
    @Override  
    public String getParameter(String name) {  
        return filter(super.getParameter(name));  
    }  
    @Override  
    public String[] getParameterValues(String name) {  
        String[] values = super.getParameterValues(name);  
        if (values == null || values.length == 0)  
            return values;  
        for (int i = 0; i < values.length; i++) {  
            String str = values[i];  
            values[i] = filter(str);  
        }  
        return values;  
    }  
    /** 
     * 对特殊的html字符进行编码 
     *  
     * @param message 
     * @return 
     */  
    private String filter(String message) {  
  
        if (message == null)  
            return (null);  
  
        char content[] = new char[message.length()];  
        message.getChars(0, message.length(), content, 0);  
        StringBuilder result = new StringBuilder(content.length + 50);  
        for (int i = 0; i < content.length; i++) {  
            switch (content[i]) {  
            case '<':  
                result.append("&lt;");  
                break;  
            case '>':  
                result.append("&gt;");  
                break;  
            case '"':  
                result.append("&quot;");  
                break;  
            default:  
                result.append(content[i]);  
            }  
        }  
        return (result.toString());  
  
    }  
}  