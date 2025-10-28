package com.rezeptapp.data.api;

import java.util.List;
import java.util.Optional;

public interface RecipeManager {
    boolean addRecipe(Recipe recipe);
    void removeRecipe(Recipe recipe);
    List<String> getIngredientsPerEmail(); 
    List<Recipe> getAllRecipesOfUser(User user);
    List<Recipe> getAllRecipes();
    Optional<Recipe> getRecipeById(int id);
    boolean updateRecipe(Recipe recipe);
    boolean deleteRecipe(int id);
}