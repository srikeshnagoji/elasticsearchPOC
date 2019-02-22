package com.techprimers.elastic.jparepository;

import com.techprimers.elastic.model.Persons;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserJpaRepository extends JpaRepository<Persons, Long> {
    Persons findById(Long id);
    List<Persons> findByName(String name);
}
