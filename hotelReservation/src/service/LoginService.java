package service;

import dataAccessObjects.LoginDAO;
import models.Login;

import java.sql.SQLException;

public class LoginService {
    private LoginDAO loginDAO;

    public LoginService() {
        this.loginDAO = new LoginDAO();
    }

    public void addLogin(Login login) throws SQLException {
        loginDAO.addLogin(login);
    }

    public Login getLogin(int loginId) throws SQLException {
        return loginDAO.getLogin(loginId);
    }

    public boolean validateLogin(int loginId, String inputPassword) throws SQLException {
        return loginDAO.validateLogin(loginId, inputPassword);
    }

    public void updateLoginPassword(int loginId, String newPassword) throws SQLException {
        loginDAO.updateLoginPassword(loginId, newPassword);
    }

    public void deleteLogin(int loginId) throws SQLException {
        loginDAO.deleteLogin(loginId);
    }
}
