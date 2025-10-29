package com.rezeptapp.web;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@CrossOrigin(origins = {
    "https://enigmatic-plateau-04468-3ab96016f4f2.herokuapp.com",
    "http://localhost:8080"
})
@RestController
@RequestMapping("/graphql")
public class GraphQLEndpoint {

    private final GraphQL graphQL;

    @Autowired
    public GraphQLEndpoint(GraphQL graphQL) {
        this.graphQL = graphQL;
    }

    @PostMapping
    public ResponseEntity<Object> graphql(@RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        ExecutionResult executionResult = graphQL.execute(query);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data", executionResult.getData());
        if (!executionResult.getErrors().isEmpty()) {
            result.put("errors", executionResult.getErrors());
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
