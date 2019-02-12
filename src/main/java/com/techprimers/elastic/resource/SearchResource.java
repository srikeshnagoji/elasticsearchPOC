package com.techprimers.elastic.resource;

import com.techprimers.elastic.model.Users;
import com.techprimers.elastic.repository.UsersRepository;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.index.query.MatchQueryBuilder.Operator.AND;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;


@RestController
@RequestMapping("/rest/search")
public class SearchResource {

    @Autowired
    UsersRepository usersRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    Client client;

    @GetMapping(value = "/name/{text}")
    public List<Users> searchName(@PathVariable final String text) {
        return usersRepository.findByName(text);
    }


    @GetMapping(value = "/salary/{salary}")
    public List<Users> searchSalary(@PathVariable final Long salary) {
        return usersRepository.findBySalary(salary);
    }


    @GetMapping(value = "/all")
    public List<Users> searchAll() {
        List<Users> usersList = new ArrayList<>();
        Iterable<Users> userses = usersRepository.findAll();
        userses.forEach(usersList::add);
        return usersList;
    }

    @GetMapping(value = "/fuzzy/{fuzzyString}")//spell mistake
    public List<Users> givenPhraseWithType_whenUseFuzziness_thenQueryMatches(@PathVariable final String fuzzyString) {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("name", fuzzyString).operator(AND).fuzziness(Fuzziness.TWO)
                        /*.prefixLength(3)*/).build();

        final List<Users> userspage = elasticsearchTemplate.queryForList(searchQuery, Users.class);
//        Page<Users> userspage = usersRepository.search(searchQuery);
        return userspage;
    }

    @GetMapping(value = "/advsearch/{queryq}")//advanced search
    public List<Users> givenPhraseWithType(@PathVariable final String queryq) {


        Iterable<Users> userspage = usersRepository.search(queryStringQuery(queryq));
        List<Users> list=itr2lis(userspage);
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
    public List<Users> elasSearch(@RequestBody final String query){
        QueryBuilder queryBuilder=QueryBuilders.wrapperQuery(query);

        Iterable<Users> userspage = usersRepository.search(queryBuilder);
        List<Users> list=itr2lis(userspage);
        return list;
    }
    public List<Users> itr2lis(Iterable<Users> userspage){
        ArrayList<Users> list = new ArrayList<Users>();
        if (userspage != null) {
            for (Users e : userspage) {
                list.add(e);
            }
        }
        return list;

    }
}
