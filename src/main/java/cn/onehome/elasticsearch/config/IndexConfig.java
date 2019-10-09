package cn.onehome.elasticsearch.config;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: onehome
 * @version: 1.0
 * @date: 2019/10/8 18:47
 */
public class IndexConfig {

    private static final String SUFFIX = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

    private IndexConfig() {
    }

    public static String getSuffix() {
        return SUFFIX;
    }
}
