package base.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数化工具类
 * @author Administrator
 */
public class ParameUtils {
    // 全局变量存放的数据池
    private static Map<String, Object> globalParameMap = new HashMap<String, Object>();
    
    
    
    public static void main(String[] args) {
        ParameUtils.addGlobalData("phone", "13888889999");
        ParameUtils.addGlobalData("pwd", "abc123456");
        ParameUtils.addGlobalData("reg_name", "张三");
        String reqStr = "{ \"mobile_phone\": \"${phone}\",\"pwd\": \"${pwd}\",'reg_name':'${reg_name}'}";
        String result = getReplacedParameter(reqStr);
        System.out.println(result);
    }
    /**
     * 正则，获值，替换，
     * @param reqStr 要替换的字符串
     * @return
     */
    public static String getReplacedParameter(String reqStr) {
        // 把${mobile_phone}提取出来--》提取出mobile_phone--
        // 正这表达式： \$\{.*?\}
        String regex = "\\$\\{(.*?)\\}";
        // 1.正则表达式创建一个模式(规则)对象
        Pattern compile = Pattern.compile(regex);
        // 2.对字符串进行匹配
        Matcher matcher = compile.matcher(reqStr);
        while (matcher.find()) {
            // 表示完全匹配的内容(分组：用括号)
            String totalStr = matcher.group(0);
            // 分组后的匹配内容（参数名）：括号里的部分
            String parameName = matcher.group(1);
            // System.out.println(group);
            // 从容器中找到对应的值
            Object parameValue = globalParameMap.get(parameName);
            // System.out.println(parame+"=="+parameValue);
            if (parameValue != null) {
                // 替换符合规则的文本
                reqStr = reqStr.replace(totalStr, parameValue.toString());
            }
        }
        return reqStr;
    }
    /**
     * 获取全局数据池的参数
     * @param parameName 参数key
     * @return
     */
    public static Object getGlobalData(String parameName) {
        return globalParameMap.get(parameName);

    }
    /**
     * 设置一个全局变量
     * @param parameName 参数key
     * @param parameValue 参数对应的值value
     */
    public static void addGlobalData(String parameName, Object parameValue) {
        globalParameMap.put(parameName, parameValue);
    }
}
