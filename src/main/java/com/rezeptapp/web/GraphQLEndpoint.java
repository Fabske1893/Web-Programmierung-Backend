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
import java.util.HashMap;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/graphql")
public class GraphQLEndpoint {

    private final GraphQL graphQL;

    @Autowired
    public GraphQLEndpoint(GraphQL graphQL) {
        this.graphQL = graphQL;
    }

    @PostMapping("/graphql")
    public ResponseEntity<Object> graphql(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        
        String query = (String) request.get("query");
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");
        
        // Context mit Authorization Header erstellen
        Map<String, Object> context = new HashMap<>();
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            context.put("Authorization", authorizationHeader);
        }
        
        // ExecutionInput mit Context erstellen
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .variables(variables != null ? variables : new HashMap<>())
                .context(context)
                .build();
        
        ExecutionResult executionResult = graphQL.execute(executionInput);
        
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
}