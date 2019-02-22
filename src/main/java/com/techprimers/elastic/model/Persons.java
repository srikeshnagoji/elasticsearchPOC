package com.techprimers.elastic.model;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Document(indexName = "persons", type = "persons", shards = 1, refreshInterval = "2s")
public class Persons {

    private String name;
    @Id
    @GeneratedValue
    private Long id;
    private String teamName;
    private Long salary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    public Persons(String name, Long id, String teamName, Long salary) {

        this.name = name;
        this.id = id;
        this.teamName = teamName;
        this.salary = salary;
    }

    public Persons() {

    }
}