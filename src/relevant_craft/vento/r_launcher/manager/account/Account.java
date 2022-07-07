package relevant_craft.vento.r_launcher.manager.account;

public class Account {
    public AccountType type;
    public String email;
    public String name;
    public String password;
    public String accessToken;

    public Account(String _acctype, String _email, String _name, String _password, String _accessToken) {
        type = AccountType.valueOf(_acctype);
        email = _email;
        name = _name;
        password = _password;
        accessToken = _accessToken;
    }
}
