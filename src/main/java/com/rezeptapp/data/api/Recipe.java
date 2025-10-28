package com.rezeptapp.data.api;
import com.rezeptapp.data.model.Ingredient;
import java.util.List;

public interface Recipe {
    String getName();
    void setName(String name);
    String getPictureUrl();
    void setPictureUrl(String url);
    List<Ingredient> getIngredients();        
    void setIngredients(List<Ingredient> ingredients);
    String getInstructions();
    void setInstructions(String instructions);
    String getDifficultyLevel();
    void setDifficultyLevel(String level);
    String getCategory();
    void setCategory(String category);
    int getId();
    void setId(int id);
    
}