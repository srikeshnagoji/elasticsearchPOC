package com.techprimers.elastic.resource;

import com.techprimers.elastic.jparepository.UserJpaRepository;
import com.techprimers.elastic.model.Persons;
import com.techprimers.elastic.repository.UsersRepository;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.MatchQueryBuilder.Operator.AND;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;


@RestController
@RequestMapping("/rest/search")
public class SearchResource {

    @Autowired
    UsersRepository usersRepository;
    @Autowired
    UserJpaRepository usersJpaRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    Client client;

    @GetMapping(value = "/name/{text}")
    public List<Persons> searchName(@PathVariable final String text) {
        return usersRepository.findByName(text);
    }
    @GetMapping(value = "/namefromjpa/{text}")
    public List<Persons> searchNamefromJpa(@PathVariable final String text) {
        return usersJpaRepository.findByName(text);
    }

    @GetMapping(value = "/salary/{salary}")
    public List<Persons> searchSalary(@PathVariable final Long salary) {
        return usersRepository.findBySalary(salary);
    }


    @PostMapping("/persons")
    public ResponseEntity<Object> createPerson(@RequestBody Persons persons) {
        Persons savedPerson = usersJpaRepository.save(persons);
        usersRepository.save(savedPerson);//to elastic search db

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedPerson.getId()).toUri();

        return ResponseEntity.created(location).build();

    }
    @PutMapping("/persons/{id}")
    public ResponseEntity<Object> updatePerson(@RequestBody Persons persons, @PathVariable long id) {

        Persons studentOptional = usersJpaRepository.findById(id);

        if (studentOptional==null)
            return ResponseEntity.notFound().build();

        persons.setId(id);

        usersJpaRepository.save(persons);
        usersRepository.save(persons);//to elasticsearch

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/all")
    public List<Persons> searchAll() {
        List<Persons> personsList = new ArrayList<>();
        Iterable<Persons> userses = usersRepository.findAll();
        userses.forEach(personsList::add);
        return personsList;
    }

    @GetMapping(value = "/fuzzy/{fuzzyString}")//spell mistake
    public List<Persons> givenPhraseWithType_whenUseFuzziness_thenQueryMatches(@PathVariable final String fuzzyString) {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("name", fuzzyString).operator(AND).fuzziness(Fuzziness.TWO)
                        /*.prefixLength(3)*/).build();

        final List<Persons> userspage = elasticsearchTemplate.queryForList(searchQuery, Persons.class);
//        Page<Persons> userspage = usersRepository.search(searchQuery);
        return userspage;
    }

    @GetMapping(value = "/advsearch/{queryq}")//advanced search
    public List<Persons> givenPhraseWithType(@PathVariable final String queryq) {


        Iterable<Persons> userspage = usersRepository.search(queryStringQuery(queryq));
        List<Persons> list=itr2lis(userspage);
        return list;
    }


   /*//suggestor name
    @GetMapping(value = "/suggest/{str}")
    public List<String> suggestSome(@PathVariable final String str) {

//        SuggestBuilder.SuggestionBuilder completionSuggestionFuzzyBuilder = SuggestBuilders.fuzzyCompletionSuggestion("name");
//
//        final SuggestResponse suggestResponse = elasticsearchTemplate.suggest(completionSuggestionFuzzyBuilder, User.class);
//        CompletionSuggestion completionSuggestion = suggestResponse.getSuggest().getSuggestion("test-suggest");
//        List<CompletionSuggestion.Entry.Option> options = completionSuggestion.getEntries().get(0).getOptions();
//        return options;

        List<String> returnList = new ArrayList<>();

        String suggestionName = "suggestion";

        CompletionSuggestionBuilder completionSuggestionBuilder = new CompletionSuggestionBuilder(suggestionName);
        SuggestResponse suggestResponse = client.prepareSuggest("wow").setSuggestText(str).addSuggestion(completionSuggestionBuilder.field("name.suggest")).execute().actionGet();
        Suggest suggest = suggestResponse.getSuggest();
        Suggest.Suggestion suggestion = suggest.getSuggestion(suggestionName);

        List<Suggest.Suggestion.Entry> list = suggestion.getEntries();
        for(Suggest.Suggestion.Entry entry : list) {
            List<Suggest.Suggestion.Entry.Option> options = entry.getOptions();
            for(Suggest.Suggestion.Entry.Option option : options) {
                returnList.add(option.getText().toString());
            }
        }

        return returnList;

    }*/

    @GetMapping(value = "/jsonquery")
    public List<Persons> elasSearch(@RequestBody final String query){
        QueryBuilder queryBuilder=QueryBuilders.wrapperQuery(query);

        Iterable<Persons> userspage = usersRepository.search(queryBuilder);
        List<Persons> list=itr2lis(userspage);
        return list;
    }
    public List<Persons> itr2lis(Iterable<Persons> userspage){
        ArrayList<Persons> list = new ArrayList<Persons>();
        if (userspage != null) {
            for (Persons e : userspage) {
                list.add(e);
            }
        }
        return list;

    }

//exp
   /* public List<Persons> givenPhraseWithType_Fuzziness_thenQueryMatches(@PathVariable final String fuzzyString) {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("name", fuzzyString).operator(AND).fuzziness(Fuzziness.TWO)
                        *//*.prefixLength(3)*//*).build();

        final List<Persons> userspage = elasticsearchTemplate.queryForList(searchQuery, Persons.class);
//        Page<Persons> userspage = usersRepository.search(searchQuery);
        return userspage;
    }*/
}
