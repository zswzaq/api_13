package base.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;
import com.lemon.EncryptUtils;

import base.pojo.ApiCaseDetail;
import base.pojo.WriteDate;
import base.utils.ApiTools;
import base.utils.AssertTools;
import base.utils.ExcalTools;
import base.utils.HttpTools;

public class CaseTestAllBatch_test {
    @DataProvider
    public Object[][] getData() {
        return ApiTools.getData();
    }

    @Test(dataProvider = "getData")
    public void f1(ApiCaseDetail apiCaseDetail) {
        String actualResult = HttpTools.excute(apiCaseDetail);
        // 搜集要写入 的数据
        // 添加一条要写的数据
        ApiTools.setWriteDatesList(new WriteDate(apiCaseDetail.getRowNo(), 6, actualResult));
        // 将数据写回Excel：行号、列号、内容
        // WriteDate writeDate = new WriteDate(apiCaseDetail.getRowNo(), 4,
        // actualResult);
        // Excel原路径，文件写入目标路径，写入的数据（表单索引、行索引、列索引、内容）
        // ExcalTools.writeBack("/case/test_case_all.xlsx", "D:\\myGit\\a.xlsx",
        // 0, writeDate);
        // 开始做断言
        AssertTools.assertResponse(apiCaseDetail, actualResult);
        System.out.println(actualResult);
    }

    // 执行完所有的测试用例后，在全部写一次
    @AfterSuite
    public void afterSuite() {
        List<Integer> sheetList = new ArrayList<Integer>();
        sheetList.add(0);
        sheetList.add(2);
        ExcalTools.writeBackBatch2("/case/test_case_all.xlsx", "D:\\a.xlsx", sheetList);
    }

    // 3.timestamp+token+sign 时间戳+token+签名
    // 反对称加密：公钥（加密：客户端）；私钥（解密）
    public static void main(String[] args) {
        // md5加密，对称 不会变化的 e10adc3949ba59abbe56e057f20f883e
        //System.out.println(EncryptUtils.md5Encrypt("123456"));
        String result = EncryptUtils.rsaEncrypt("123456");
        // System.out.println(result);

        // 原请求体
        String reqStr = "{\"member_id\": 1,\"amount\": 0}";
        // 获取token --》请求头
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJtZW1iZXJfaWQiOjExLCJleHAiOjE1NjkwMzI4Mzl9.MbDsm9F0vnbIfsDKWn5ihWc1VbLwrpbFpsfLgKcGErYdQ3GShr2JN6K99kdmiCGenwfHXoE3Au8DJ_j4QYYKrg";
        // 获取时间戳
        Long timestamp = System.currentTimeMillis() / 1000;
        // 时间戳+token=sign --》fang入请求体中
        String tempStr = token.substring(0,50)+timestamp;
        String sign = EncryptUtils.rsaEncrypt(tempStr);
        System.out.println(sign);
        //将请求体转成map，设置入新加的两个请求头信息
        Map<String, Object> map = (Map<String, Object>) JSONObject.parse(reqStr);
        // 将新加的请求体put进去
        map.put("timestamp", timestamp);
        map.put("sign", sign);
        System.out.println(map.toString());
        // 再将map转成string类型的
        String finalReqStr = JSONObject.toJSONString(map);
        System.out.println(finalReqStr);
        
    }

}
