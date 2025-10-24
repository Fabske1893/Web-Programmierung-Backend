package com.rezeptapp.data.api;

import java.util.List;

public interface RecipeManager {
    boolean addRecipe(Recipe recipe);
    void removeRecipe(Recipe recipe);
    List<String> getIngredientsPerEmail(); 
    List<Recipe> getAllRecipesOfUser(User user);
    List<Recipe> getAllRecipes();


    
}