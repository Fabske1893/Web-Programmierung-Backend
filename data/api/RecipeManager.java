import java.util.List;

public Interface RecipeManager {
    boolean addRecipe(Recipe recipe);
    void removeRecipe(Recipe recipe);
    List<Recipe.getIngredients()> getIngredientsPerEmail();
    List<Recipe> getAllRecipesOfUser(User user);
    List<Recipe> getAllRecipes();
}