package com.medjar.batch.productimport.batch.reader;

import com.medjar.batch.productimport.enums.ImportBoolean;
import com.medjar.batch.productimport.model.OrderEntity;
import com.medjar.batch.productimport.properties.BatchProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.medjar.batch.productimport.constant.BatchImportConstant.JOB_PARAM_FILE;
import static com.medjar.batch.productimport.util.BatchUtils.booleanConvert;

/**
 * Created By Jarray Mohamed.
 * E-mail : jarraymohamed92@hotmail.fr
 *
 * @Date mai 06, 2022.
 */
@Slf4j
@StepScope //todo
@Component
public class OrderReader extends AbstractReader<OrderEntity> {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/MM/yyyy");
    @Autowired
    private BatchProperties batchProperties;

    @Value("#{jobExecutionContext[" + JOB_PARAM_FILE + "]}")
    private File orderTempFile;

    @Override
    protected int[] getOrderedIndexes() {
        return OrderEntity.getOrderedIndexes();
    }

    @Override
    protected String[] getOrderedColumnsNames() {
        return OrderEntity.getOrderedColumnsNames();
    }

    @Override
    protected File getFile() {
        InputStream initialStream;
        File targetFile = new File("/Users/med/Desktop/in-comming/orders_1.data");

        try {
            initialStream = new FileInputStream(
                    "/Users/med/Desktop/in-comming/orders.data");
            Files.copy(
                    initialStream,
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            IOUtils.closeQuietly(initialStream);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
        log.info("------------  target file name : {}  ---------------", targetFile.getName());
        return targetFile;
    }

    @Override
    public OrderEntity mapFieldSet(FieldSet fieldSet) {
        String orderId = fieldSet.readString("orderId");

        if (!StringUtils.hasText(orderId)) {
            String message = String.format("The columns order_id (%s) cannot be null or empty", orderId);
            log.error(message);
            throw new UnexpectedJobExecutionException(message);
        }


        log.info("---------------------- test ------------------");
        log.info("dateOrder : {}", fieldSet.readString("orderDate"));

        return OrderEntity.builder()
                .orderId(orderId)
                .orderDate(LocalDate.parse(fieldSet.readString("orderDate"), DATE_FORMATTER))
                .fullName(fieldSet.readString("fullName"))
                .amount(BigDecimal.valueOf(Double.parseDouble(fieldSet.readString("amount"))))
                .verified(booleanConvert(fieldSet, ImportBoolean.TRUE.getValue(), "verified"))
                .phoneNumber(fieldSet.readString("phoneNumber"))
                .email(fieldSet.readString("email"))
                .address(getAddress(fieldSet))
                .build();
    }

    private OrderEntity.Address getAddress(FieldSet fieldSet) {
        return OrderEntity.Address.builder()
                .region(fieldSet.readString("region"))
                .postalZip(fieldSet.readString("postalZip"))
                .country(fieldSet.readString("country"))
                .build();
    }
}
