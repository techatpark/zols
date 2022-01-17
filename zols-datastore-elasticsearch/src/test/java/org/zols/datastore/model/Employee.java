/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.model;

import org.zols.jsonschema.bind.annotation.Localized;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity(name = "employee")
public class Employee {

    @Id
    private String name;

    @NotNull
    @Localized
    private String city;

    private Boolean isContractor;
    private int salary;

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Boolean getIsContractor() {
        return isContractor;
    }

    public void setIsContractor(final Boolean isContractor) {
        this.isContractor = isContractor;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(final int salary) {
        this.salary = salary;
    }

}
