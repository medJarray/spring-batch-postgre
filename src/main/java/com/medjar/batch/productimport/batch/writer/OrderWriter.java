package com.medjar.batch.productimport.batch.writer;

import com.medjar.batch.productimport.model.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created By Jarray Mohamed.
 * E-mail : jarraymohamed92@hotmail.fr
 *
 * @Date mai 06, 2022.
 */
@Slf4j
@Component
public class OrderWriter extends AbstractWriter<OrderEntity> {

    @Override
    protected String getSql() {
        return "INSERT INTO temp_orders(order_id, order_date, client_full_name, amount, verified, phone_number, email, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    public void setValues(OrderEntity item, PreparedStatement ps) throws SQLException {
        log.info("setValue : {}", item.toString());
        ps.setString(1, item.getOrderId());
        ps.setString(2, item.getOrderDate().toString());
        ps.setString(3, item.getFullName());
        ps.setString(4, item.getAmount().toString());
        ps.setString(5, item.getVerified().toString());
        ps.setString(6, item.getPhoneNumber());
        ps.setString(7, item.getEmail());
        ps.setString(8, OrderEntity.Address.flatAddress());
    }
}
