package base.pojo;

/**
 * 请求头信息封装
 * @author Administrator
 *
 */
public class Headers {

    private String name;
    private String value;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return "Headers [name=" + name + ", value=" + value + "]";
    }
    
}
