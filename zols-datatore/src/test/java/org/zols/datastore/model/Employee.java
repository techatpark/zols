/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.model;

import javax.persistence.Id;

public class Employee {

    @Id
    private String name;
    private Boolean isContractor;
    private int salary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsContractor() {
        return isContractor;
    }

    public void setIsContractor(Boolean isContractor) {
        this.isContractor = isContractor;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

}
