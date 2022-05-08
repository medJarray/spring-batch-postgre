package com.medjar.batch.productimport.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created By Jarray Mohamed.
 * E-mail : jarraymohamed92@hotmail.fr
 *
 * @Date mai 06, 2022.
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"orderId"})
public class OrderEntity {

    private String orderId;
    private LocalDate orderDate;
    private String fullName;
    private BigDecimal amount;
    private Boolean verified;
    private String phoneNumber;
    private String email;
    private Address address;


    public static int[] getOrderedIndexes() {
        return new int[]{
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        };
    }

    public static String[] getOrderedColumnsNames() {
        return new String[]{
                "orderId",
                "orderDate",
                "fullName",
                "amount",
                "verified",
                "phoneNumber",
                "email",
                "region",
                "postalZip",
                "country"
        };
    }


    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address {
        private String region;
        private String postalZip;
        private String country;

        public static String flatAddress() {
            return builder().region + " " + builder().postalZip + ", " + builder().country;
        }
    }
}
