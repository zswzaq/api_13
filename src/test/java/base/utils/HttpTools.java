package base.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.lemon.EncryptUtils;

import base.pojo.ApiCaseDetail;
import base.pojo.Headers;

public class HttpTools {
    static  Logger  log = Logger.getLogger(LogTest.class);
    /**
     * get请求
     * @param url 请求地址
     * @param map 请求参数 form表单
     * @return
     * @throws Exception
     */
    public static String excute(ApiCaseDetail apiCaseDetail) {
        log.info("开始发包");
        // 处理请求体数据：普通参数的替换、鉴权的参数的添加
        handle_RequestData(apiCaseDetail);
        // 判断请求类型，请求分发
        String type = apiCaseDetail.getApiInfo().getType();
        String result = null;
        if ("GET".equalsIgnoreCase(type)) {
            // url 和数据是api详情里有的，所以直接可以传一个apiCase详情对象
            result = HttpTools.doGet(apiCaseDetail);
        } else if ("POST".equalsIgnoreCase(type)) {
            result = HttpTools.doPost(apiCaseDetail);
        } else if ("PATCH".equalsIgnoreCase(type)) {
            result = HttpTools.doPatch(apiCaseDetail);
        } else if ("DELETE".equalsIgnoreCase(type)) {
            result = HttpTools.doDelete(apiCaseDetail);
        }
        log.info("请求结果"+result);
        return result;
    }
    
    private static void handle_RequestData(ApiCaseDetail apiCaseDetail) {
        // 拿到原请求体，
        String requestData = apiCaseDetail.getRequestData();
        // 替换请求体中的数据：member_Id等数据
        String replacedRequestData = ParameUtils.getReplacedParameter(requestData);
        // 拿到authCheck，是否需要鉴权
        String auth = apiCaseDetail.getApiInfo().getAuthCheck();
        if (auth != null && "T".equalsIgnoreCase(auth)) {
            // 获取token： 从数据池中获取
            String token = ParameUtils.getGlobalData("token").toString();
            // 获取时间戳
            Long timestamp = System.currentTimeMillis() / 1000;
            // 时间戳+token=sign --》put入请求体中
            String tempStr = token.substring(0, 50) + timestamp;
            String sign = EncryptUtils.rsaEncrypt(tempStr);
            // 将请求体转成map，设置入新加的两个请求头信息
            Map<String, Object> reqMap = (Map<String, Object>) JSONObject.parse(replacedRequestData);
            // 将新加的请求体put进去
            reqMap.put("timestamp", timestamp);
            reqMap.put("sign", sign);
            System.out.println(reqMap.toString());
            // 再将map转成string类型的,即为最终的请求体
            replacedRequestData = JSONObject.toJSONString(reqMap);
        }
        // 重新set回请求体去
        apiCaseDetail.setRequestData(replacedRequestData);
    }

