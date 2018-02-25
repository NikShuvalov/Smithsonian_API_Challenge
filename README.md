# Smithsonian_API_Challenge

## Introduction

An API created in Java using the Spring Framework for routing and H2 Database as its in-memory database. The API allows for manipulation of Show Objects (Adding, Deleting, Editing) as well as adding or deleting Keywords associated with those Show Objects.
The Database is seeded with 100 shows. The first 9 of which are fixed titles, and subsequent are "Filler Show #{n}". Each show has 4 random keywords attached to them. 
The API handles querying the Show database through Titles or Keywords. Results can be sorted in ascending or descending order by either ID or Title. Keywords for a show can be retrieved and sorted in Alphabetical or Reverse Alphabetical order as well. 


## Show JSON

```
{
	"id": int,
	"title": String,
	"description": String,
	"duration" : long,
	"originalAirDate": long,
	"rating": float,
	"keywords": String[]
}

```

## Paths

The API is hosted locally on port 8080 ("localhost:8080")

### GET Methods
#### /shows?

This path can accept URL params to query by title or keyword, and handle sorting as well as pagination options.

| Url-Params | Description|
|:---|---|
|title | Searches for shows that have the provided text somewhere in title.|
|keyword | Searches for shows that are associated with the provided keyword.|
|sort_by_title| Sorts the search results. Use "asc" for alphabetical order, "desc" for reverse alphabetical order|
|sort_by_id| Sorts the search results. "asc" for ascending, "desc" for descending.|
|results_per_page| Limits the result list to this number. Defaults to 25.|
|page| Determines which page to return. Defaults to 0.|

If no URL params are provided in the query, then the API by default will return the first 25 shows in the database in ascending ID order.

Returned results will be a JSONArray of JSON Objects representing the Show Ojects that are relevant to the search. If the API gets a null result from Database, server returns HTTP status for Internal Server Error.

#### /shows/index

Returns all shows in the database as JSONArray of the Show JSON Objects in the order they exist in the database without any contstraints.

#### /shows/{id}

Returns a specific show with the provided id in JSON format.

#### /shows/{id}/keywords?

| Url-params | Description|
| sort | Use "asc" to sort results in Alphabetical order; "desc" for reverse Alphabetical order|

Returns all keywords associated with show of given id as a JSONArray of Strings.

### POST Methods

#### /shows

Use this method to add a new Show to the database. Consumes a Show JSON. 
If successfully added will return Accepted HTTP Response, otherwise a Conflict response.

#### /shows/{id}/keywords

This method consumes a JSONArray of Strings. All of the provided keywords are associated to the Show with the given ID.*

### PUT Methods

#### /shows/{id}

Use this method to update the details of a show. This method takes a complete Show JSON (excluding Keywords). Passing a Show JSON that has a an id that doesn't match the id passed in the url will return a Bad Request response.

##### Considerations:
- Any values based as null will be set as null in database instead of ignored.
- Attempting to update a non-existing show will return an Accepted response but the action does nothing.
- Keywords passed through this method will not affect the keywords associated with the show. Use the keywords DELETE/POST paths to change keywords associated with the show.

### DELETE Methods

#### /shows/{id}

Removes a show with the given id as well as removing all keywords that were associated with that show.

#### /shows/{id}/keywords

This path consumes a JSONArray of Strings. All keyword strings provided are removed from the selected shows keyword list.

## Known Bugs
- Keyword duplicates are not handled; the same keyword can be added to a given show multiple times.*
- When querying with keywords: while shows with a duplicate of that keyword are omitted in the JSONArray sent back, they will still count towards the results_per_page count. (E.g. "Survivor" show has 2 instances of "reality" as a keyword. If the limit per page is 5, then 4 or less results will be displayed for that page)
- Keywords can be added to the Keyword table for show_ids that don't exist in the Show table
