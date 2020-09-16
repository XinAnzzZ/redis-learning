package com.yuhangma.redis.learning.model;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Moore
 * @since 2020/09/14
 */
@Data
@Table
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String password;

}
