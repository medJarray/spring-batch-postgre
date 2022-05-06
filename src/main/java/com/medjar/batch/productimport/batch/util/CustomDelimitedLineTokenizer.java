package com.medjar.batch.productimport.batch.util;

import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

/**
 * Created By Jarray Mohamed.
 * E-mail : jarraymohamed92@hotmail.fr
 *
 * @Date mai 06, 2022.
 */
public class CustomDelimitedLineTokenizer extends DelimitedLineTokenizer {

    @Override
    protected boolean isQuoteCharacter(char c) {
        return false;
    }
}
