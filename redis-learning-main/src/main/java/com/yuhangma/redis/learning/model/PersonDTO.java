package com.yuhangma.redis.learning.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Random;

/**
 * @author Moore
 * @since 2020/08/29
 */
@Data
@Accessors(chain = true)
public class PersonDTO {

    private String name;

    private Integer age;

    public static PersonDTO newPerson() {
        return new PersonDTO().setName("张三")
                .setAge(20);
    }

    public static PersonDTO newRandomPerson() {
        Random random = new Random();
        int r = random.nextInt(100);
        return new PersonDTO().setName("张三")
                .setAge(r);
    }
}
