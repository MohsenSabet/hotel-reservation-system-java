package models;

public class Login {
    private int loginId;
    private String loginPassword;

    // Constructor
    public Login(int loginId, String loginPassword) {
        this.loginId = loginId;
        this.loginPassword = loginPassword;
    }

    // Getters and Setters
    public int getLoginId() { return loginId; }
    public void setLoginId(int loginId) { this.loginId = loginId; }
    
    public String getLoginPassword() { return loginPassword; }
    public void setLoginPassword(String loginPassword) { this.loginPassword = loginPassword; }

    // Method to validate login credentials
    public boolean validateLogin(String inputPassword) {
        return this.loginPassword.equals(inputPassword);
    }

    // Method to print login details (excluding the password)
    public void printLoginDetails() {
        System.out.println("Login ID: " + loginId);
    }
}
