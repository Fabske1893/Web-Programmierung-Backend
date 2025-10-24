
import data.api.Recipe;

public class RecipeImpl implements Recipe {
    private String name;
    private String pictureUrl;
    private String ingredients;
    private String instructions;
    private int difficultyLevel;
    private String category;


    @Override
    public String getName() { return this.name; }
    @Override
    public void setName(String name) { this.name = name; }

    @Override
    public String getPictureUrl() { return this.pictureUrl; }
    @Override
    public void setPictureUrl(String url) { this.pictureUrl = url; }

    @Override
    public String getIngredients() { return this.ingredients; }
    @Override
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    @Override
    public String getInstructions() { return this.instructions; }
    @Override
    public void setInstructions(String instructions) { this.instructions = instructions; }

    @Override
    public int getDifficultyLevel() { return this.difficultyLevel; }
    @Override
    public void setDifficultyLevel(int level) { this.difficultyLevel = level; }

    @Override
    public String getCategory() { return this.category; }
    @Override
    public void setCategory(String category) { this.category = category; }
}