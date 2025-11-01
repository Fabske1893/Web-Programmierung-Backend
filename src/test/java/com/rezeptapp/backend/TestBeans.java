package com.rezeptapp.backend;

import com.rezeptapp.data.api.Recipe;
import com.rezeptapp.data.api.RecipeManager;
import com.rezeptapp.data.api.User;
import com.rezeptapp.data.api.UserManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@TestConfiguration
public class TestBeans {

    @Bean
    public RecipeManager recipeManager() {
        return new RecipeManager() {
            @Override
            public boolean addRecipe(Recipe recipe) { return true; }

            @Override
            public void removeRecipe(Recipe recipe) { }

            @Override
            public List<String> getIngredientsPerEmail() { return new ArrayList<>(); }

            @Override
            public List<Recipe> getAllRecipesOfUser(User user) { return new ArrayList<>(); }

            @Override
            public List<Recipe> getAllRecipes() { return new ArrayList<>(); }

            @Override
            public Optional<Recipe> getRecipeById(int id) { return Optional.empty(); }

            @Override
            public boolean updateRecipe(Recipe recipe) { return true; }

            @Override
            public boolean deleteRecipe(int id) { return true; }
        };
    }

    @Bean
    public UserManager userManager() {
        return new UserManager() {
            @Override
            public String loginUser(String email, String password) { return "TOKEN_TEST"; }

            @Override
            public boolean logoffUser(String email) { return true; }

            @Override
            public boolean registerUser(User username) { return true; }

            @Override
            public String getEmailFromToken(String token) { return token == null || token.equals("OFF") ? null : "test@example.com"; }
        };
    }
}
