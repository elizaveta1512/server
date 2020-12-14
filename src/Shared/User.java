package Shared;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID =
            9166115907993921871L;
    String login;
    String password;
    String type;

    public User(String login, String password, String type) {
        this.login = login;
        this.password = password;
        this.type = type;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }
}
