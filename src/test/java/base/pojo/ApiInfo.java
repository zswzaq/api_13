package base.pojo;

/**
 * api的基本信息
 * 
 * @author Administrator
 */
public class ApiInfo extends Excel {
    private String apiId;
    private String apiName;
    private String url;
    private String type;
    // headers
    private String headers;
    // 判断是否需要鉴权
    private String authCheck;

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getAuthCheck() {
        return authCheck;
    }

    public void setAuthCheck(String authCheck) {
        this.authCheck = authCheck;
    }

    @Override
    public String toString() {
        return "ApiInfo [apiId=" + apiId + ", apiName=" + apiName + ", url=" + url + ", type=" + type + ", headers="
                + headers + ", authCheck=" + authCheck + ", getRowNo()=" + getRowNo() + "]";
    }

}
