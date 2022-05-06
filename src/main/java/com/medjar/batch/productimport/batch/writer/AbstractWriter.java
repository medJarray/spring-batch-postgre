package com.medjar.batch.productimport.batch.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Created By Jarray Mohamed.
 * E-mail : jarraymohamed92@hotmail.fr
 *
 * @Date mai 06, 2022.
 */
@Slf4j
public abstract class AbstractWriter<E> extends JdbcBatchItemWriter<E> implements ItemPreparedStatementSetter<E> {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void initWriter() {
        setDataSource(dataSource);
        setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        setSql(getSql());
        setItemPreparedStatementSetter(this);
    }

    protected abstract String getSql();
}
