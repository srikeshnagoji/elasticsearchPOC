# Spring Data Elastic Search POC
This POC has the Elastic Search functionality with Spring Data Elastic Search.

## Description
This Project loads the list of Users into Elastic and Searches.
Using the following endpoints, different operations can be achieved:
- `/rest/search/name/{text}` - This returns the list of Users for a name.
- `/rest/search/salary/{salary}` - This returns the list of Users for the matching salary.
- `/rest/search/all` - This returns all Users.
- `/rest/search/fuzzy/{fuzzyString}` - This returns the list of Users with similar names.
- `rest/search/advsearch/{queryq}` - This is the advanced search endpoint which accepts complex search criteria with ANDs and ORs.
- `rest/search/jsonquery` - This endpoint takes in JSON query in a get request.

## Libraries used
- Spring Boot
- Spring REST Controller
- Spring Data Elastic Search
