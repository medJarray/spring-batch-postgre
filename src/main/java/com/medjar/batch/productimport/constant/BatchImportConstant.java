package com.medjar.batch.productimport.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BatchImportConstant {

    public static final String JOB_PARAM_FILE = "file";

    public static final String DECIDER_CONTINUE_STATUS = "CONTINUE";
    public static final String DECIDER_SKIP_STATUS = "SKIP";

    public static final String SAVE_ORDERS_FLOW_PREFIX = "saveOrdersFlow";

    public static final String JOB_PARAM_ORDER_TEMP_FILE = "ordersTempFile";
    public static final String JOB_PARAM_PRODUCT_TEMP_FILE = "productsTempFile";
    public static final String JOB_PARAM_SALE_TEMP_FILE = "salesTempFile";

}
