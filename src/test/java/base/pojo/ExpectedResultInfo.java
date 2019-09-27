package base.pojo;

/**
 * 预期结果与实际结果比较
 * @author Administrator
 *
 */
public class ExpectedResultInfo {
    // 要提取的实际结果
    private String actual;
    // 预期结果
    private Object expected;
    public String getActual() {
        return actual;
    }
    public void setActual(String actual) {
        this.actual = actual;
    }
    public Object getExpected() {
        return expected;
    }
    public void setExpected(Object expected) {
        this.expected = expected;
    }
    @Override
    public String toString() {
        return "ExpectedResultInfo [actual=" + actual + ", expected=" + expected + "]";
    }
    

}
