package com.cmcorg.engine.web.util.util;

import cn.hutool.core.collection.CollUtil;

/**
 * 连接符，工具类
 */
public class SeparatorUtil {

    // 竖线分隔符
    public final static String VERTICAL_LINE_SEPARATOR = "|";

    /**
     * 返回：被【竖线分隔符】，包裹的字符串
     */
    public static String verticalLine(Object object) {
        return VERTICAL_LINE_SEPARATOR + object + VERTICAL_LINE_SEPARATOR;
    }

    /**
     * @return 示例：|1||2||3|
     */
    public static <T> String verticalLine(Iterable<T> iterable) {
        return CollUtil.join(iterable, "", VERTICAL_LINE_SEPARATOR, VERTICAL_LINE_SEPARATOR);
    }

}
