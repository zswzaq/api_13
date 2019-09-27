package base.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import base.pojo.ApiCaseDetail;
import base.pojo.ApiInfo;
import base.pojo.ExtractRespData;
import base.pojo.SqlChecker;
import base.pojo.WriteDate;

/**
 * Api工具类
 * 
 * @author Administrator
 */
public class ApiTools {
    // 创建一个数据池,保存需要回写的数据
    private static List<WriteDate> writeDatesList = new ArrayList<WriteDate>();

    // 获取整个数据池容器的所有数据
    public static List<WriteDate> getWriteDatesList() {
        return writeDatesList;
    }

    // 把一条一条要写的数据放在数据池中
    public static void setWriteDatesList(WriteDate writeDate) {
        writeDatesList.add(writeDate);
    }

    // 创建一个数据池,保存需要回写的sql验证数据
    private static List<WriteDate> sqlDatesList = new ArrayList<WriteDate>();

    // 获取整个数据池容器的所有数据
    public static List<WriteDate> getSqlDatesList() {
        return sqlDatesList;
    }

    // 把一条一条要写的数据放在数据池中
    public static void setSqlDatesList(WriteDate writeDate) {
        sqlDatesList.add(writeDate);
    }

    // 数据提供者
    public static Object[][] getData() {
        // 获取用例的详细信息
        List<Object> apiCaseDetailList = ExcalTools.readExcal("/case/test_case_all.xlsx", 0, ApiCaseDetail.class);
        // 接口的基本信息
        List<Object> apiInfoList = ExcalTools.readExcal("/case/test_case_all.xlsx", 1, ApiInfo.class);
        // sql表单数据验证信息
        List<Object> sqlCheck = ExcalTools.readExcal("/case/test_case_all.xlsx", 2, SqlChecker.class);
        // 每条测试用例都对应一条基本信息,
        // 接口的基本信息是测试用例的一个属性
        // 把List的中间数据重新组装到map中去
        Map<String, ApiInfo> apiInfoMap = new HashMap<String, ApiInfo>();
        for (Object object : apiInfoList) {
            ApiInfo apiInfo = (ApiInfo) object;
            apiInfoMap.put(apiInfo.getApiId(), apiInfo);
        }
        // 准备一个容器，分别存放前置、后置的sql列表
        Map<String, List<SqlChecker>> sqlCheckMap = new LinkedHashMap<String, List<SqlChecker>>();
        // 遍历所有的数据验证列表
        for (Object sqlObject : sqlCheck) {
            SqlChecker sqlChecker = (SqlChecker) sqlObject;
            // String type = sqlChecker.getType();//bf、af
            // 拿着一条数据的caseId拼接上type，当作key
            String key = sqlChecker.getCaseId() + "_" + sqlChecker.getType();
            // 根据key，得到对应的一条sql验证数据对象
            List<SqlChecker> checkerList = sqlCheckMap.get(key);
            if (checkerList == null) {
                checkerList = new ArrayList<SqlChecker>();
            }
            checkerList.add(sqlChecker);// 添加到对应的容器
            sqlCheckMap.put(key, checkerList);
        }
        // 创建一个二维数组，做数据提供者
        Object[][] datas = new Object[apiCaseDetailList.size()][];
        for (int i = 0; i < apiCaseDetailList.size(); i++) {
            // 获取当前索引的api详情信息对象
            ApiCaseDetail apiCaseDetail = (ApiCaseDetail) apiCaseDetailList.get(i);
            // 获取api详情信息的apiId
            String apiId = apiCaseDetail.getApiId();
            // 获取到api详细信息对象,设置到用例详细对象中
            apiCaseDetail.setApiInfo(apiInfoMap.get(apiId));
            // 设置前置、后置的key
            String beforeKey = apiCaseDetail.getCaseId() + "_" + "bf";
            String afterKey = apiCaseDetail.getCaseId() + "_" + "af";
            // 放对应前置、后置的List中
            apiCaseDetail.setBeforeCheckList(sqlCheckMap.get(beforeKey));
            apiCaseDetail.setAfterCheckList(sqlCheckMap.get(afterKey));
            Object[] arr = { apiCaseDetailList.get(i) };
            datas[i] = arr;
        }
        return datas;
    }

    /**
     * 提取数据到全局变量数据池中
     * @param actualResult 实际结果
     * @param apiCaseDetail 测试用例对象（有测试用例所有的细节）
     */
    public static void extractSetGlobalData(String actualResult, ApiCaseDetail apiCaseDetail) {
        // 从Excel中提取数据的字符串
        String extractRespDataStr = apiCaseDetail.getExtractRespData();
        // 得到字符串的列表
        List<ExtractRespData> extractRespDataList = JSONObject.parseArray(extractRespDataStr, ExtractRespData.class);
        if (extractRespDataList == null) {
            return;
        } else {
            // 把实际响应结果解析成jsonpath对象
            Object parse = Configuration.defaultConfiguration().jsonProvider().parse(actualResult);
            for (ExtractRespData extractRespData : extractRespDataList) {
                // 提取对应数据的jsonpath
                String getData = extractRespData.getGetData();
                // 要放入数据池中的参数
                String parameName = extractRespData.getToGloableParame();
                // jsonpath 解析实际响应结果数据,提取
                Object parameValue = JsonPath.read(parse, getData);
                // 放入全局变量池中
                ParameUtils.addGlobalData(parameName, parameValue);
            }
        }
    }

}
