package base.test;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import base.pojo.ApiCaseDetail;
import base.pojo.WriteDate;
import base.utils.ApiTools;
import base.utils.AssertTools;
import base.utils.ExcalTools;
import base.utils.HttpTools;
import base.utils.ParameUtils;
import base.utils.SqlCheckTools;

public class CaseTestAllBatch {
    @BeforeSuite
    public void beforeSuite() {
        ParameUtils.addGlobalData("phone", "13344445555");
        // ParameUtils.addGlobalData("pwd", "abc123456");
    }

    @DataProvider
    public Object[][] getData() {
        return ApiTools.getData();
    }

    @Test(dataProvider = "getData")
    public void f1(ApiCaseDetail apiCaseDetail) {
        // 1.数据库的前置验证
        // List<SqlChecker> checkList = apiCaseDetail.getCheckList();// 不好
        SqlCheckTools.beforeCheck(apiCaseDetail);
        String actualResult = HttpTools.excute(apiCaseDetail);
        // 搜集要写入 的数据,将数据写回Excel：行号、列号、内容
        WriteDate writeDate = new WriteDate(apiCaseDetail.getRowNo(), 6, actualResult);
        // 收集要回写的数据
        ApiTools.setWriteDatesList(writeDate);
        // 提取数据到全局变量数据池中
        ApiTools.extractSetGlobalData(actualResult,apiCaseDetail);
        // 2.数据库的后置验证
        SqlCheckTools.afterCheck(apiCaseDetail);
        System.out.println(actualResult);
        // 开始做断言
        AssertTools.assertResponse(apiCaseDetail, actualResult);
    }

    // 执行完所有的测试用例后，在全部写一次
    @AfterSuite
    public void afterSuite() {
        ExcalTools.writeBackBatch("/case/test_case_all.xlsx", "D:\\a.xlsx", 0, 2);
        // ExcalTools.writeBackBatch2("/case/test_case_all.xlsx", "D:\\a.xlsx",
        // 2);
    }
}
