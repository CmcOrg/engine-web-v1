package com.cmcorg.engine.auth.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg.engine.auth.model.entity.BaseEntityTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Map;

public class MyEntityUtil {

    /**
     * 获取不为 null对象的 字符串
     */
    @NotNull
    public static String getNotNullStr(String str) {
        return getNotNullStr(str, "");
    }

    /**
     * 如果为空，则返回默认值
     */
    @NotNull
    public static String getNotNullStr(String str, String defaultStr) {
        return StrUtil.isBlank(str) ? defaultStr : str;
    }

    /**
     * 获取不为 null对象的 ParentId字符串
     */
    @NotNull
    public static Long getNotNullParentId(Long aLong) {
        return aLong == null ? 0L : aLong;
    }

    /**
     * 如果 parentId为 0，则设置为 null
     */
    public static void handleParentId(BaseEntityTree<?> baseEntityTree) {
        if (baseEntityTree.getParentId() == 0L) {
            baseEntityTree.setParentId(null);
        }
    }

    /**
     * 获取不为 null对象的 BigDecimal
     */
    @NotNull
    public static BigDecimal getNotNullBigDecimal(BigDecimal bigDecimal) {
        return bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
    }

    /**
     * 获取不为 null对象的 Integer
     */
    @NotNull
    public static Integer getNotNullInt(Integer integer) {
        return integer == null ? -1 : integer;
    }

    /**
     * 获取不为 null对象的 OrderNo
     */
    @NotNull
    public static Integer getNotNullOrderNo(Integer integer) {
        return integer == null ? 0 : integer;
    }

    /**
     * 获取不为 null对象的 Long
     */
    @NotNull
    public static Long getNotNullLong(Long aLong) {
        return aLong == null ? -1L : aLong;
    }

    /**
     * number为 -1的，设置为 null
     */
    @Nullable
    public static <T> T removeDefault(T t) {
        if (t == null) {
            return null;
        }

        Map<String, Object> map = BeanUtil.beanToMap(t);

        for (Map.Entry<String, Object> item : map.entrySet()) {
            if (item.getValue() == null) {
                continue;
            }
            if ((item.getValue() instanceof Integer) && ((int)item.getValue() == -1)) {
                item.setValue(null);
                continue;
            }
            if ((item.getValue() instanceof Byte) && ((byte)item.getValue() == -1)) {
                item.setValue(null);
                continue;
            }
            if ((item.getValue() instanceof Long) && ((long)item.getValue() == -1)) {
                item.setValue(null);
            }
        }

        return BeanUtil.toBean(map, (Class<T>)t.getClass());
    }

}
