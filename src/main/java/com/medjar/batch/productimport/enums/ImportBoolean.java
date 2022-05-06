package com.medjar.batch.productimport.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

/**
 * Created By Jarray Mohamed.
 * E-mail : jarraymohamed92@hotmail.fr
 *
 * @Date mai 06, 2022.
 */
@AllArgsConstructor
public enum ImportBoolean {

    TRUE("Y"),
    FALSE("N");
    private final String value;

    public static ImportBoolean fromBoolean(Boolean bool) {
        if (bool == null) {
            return null;
        }
        return bool ? TRUE : FALSE;
    }


    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ImportBoolean ofValue(String value) {

        for (ImportBoolean cmdmBoolean : ImportBoolean.values()) {
            if (cmdmBoolean.getValue().equals(value)) {
                return cmdmBoolean;
            }
        }
        return null;
    }
}
