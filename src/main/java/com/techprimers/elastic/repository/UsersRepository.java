package com.techprimers.elastic.repository;

import com.techprimers.elastic.model.Persons;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
//@RepositoryRestResource(collectionResourceRel = "persons", path = "persons")
public interface UsersRepository extends ElasticsearchRepository<Persons, Long> {
    List<Persons> findByName(String text);
    List<Persons> findBySalary(Long salary);
}
