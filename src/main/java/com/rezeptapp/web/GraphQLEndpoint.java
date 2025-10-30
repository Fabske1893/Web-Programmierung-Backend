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

@RestController
@CrossOrigin(origins = "*")
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
