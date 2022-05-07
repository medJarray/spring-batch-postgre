package com.medjar.batch.productimport.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * Created By Jarray Mohamed.
 * E-mail : jarraymohamed92@hotmail.fr
 *
 * @Date mai 06, 2022.
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WriterDataSourceConfig {

    private static final String CLASSPATH_SCHEMA_SQL = "classpath:schema-all.sql";

    @Primary
    @Bean("writeDataSourceProperties")
    @ConfigurationProperties("mjarray.datasource-master")
    public DataSourceProperties writeDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("dataSource")
    public DataSource dataSource(@Qualifier("writeDataSourceProperties") DataSourceProperties writeDataSourceProperties) {
        log.info("Create datasource master {} ", writeDataSourceProperties.getUrl());
        writeDataSourceProperties.setSchema(Collections.singletonList("schema-all.sql"));
        return writeDataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean("writeJdbcTemplate")
    public JdbcTemplate writeJdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public BatchDataSourceScriptDatabaseInitializer batchDataSourceInitializer(@Qualifier("dataSource") DataSource dataSource,
                                                                               ResourceLoader resourceLoader) throws IOException {
        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
        BatchDataSourceScriptDatabaseInitializer batchDataSourceInitializer = new BatchDataSourceScriptDatabaseInitializer(dataSource, settings);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        Resource resource = resourceLoader.getResource(CLASSPATH_SCHEMA_SQL);
        try (InputStream inputStream = resource.getInputStream()) {
            String script = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            log.info("Run script [{}]", script);
        }

        populator.addScript(resource);
        populator.setContinueOnError(true);
        DatabasePopulatorUtils.execute(populator, dataSource);
        return batchDataSourceInitializer;
    }

}
