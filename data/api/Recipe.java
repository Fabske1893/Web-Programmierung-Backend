public interface Recipe {
    String getName();
    void setName(String name);
    String getPictureUrl();
    void setPictureUrl(String url);
    String getIngredients();
    void setIngredients(String ingredients);
    String getInstructions();
    void setInstructions(String instructions);
    int getDifficultyLevel();
    void setDifficultyLevel(int level);
    String getCategory();
    void setCategory(String category);
}