package com.rezeptapp.data.api;
import com.rezeptapp.data.model.Ingredient;
import java.util.List;

public interface Recipe {
    String getTitle();
    void setTitle(String title);
    String getImageUrl();
    void setImageUrl(String url);
    List<Ingredient> getIngredients();        
    void setIngredients(List<Ingredient> ingredients);
    String getInstructions();
    void setInstructions(String instructions);
    String getDifficulty();
    void setDifficulty(String difficulty);
    String getCategory();
    void setCategory(String category);
    int getId();
    void setId(int id);
    int getLikes();
    void setLikes(int likes);
    
}