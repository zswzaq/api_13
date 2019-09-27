package base.pojo;

import java.util.List;

/**
 * 接口测试用例的详细信息
 * 
 * @author Administrator
 */
public class ApiCaseDetail extends Excel {
    private String caseId;
    private String apiId;
    private String requestData;// 请求体
    // 每条测试用例都对应有一个接口基本信息：接口基本信息是测试用例对象的一个属性
    private ApiInfo apiInfo;
    // 断言预期结果
    private String expectedResultInfo;
    private String extractRespData;
    // 写回excel的实际数据
    private String actualRespData;
    // 要验证的sqlCheck列表
    // 前置验证列表
    private List<SqlChecker> beforeCheckList;
    // 后置验证列表
    private List<SqlChecker> afterCheckList;

    public List<SqlChecker> getBeforeCheckList() {
        return beforeCheckList;
    }

    public void setBeforeCheckList(List<SqlChecker> beforeCheckList) {
        this.beforeCheckList = beforeCheckList;
    }

    public List<SqlChecker> getAfterCheckList() {
        return afterCheckList;
    }

    public void setAfterCheckList(List<SqlChecker> afterCheckList) {
        this.afterCheckList = afterCheckList;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public ApiInfo getApiInfo() {
        return apiInfo;
    }

    public void setApiInfo(ApiInfo apiInfo) {
        this.apiInfo = apiInfo;
    }

    public String getExpectedResultInfo() {
        return expectedResultInfo;
    }

    public void setExpectedResultInfo(String expectedResultInfo) {
        this.expectedResultInfo = expectedResultInfo;
    }

    public String getActualRespData() {
        return actualRespData;
    }

    public void setActualRespData(String actualRespData) {
        this.actualRespData = actualRespData;
    }

    public String getExtractRespData() {
        return extractRespData;
    }

    public void setExtractRespData(String extractRespData) {
        this.extractRespData = extractRespData;
    }

    @Override
    public String toString() {
        return "ApiCaseDetail [caseId=" + caseId + ", apiId=" + apiId + ", requestData=" + requestData + ", apiInfo="
                + apiInfo + ", expectedResultInfo=" + expectedResultInfo + ", extractRespData=" + extractRespData
                + ", actualRespData=" + actualRespData + ", beforeCheckList=" + beforeCheckList + ", afterCheckList="
                + afterCheckList + ", getRowNo()=" + getRowNo() + "]";
    }

}
