package com.rezeptapp.web;

import graphql.ExecutionInput;
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
    "http://localhost:8080",
    "http://localhost:3000"
})
@RestController
@RequestMapping("/graphql")
public class GraphQLEndpoint {

    private final GraphQL graphQL;

    @Autowired
    public GraphQLEndpoint(GraphQL graphQL) {
        this.graphQL = graphQL;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> graphql(@RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) request.getOrDefault("variables", new LinkedHashMap<>());
        String operationName = (String) request.get("operationName");

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .operationName(operationName)
                .variables(variables)
                .build();

        ExecutionResult executionResult = graphQL.execute(executionInput);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data", executionResult.getData());
        if (!executionResult.getErrors().isEmpty()) {
            result.put("errors", executionResult.getErrors());
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
