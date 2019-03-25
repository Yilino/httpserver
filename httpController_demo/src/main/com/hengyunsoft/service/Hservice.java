package main.com.hengyunsoft.service;

import com.alibaba.fastjson.JSON;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import main.com.hengyunsoft.Dao.User;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Hservice {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8001),0);
        server.createContext("/test",new TestHandler());
        server.start();
    }
    static class TestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        User user = new User();
                        user.setName("response");
                        user.setAge(18);
                        user.setGender("女");
                        user.setMotto("姿势要优雅~");
                        String response= JSON.toJSONString(user);
                        response= URLEncoder.encode(response);
                        String requestMethod = exchange.getRequestMethod();
                        if (requestMethod.equals("GET")){
                            //获得查询字符串(get)
                            String queryString =  exchange.getRequestURI().getQuery();
                            System.out.println("GET请求:");
                            System.out.println(queryString);
                            Map<String,String> queryStringInfo = formData2Dic(queryString,"GET");
                            System.out.println(queryStringInfo);
                        }else {
                            //获得表单提交数据(post)
                            String postString = IOUtils.toString(exchange.getRequestBody());
                            System.out.println("POST请求:");
                            System.out.println(postString);
                            Map<String,String> postInfo = formData2Dic(postString,"POST");
                            System.out.println(postInfo);
                        }
                        exchange.sendResponseHeaders(200,0);
                        OutputStream os = exchange.getResponseBody();

                        os.write(response.getBytes());
                        os.close();
                    }catch (IOException ie) {
                        ie.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static Map<String,String> formData2Dic(String formData,String requestMethod){
        Map<String,String> result = new HashMap<>();
        if (formData == null || formData.trim().length() == 0){
            return result;
        }
        if (requestMethod.equals("GET")){
            final String[] items = formData.split("&");
            Arrays.stream(items).forEach(item ->{
                final String[] keyAndVal = item.split("=");
                if (keyAndVal.length == 2){
                    try {
                        final String key = URLDecoder.decode(keyAndVal[0],"utf8");
                        final String val = URLDecoder.decode( keyAndVal[1],"utf8");
                        result.put(key,val);
                    }catch (UnsupportedEncodingException e){

                    }
                }
            });
        }else {
            final String[] items = formData.split(",");
            Arrays.stream(items).forEach(item ->{
                final String[] keyAndVal = item.split(":");
                if (keyAndVal.length == 2){
                    try {
                        final String key = URLDecoder.decode(keyAndVal[0],"utf8");
                        final String val = URLDecoder.decode( keyAndVal[1],"utf8");
                        result.put(key,val);
                    }catch (UnsupportedEncodingException e){

                    }
                }
            });
        }

        return result;
    }
}
