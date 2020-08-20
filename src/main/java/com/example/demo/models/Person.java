package com.example.demo.models;

public class Person {
    private long id;
    private long orgId;
    private String name;
    private int salary;

    public Person() {

    }

    public Person(long id, long orgId, String name, int salary) {
        this.id = id;
        this.orgId = orgId;
        this.name = name;
        this.salary = salary;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}
