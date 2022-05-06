package com.medjar.batch.productimport.batch.reader;

import com.medjar.batch.productimport.batch.util.CustomDelimitedLineTokenizer;
import com.medjar.batch.productimport.properties.BatchProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Created By Jarray Mohamed.
 * E-mail : jarraymohamed92@hotmail.fr
 *
 * @Date mai 06, 2022.
 */
@Slf4j
public abstract class AbstractReader<E> extends FlatFileItemReader<E> implements FieldSetMapper<E> {

    @Autowired
    private BatchProperties batchProperties;

    @PostConstruct
    private void initReader() {
        CustomDelimitedLineTokenizer delimitedLineTokenizer = new CustomDelimitedLineTokenizer();
        delimitedLineTokenizer.setIncludedFields(getOrderedIndexes());
        delimitedLineTokenizer.setNames(getOrderedColumnsNames());
        delimitedLineTokenizer.setDelimiter(batchProperties.getFiles().getColumnDelimiter());
        delimitedLineTokenizer.setStrict(false);

        DefaultLineMapper<E> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(delimitedLineTokenizer);
        lineMapper.setFieldSetMapper(this);

        setResource(new FileSystemResource(getFile()));
        setLineMapper(lineMapper);
    }

    protected abstract int[] getOrderedIndexes();

    protected abstract String[] getOrderedColumnsNames();

    protected abstract File getFile();

    @Override
    public E read() throws Exception, UnexpectedInputException, ParseException {
        return super.read();
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        super.open(executionContext);
    }
}
