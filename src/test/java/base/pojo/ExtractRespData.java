package base.pojo;

//
public class ExtractRespData {

    // 通过jsonpath，用于提取响应体中需要的数据
    private String getData;
    // 参数名，保存到全局变量数据池中的key
    private String toGloableParame;
    public String getGetData() {
        return getData;
    }
    public void setGetData(String getData) {
        this.getData = getData;
    }
    public String getToGloableParame() {
        return toGloableParame;
    }
    public void setToGloableParame(String toGloableParame) {
        this.toGloableParame = toGloableParame;
    }
    @Override
    public String toString() {
        return "ExtractRespData [getData=" + getData + ", toGloableParame=" + toGloableParame + "]";
    }
    
    
}
