package com.medjar.batch.productimport.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created By Jarray Mohamed.
 * E-mail : jarraymohamed92@hotmail.fr
 *
 * @Date mai 06, 2022.
 */
@Getter
@Setter
@Component
@ConfigurationProperties("mjarray.batch.config")
public class BatchProperties {

    private ImportFiles files;
    private TaskExecutorConfig step;
    private List<String> orderStatus;
    private TaskExecutorConfig partitioner;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class TaskExecutorConfig {
        /**
         * The limit of threads created for multithreading steps.
         */
        private int maxPoolSize;
        /**
         * The commit interval.
         */
        private int chunkSize;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class ImportFiles {
        /**
         * The delimiter char used for SDE files.
         */
        private String columnDelimiter;

        // The import file prefix
        private String orderFilePrefix;
        private String productFilePrefix;
        private String saleFilePrefix;
    }
}
