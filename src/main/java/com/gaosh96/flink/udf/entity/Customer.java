package com.gaosh96.flink.udf.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author gaosh
 * @version 1.0
 * @since 2024/4/25
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Customer implements Serializable {

    private String uid;

    private Integer userAge;

    private String userLevel;

    private String address;

    private String label;

}