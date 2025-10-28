package com.rezeptapp.web;

import com.rezeptapp.data.api.Recipe;
import com.rezeptapp.data.api.RecipeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GraphQLController {

    private final RecipeManager recipeManager;

    @Autowired
    public GraphQLController(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    @PostMapping(path = "/graphQL", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> graphQLRoot(@RequestBody Map<String, Object> body) {
        try {
            List<Recipe> allRecipes = recipeManager.getAllRecipes();
            List<Map<String, Object>> recipes = new ArrayList<>();

            for (Recipe r : allRecipes) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", r.getId());
                m.put("imageUrl", r.getImageUrl());
                m.put("title", r.getName());
                m.put("difficulty", r.getDifficulty());
                m.put("category", r.getCategory());
                m.put("likes", r.getLikes());
                recipes.add(m);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("recipes", recipes);

            Map<String, Object> response = new HashMap<>();
            response.put("data", data);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResp = new HashMap<>();
            errorResp.put("errors", new String[]{"Internal server error"});
            return new ResponseEntity<>(errorResp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
