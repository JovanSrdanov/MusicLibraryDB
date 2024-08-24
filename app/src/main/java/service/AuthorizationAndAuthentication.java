package service;

import model.User;
import repository.UserRepository;

public class AuthorizationAndAuthentication {

    private static AuthorizationAndAuthentication instance = null;
    private User loggedInUser = null;

    public static synchronized AuthorizationAndAuthentication getInstance() {
        if (instance == null) {
            instance = new AuthorizationAndAuthentication();
        }
        return instance;
    }

    public boolean login(String username, String password) {
        User user = UserRepository.getInstance().existsByUsernameAndPassword(username, password);
        if (user != null) {
            setLoggedInUser(user);
            return true;
        }
        return false;
    }

    public void logout() {
        loggedInUser = null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    private void setLoggedInUser(User user) {
        loggedInUser = user;
    }

}
