package com.rezeptapp.web;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class GraphQLEndpoint {

    private final GraphQL graphQL;

    @Autowired
    public GraphQLEndpoint(GraphQL graphQL) {
        this.graphQL = graphQL;
    }

    @PostMapping("/graphql")
    public ResponseEntity<Object> graphql(@RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        
        ExecutionResult executionResult = graphQL.execute(query);
        
        Map<String, Object> result = new LinkedHashMap<>();
        
        if (executionResult.getErrors().isEmpty()) {
            result.put("data", executionResult.getData());
        } else {
            result.put("errors", executionResult.getErrors());
            result.put("data", executionResult.getData());
        }
        
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
