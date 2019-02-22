package com.techprimers.elastic.load;

import com.techprimers.elastic.jparepository.UserJpaRepository;
import com.techprimers.elastic.model.Persons;
import com.techprimers.elastic.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class Loaders {

    @Autowired
    ElasticsearchOperations operations;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UserJpaRepository userJpaRepository;

    @PostConstruct
    @Transactional
    public void loadAll(){

        operations.putMapping(Persons.class);
        System.out.println("Loading Data");
        List<Persons> data = getData();
//        //userJpaRepository.deleteAll();//remove this
//        userJpaRepository.save(data); //saves to H2 DB

        List<Persons> personsList = userJpaRepository.findAll(); //Get from H2 DB
        usersRepository.deleteAll();//remove this also
        usersRepository.save(personsList); //loads into Elastic
        System.out.printf("Loading Completed");
    }

    private List<Persons> getData() {
        List<Persons> persons = new ArrayList<>();
        persons.add(new Persons("Ajay",123L, "Accounting", 12000L));
        persons.add(new Persons("Jaga",1234L, "Finance", 22000L));
        persons.add(new Persons("Thiru",1235L, "Accounting", 12000L));
        persons.add(new Persons("Near",1235L, "Accounting", 12000L));
        persons.add(new Persons("Dear",1235L, "Accounting", 12000L));
        persons.add(new Persons("Thiruvanth",1236L, "Accounting", 12000L));
        persons.add(new Persons("Wright",1237L, "Accounting", 12000L));
        persons.add(new Persons("write",1238L, "Accounting", 12000L));
        persons.add(new Persons("right",1239L, "Accounting", 12000L));
        persons.add(new Persons("writer",1241L, "Accounting", 12000L));
        persons.add(new Persons("thirush",1245L, "Accounting", 12000L));
        persons.add(new Persons("Spurthi",1287L, "Accounting", 12000L));
        persons.add(new Persons("spurthir",1289L, "Accounting", 12000L));
        return persons;
    }
}
