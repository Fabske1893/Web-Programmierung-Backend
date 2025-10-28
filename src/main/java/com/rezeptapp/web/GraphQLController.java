package com.rezeptapp.web;

import com.rezeptapp.data.api.Recipe;
import com.rezeptapp.data.api.RecipeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller; 

import java.util.List;

@Controller
@CrossOrigin
public class GraphQLController {

    private final RecipeManager recipeManager;

    @Autowired
    public GraphQLController(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    
    @QueryMapping
    public List<Recipe> recipes() {
       
        return recipeManager.getAllRecipes();
    }
}