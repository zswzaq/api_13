package base.utils;

import java.util.List;

import org.testng.Assert;

import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import base.pojo.ApiCaseDetail;
import base.pojo.ExpectedResultInfo;

/**
 * 断言工具类
 * 
 * @author Administrator
 *
 */
public class AssertTools {

    public static void assertResponse(ApiCaseDetail apiCaseDetail, String actualResult) {

        // 断言关键信息
        // 1.拿预期结果与实际结果
        String expectedResultInfo = apiCaseDetail.getExpectedResultInfo();
        // json解析为集合
        List<ExpectedResultInfo> resultInfos = JSONObject.parseArray(expectedResultInfo, ExpectedResultInfo.class);
        // 保护一下，如果为空，直接返回，不然会空指针
        if (resultInfos == null) {
            return;
        } else {
          //把实际响应结果解析成jsonpath对应的对象
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(actualResult);
            //遍历每个要断言的信息
            for (ExpectedResultInfo expectedRespKeyInfo : resultInfos) {
                //提取数据的实际值
                String jsonPath = expectedRespKeyInfo.getActual();
                //提取数据的期望值
                Object expected = expectedRespKeyInfo.getExpected();
                //通过jsonpath技术提取对应的实际结果
                Object actualData = JsonPath.read(document, jsonPath);
                //断言
                Assert.assertEquals(actualData, expected);
            }
        }

    }

    
    
    
    
    
}
