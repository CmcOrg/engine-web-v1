package com.cmcorg.engine.web.ip2region.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import org.lionsoul.ip2region.xdb.Searcher;

public class Ip2regionUtil {

    private static Searcher searcher;

    static {
        try {
            searcher = Searcher.newWithBuffer(IoUtil.readBytes(new ClassPathResource("ip2region.xdb").getStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过 ip获取 ip的区域
     * 返回格式：0|0|0|内网IP|内网IP
     */
    @SneakyThrows
    public static String getRegion(String ip) {
        if (StrUtil.isBlank(ip)) {
            return "";
        }
        try {
            return searcher.search(ip);
        } catch (Exception e) {
            e.printStackTrace();
            return "errorIp#" + ip;
        }
    }

}