    public static String doGet(String url, Map<String, String> map) {
        try {
            // 容器
            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            Set<String> keySet = map.keySet();
            for (String string : keySet) {// 遍历一次，生成一对名值对
                String name = string;
                String value = map.get(name);
                BasicNameValuePair nameValuePair = new BasicNameValuePair(name, value);
                // 放名值对（键值对）
                parameters.add(nameValuePair);
            }
            // 格式化参数，变成 ：n=v&n=v&n=v...
            String params = URLEncodedUtils.format(parameters, "utf-8");
            // 创建一个http 的get请求
            HttpGet get = new HttpGet(url + "?" + params);
            // 创建一个发包客户端
            CloseableHttpClient createDefault = HttpClients.createDefault();
            // 发包,得到http响应
            CloseableHttpResponse response = createDefault.execute(get);
            // 3.获取响应体
            HttpEntity entity = response.getEntity();
            String string = EntityUtils.toString(entity);// 工具包toString，将响应体转化为字符串
            return string;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * post请求
     * @param url 请求地址
     * @param map 请求参数对 ：form表单格式
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, String> map) {
        try {
            HttpPost post = new HttpPost(url);
            // 容器
            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            Set<String> keySet = map.keySet();
            for (String string : keySet) {// 遍历一次，生成一对名值对
                String name = string;
                String value = map.get(name);
                BasicNameValuePair nameValuePair = new BasicNameValuePair(name, value);
                // 放名值对（键值对）
                parameters.add(nameValuePair);
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "utf-8");// 设置字符集（可以不设置）
            post.setEntity(formEntity);
            // 创建一个发包客户端
            CloseableHttpClient createDefault = HttpClients.createDefault();
            // 发包,得到http响应
            CloseableHttpResponse response = createDefault.execute(post);
            // 3.获取响应体
            HttpEntity entity = response.getEntity();
            String string = EntityUtils.toString(entity);// 工具包toString，将响应体转化为字符串
            return string;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * doPost方法
     * @param url  请求url
     * @param requestData json请求体
     * @return
     */
    public static String doPost(String url, String requestData) {
        try {
            HttpPost post = new HttpPost(url);
            // 设值请求头的必填项
            post.setHeader("X-Lemonban-Media-Type", "lemonban.v1");
            // 创建一个json格式的请求体
            StringEntity entity = new StringEntity(requestData, ContentType.APPLICATION_JSON);
            // 设值请求体
            post.setEntity(entity);
            // 设值字符集
            entity.setContentEncoding("utf-8");
            // 创建一个发包客户端
            CloseableHttpClient createDefault = HttpClients.createDefault();
            // 发包,得到http响应
            CloseableHttpResponse response = createDefault.execute(post);
            // 3.获取响应体
            HttpEntity reEntity = response.getEntity();
            String string = EntityUtils.toString(reEntity);// 工具包toString，将响应体转化为字符串
            return string;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * doPost方法
     * @param apiCaseDetail 参数：用例详情对象
     * @return
     */
    public static String doPost(ApiCaseDetail apiCaseDetail) {
        try {
            HttpPost post = new HttpPost(apiCaseDetail.getApiInfo().getUrl());
            // 拿到所有的headers
            String headerStr = apiCaseDetail.getApiInfo().getHeaders();
            List<Headers> headers = JSONObject.parseArray(headerStr, Headers.class);
            // 设值请求头的必填项
            // post.setHeader("X-Lemonban-Media-Type", "lemonban.v1");
            for (Headers header : headers) {
                // 从数据池拿出来的请求头token，替换请求头中的${token},
                String replacedHeader = ParameUtils.getReplacedParameter(header.getValue());
                post.setHeader(header.getName(), replacedHeader);
            }
            // 创建一个json格式的请求体
            StringEntity entity = new StringEntity(apiCaseDetail.getRequestData(), ContentType.APPLICATION_JSON);
            // 设值请求体
            post.setEntity(entity);
            // 设值字符集
            entity.setContentEncoding("utf-8");
            // 创建一个发包客户端
            CloseableHttpClient createDefault = HttpClients.createDefault();
            // 发包,得到http响应
            CloseableHttpResponse response = createDefault.execute(post);
            // 3.获取响应体
            HttpEntity reEntity = response.getEntity();
            String string = EntityUtils.toString(reEntity);// 工具包toString，将响应体转化为字符串
            return string;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * doGet方法
     * @param url 请求url
     * @param requestData 请求参数json格式
     * @return
     */
    public static String doGet(String url, String requestData) {
        try {
            // 容器
            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            // 把json对象转成map对象
            Map<String, Object> map = JSONObject.parseObject(requestData);
            Set<String> keySet = map.keySet();
            for (String key : keySet) {// 遍历一次，生成一对名值对
                String name = key;
                String value = (String) map.get(name);
                BasicNameValuePair nameValuePair = new BasicNameValuePair(name, value);
                // 放名值对（键值对）
                parameters.add(nameValuePair);
            }
            // 格式化参数，变成 ：n=v&n=v&n=v...
            String params = URLEncodedUtils.format(parameters, "utf-8");
            // 创建一个http 的get请求
            HttpGet get = new HttpGet(url + "?" + params);
            //get.setHeader("X-Lemonban-Media-Type", "lemonban.v1");
            // 创建一个发包客户端
            CloseableHttpClient createDefault = HttpClients.createDefault();
            // 发包,得到http响应
            CloseableHttpResponse response = createDefault.execute(get);
            // 3.获取响应体
            HttpEntity entity = response.getEntity();
            String string = EntityUtils.toString(entity);// 工具包toString，将响应体转化为字符串
            return string;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * dotGet方法
     * @param apiCaseDetail  参数：用例详情对象
     * @return
     */
    public static String doGet(ApiCaseDetail apiCaseDetail) {
        try {
            // 容器
            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            // 把json对象转成map对象
            Map<String, Object> map = JSONObject.parseObject(apiCaseDetail.getRequestData());
            Set<String> keySet = map.keySet();
            for (String key : keySet) {// 遍历一次，生成一对名值对
                String name = key;
                String value = (String) map.get(name);
                BasicNameValuePair nameValuePair = new BasicNameValuePair(name, value);
                // 放名值对（键值对）
                parameters.add(nameValuePair);
            }
            // 格式化参数，变成 ：n=v&n=v&n=v...
            String params = URLEncodedUtils.format(parameters, "utf-8");
            // 创建一个http 的get请求
            HttpGet get = new HttpGet(apiCaseDetail.getApiInfo().getUrl() + "?" + params);
            // 拿到所有的headers
            String headerStr = apiCaseDetail.getApiInfo().getHeaders();
            List<Headers> headers = JSONObject.parseArray(headerStr, Headers.class);
            // 设值请求头的必填项
            for (Headers header : headers) {
                get.setHeader(header.getName(), header.getValue());
            }
            // get.setHeader("X-Lemonban-Media-Type", "lemonban.v1");
            // 创建一个发包客户端
            CloseableHttpClient createDefault = HttpClients.createDefault();
            // 发包,得到http响应
            CloseableHttpResponse response = createDefault.execute(get);
            // 3.获取响应体
            HttpEntity entity = response.getEntity();
            String string = EntityUtils.toString(entity);// 工具包toString，将响应体转化为字符串
            return string;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doPatch(ApiCaseDetail apiCaseDetail) {
        try {
            HttpPatch httpPatch = new HttpPatch(apiCaseDetail.getApiInfo().getUrl());
            // 拿到所有的headers
            String headerStr = apiCaseDetail.getApiInfo().getHeaders();
            List<Headers> headers = JSONObject.parseArray(headerStr, Headers.class);
            // 设值请求头的必填项
            // post.setHeader("X-Lemonban-Media-Type", "lemonban.v1");
            for (Headers header : headers) {
                httpPatch.setHeader(header.getName(), header.getValue());
            }
            // 创建一个json格式的请求体
            StringEntity entity = new StringEntity(apiCaseDetail.getRequestData(), ContentType.APPLICATION_JSON);
            // 设值请求体
            httpPatch.setEntity(entity);
            // 设值字符集
            entity.setContentEncoding("utf-8");
            // 创建一个发包客户端
            CloseableHttpClient createDefault = HttpClients.createDefault();
            // 发包,得到http响应
            CloseableHttpResponse response = createDefault.execute(httpPatch);
            // 3.获取响应体
            HttpEntity reEntity = response.getEntity();
            String string = EntityUtils.toString(reEntity);// 工具包toString，将响应体转化为字符串
            return string;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String doDelete(ApiCaseDetail apiCaseDetail) {
        try {
            HttpDelete delete = new HttpDelete(apiCaseDetail.getApiInfo().getUrl());
            // 拿到所有的headers
            String headerStr = apiCaseDetail.getApiInfo().getHeaders();
            List<Headers> headers = JSONObject.parseArray(headerStr, Headers.class);
            // 设值请求头的必填项
            // post.setHeader("X-Lemonban-Media-Type", "lemonban.v1");
            for (Headers header : headers) {
                delete.setHeader(header.getName(), header.getValue());
            }
            // 创建一个json格式的请求体
            StringEntity entity = new StringEntity(apiCaseDetail.getRequestData(), ContentType.APPLICATION_JSON);
            // 设值请求体
            // FIXME
            // delete.setEntity(entity);
            // 设值字符集
            entity.setContentEncoding("utf-8");
            // 创建一个发包客户端
            CloseableHttpClient createDefault = HttpClients.createDefault();
            // 发包,得到http响应
            CloseableHttpResponse response = createDefault.execute(delete);
            // 3.获取响应体
            HttpEntity reEntity = response.getEntity();
            String string = EntityUtils.toString(reEntity);// 工具包toString，将响应体转化为字符串
            return string;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
