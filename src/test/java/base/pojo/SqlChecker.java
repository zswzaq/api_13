package base.pojo;

/**
 * 数据验证对象--sheet表单3
 * 
 * @author Administrator
 *
 */
public class SqlChecker extends Excel {

    private String sqlId;
    private String caseId;
    private String type;
    private String sql;
    private String expected;
    private String actual;
    private String checkResult;

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    @Override
    public String toString() {
        return "SqlChecker [sqlId=" + sqlId + ", caseId=" + caseId + ", type=" + type + ", sql=" + sql + ", expected="
                + expected + ", actual=" + actual + ", checkResult=" + checkResult + "]";
    }

}
