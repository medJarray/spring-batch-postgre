package com.medjar.batch.productimport.config;

import com.medjar.batch.productimport.properties.BatchProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Created By Jarray Mohamed.
 * E-mail : jarraymohamed92@hotmail.fr
 *
 * @Date mai 06, 2022.
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class ReadOnlyDataSourceConfig {

    private final BatchProperties batchProperties;


    @Bean("readOnlyDataSourceProperties")
    @ConfigurationProperties("mjarray.datasource-readonly")
    public DataSourceProperties readOnlyDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("readOnlyDataSource")
    public DataSource readOnlyDataSource(@Qualifier("readOnlyDataSourceProperties") DataSourceProperties readOnlyDataSourceProperties) {
        log.info("Create datasource read only  {} ", readOnlyDataSourceProperties.getUrl());
        return readOnlyDataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean("readOnlyJdbcTemplate")
    public JdbcTemplate readOnlyJdbcTemplate(@Qualifier("readOnlyDataSource") DataSource readOnlyDataSource) {
        return new JdbcTemplate(readOnlyDataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(@Qualifier("readOnlyDataSource") DataSource readOnlyDataSource) {
        return new NamedParameterJdbcTemplate(readOnlyDataSource);
    }

    @Bean
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("readOnlyDataSource") DataSource readOnlyDataSource) {
        return new DataSourceTransactionManager(readOnlyDataSource);
    }

}
