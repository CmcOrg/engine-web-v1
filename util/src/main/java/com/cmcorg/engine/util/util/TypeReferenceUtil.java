package com.cmcorg.engine.util.util;

import cn.hutool.core.lang.TypeReference;

import java.util.Set;

public interface TypeReferenceUtil {

    TypeReference<Set<String>> STRING_SET = new TypeReference<Set<String>>() {
    };

}
