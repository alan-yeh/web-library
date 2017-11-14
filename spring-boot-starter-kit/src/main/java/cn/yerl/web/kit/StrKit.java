package cn.yerl.web.kit;

import java.util.Map;
import java.util.Random;

/**
 * Created by alan on 2017/3/13.
 */
public class StrKit {
    /**
     * 为字符串加前缀，如果目已经有该前缀了，则直接返回
     * @param target 目标字符串
     * @param prefix 前缀
     * @return
     */
    public static final String prefix(String target, String prefix){
        if (target.startsWith(prefix)){
            return target;
        }else {
            return prefix + target;
        }
    }
    /**
     * 为字符串加后缀，如果目已经有该后缀了，则直接返回
     * @param target 目标字符串
     * @param suffix 后缀
     * @return
     */
    public static final String suffix(String target, String suffix){
        if (target.endsWith(suffix)){
            return target;
        }else {
            return target + suffix;
        }
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklnmopqrstuvwxyz0123456789ABCDEFGHIJKLNMOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 字符串为 null 或者为  "" 时返回 true
     */
    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * 字符串不为 null 而且不为  "" 时返回 true
     */
    public static boolean notBlank(String str) {
        return str != null && !"".equals(str.trim());
    }

    /**
     * 格式化字符串
     * Example:
     * StrKit.format("This is a ? string", "formatted");
     */
    public static String format(String format, Object... arg){
        StringBuilder result = new StringBuilder();

        String[] segments = (format + " ").split("[?]");
        for (int i = 0; i < segments.length; i ++){
            result.append(segments[i]);

            if (i != segments.length - 1){
                if (arg.length > i){
                    result.append(arg[i] == null ? "(null)" : arg[i].toString());
                }else {
                    result.append("?");
                }
            }
        }
        return result.substring(0, result.length() - 1);

//        for (int i = 0; i < arg.length; i++) {
//            if (arg[i] != null){
//                format = format.replaceFirst("[?]", arg[i].toString());
//            }else {
//                format = format.replaceFirst("[?]", "");
//            }
//
//        }
//        return format;
    }

    public static String format(String format, Map<String, Object> arg){
        for (Map.Entry<String, Object> entry : arg.entrySet()){
            format = format.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }
        return format;
    }

    public static String defaultValue(String str, String defaultValue){
        return notBlank(str) ? str : defaultValue;
    }
}
