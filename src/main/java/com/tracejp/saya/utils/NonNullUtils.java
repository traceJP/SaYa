package com.tracejp.saya.utils;

import com.tracejp.saya.exception.RequestParamsException;

/**
 * <p></p>
 *
 * @author traceJP
 * @since 2021/4/13 16:29
 */
public class NonNullUtils {

    /**
     * 如果为空则抛出异常
     * @param obj
     */
    public static void notNull(Object... obj) {
        for (Object o : obj) {
            if (o == null) {
                throw new RequestParamsException();
            }
        }
    }

    public static void notNull(Object obj) {
        notNull(obj, null);
    }

}
