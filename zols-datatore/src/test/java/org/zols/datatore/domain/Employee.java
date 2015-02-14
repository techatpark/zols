package org.zols.datatore.domain;

import javax.persistence.Id;

public class Employee {

    @Id
    private String name;
    
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Employee{" + "name=" + name + ", age=" + age + '}';
    }
    
}
