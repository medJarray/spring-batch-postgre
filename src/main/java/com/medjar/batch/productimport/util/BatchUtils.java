package com.medjar.batch.productimport.util;

import lombok.experimental.UtilityClass;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.util.StringUtils;

@UtilityClass
public class BatchUtils {

    public static Boolean booleanConvert(FieldSet fieldSet, String value, String key) {
        String readString = fieldSet.readString(key);
        if (!StringUtils.hasText(readString)) {
            return null;
        }
        return value.equals(readString);
    }
}
