package com.rezeptapp.web.api;

public class ShoppingListRequest {
    private String token;
    private String shoppingListText;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getShoppingListText() { return shoppingListText; }
    public void setShoppingListText(String text) { this.shoppingListText = text; }
}