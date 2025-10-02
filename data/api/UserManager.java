Interface Usermanager {
    String loginUser(String email, String password);
    boolean logoffUser(String email);
    boolean registerUser(User user);
    
}