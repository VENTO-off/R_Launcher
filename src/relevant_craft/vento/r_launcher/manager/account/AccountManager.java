package relevant_craft.vento.r_launcher.manager.account;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.manager.launcher.LauncherManager;
import relevant_craft.vento.r_launcher.manager.picture.PictureManager;
import relevant_craft.vento.r_launcher.minecraft.auth.Authenticator;
import relevant_craft.vento.r_launcher.minecraft.auth.exceptions.AuthenticationUnavailableException;
import relevant_craft.vento.r_launcher.minecraft.auth.exceptions.RequestException;
import relevant_craft.vento.r_launcher.minecraft.auth.responses.AuthenticationResponse;
import relevant_craft.vento.r_launcher.minecraft.auth.responses.RefreshResponse;
import relevant_craft.vento.r_launcher.minecraft.auth_elyby.AuthenticatorElyby;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class AccountManager {

    public AccountManager() {
        loadAccounts();
    }

    private static Preferences data = Preferences.userRoot().node("rlauncher/accounts");

    private static List<Account> accounts = new ArrayList<Account>();
    public static Account selectedAccount = null;

    public int getAmount() {
        return accounts.size();
    }

    private static void loadAccounts() {
        String last_temp = data.get("lastAccount", null);
        if (last_temp != null) {
            selectedAccount = new Account(last_temp.split("<::>")[0], last_temp.split("<::>")[1], last_temp.split("<::>")[2], last_temp.split("<::>")[3], last_temp.split("<::>")[4]);
        }

        int counter = 1;
        while (true) {
            String temp = data.get("account" + counter, null);
            if (temp == null) {
                return;
            }

            String[] acc = temp.split("<::>");

            accounts.add(new Account(acc[0], acc[1], acc[2], acc[3], acc[4]));

            counter++;
        }
    }

    public static void saveAccounts() {
        try {
            data.clear();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < accounts.size(); i++) {
            data.put("account" + String.valueOf(i+1), accounts.get(i).type.name() + "<::>" + accounts.get(i).email + "<::>" + accounts.get(i).name + "<::>" + accounts.get(i).password + "<::>" + accounts.get(i).accessToken);
        }

        saveLastAccount();
    }

    public static void saveLastAccount() {
        if (selectedAccount != null) {
            data.put("lastAccount", selectedAccount.type.name() + "<::>" + selectedAccount.email + "<::>" + selectedAccount.name + "<::>" + selectedAccount.password + "<::>" + selectedAccount.accessToken);
        } else {
            data.remove("lastAccount");
        }
    }

    public static void initLastAccount() {
        if (AccountManager.selectedAccount == null) {
            VENTO.launcherManager.currentAccount.setText("<нет аккаунтов>");
        } else {
            VENTO.launcherManager.currentAccount.setText(AccountManager.selectedAccount.name);
            VENTO.launcherManager.currentAccount.setGraphic(convertToImageType(AccountManager.selectedAccount));
        }
    }

    public static Collection<Account> getAccounts() {
        Collection<Account> accs = new ArrayList<>();
        for (Account acc : accounts) {
            accs.add(acc);
        }

        return accs;
    }

    public static JFXButton convertToButton(int index, double width) {
        JFXButton acc = new JFXButton();
        acc.setButtonType(JFXButton.ButtonType.FLAT);
        acc.setMnemonicParsing(false);
        acc.setText(accounts.get(index).name);
        acc.setCursor(Cursor.HAND);
        acc.setFont(Font.font(15));
        acc.setPrefWidth(width);
        acc.setAlignment(Pos.BASELINE_LEFT);
        acc.setGraphic(convertToImageType(accounts.get(index)));
        if (accounts.get(index).type == AccountType.MOJANG) {
            acc.setAccessibleRoleDescription(AccountType.MOJANG.name());
        } else if (accounts.get(index).type == AccountType.PIRATE) {
            acc.setAccessibleRoleDescription(AccountType.PIRATE.name());
        } else if (accounts.get(index).type == AccountType.ELYBY) {
            acc.setAccessibleRoleDescription(AccountType.ELYBY.name());
        }

        return acc;
    }

    public static ImageView convertToImageType(Account account) {
        if (account.type == AccountType.MOJANG) {
            return new ImageView(PictureManager.loadImage("mojang.png"));
        } else if (account.type == AccountType.PIRATE) {
            return new ImageView(PictureManager.loadImage("pirate.png"));
        } else if (account.type == AccountType.ELYBY) {
            return new ImageView(PictureManager.loadImage("elyby.png"));
        } else {
            return new ImageView();
        }
    }

    public static JFXButton createEmpty() {
        JFXButton empty = new JFXButton();
        empty.setButtonType(JFXButton.ButtonType.FLAT);
        empty.setMnemonicParsing(false);
        empty.setText("Здесь будут\nВаши аккаунты.");
        empty.setFont(Font.font(15));
        empty.setPrefWidth(VENTO.launcherManager.accountList.widthProperty().getValue());
        empty.setPrefHeight(VENTO.launcherManager.accountList.widthProperty().getValue() / 2);
        empty.setAlignment(Pos.CENTER);
        empty.setContentDisplay(ContentDisplay.BOTTOM);
        empty.setTextAlignment(TextAlignment.CENTER);
        empty.setDisable(true);
        empty.setWrapText(true);
        FontAwesomeIconView empty_icon = new FontAwesomeIconView(FontAwesomeIcon.USER_CIRCLE_ALT);
        empty_icon.setGlyphSize(30);
        empty.setGraphic(empty_icon);

        return empty;
    }

    public static Account getByName(String nick, AccountType type) {
        for (Account acc : accounts) {
            if (acc.name.equals(nick) && acc.type == type) {
                return acc;
            }
        }

        return null;
    }

    public static void addAccount(AccountType type, String username, String password, String nick) {
        if (type == AccountType.PIRATE) {
            accounts.add(new Account(type.name(),"#", nick.trim(), "#", "#"));
        } else if (type == AccountType.MOJANG || type == AccountType.ELYBY) {
            accounts.add(new Account(type.name(), username.trim(), nick.trim(), password.trim(), "#"));
        }

        saveAccounts();
    }

    public static void updateAccount(Account account, String nick) {
        if (!account.name.equals(nick)) {
            VENTO.launcherManager.currentAccount.setText(nick);
        }

        Account temp = account;
        temp.name = nick;

        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).name.equals(account.name) && accounts.get(i).type == account.type) {
                accounts.set(i, temp);
            }
        }

        if (selectedAccount.name.equals(account.name) && selectedAccount.type == account.type) {
            selectedAccount = temp;
        }

        saveAccounts();
        saveLastAccount();
    }

    public static void removeAccount(Account account) {
        accounts.remove(account);

        if (selectedAccount != null && selectedAccount == account) {
            selectedAccount = null;
        }

        saveAccounts();
    }

    public static boolean containsAccount(AccountType accType, String name) {
        if (accType == AccountType.MOJANG) {
            for (Account acc : accounts) {
                if (acc.type == AccountType.MOJANG && acc.email.equalsIgnoreCase(name)) {
                    return true;
                }
            }

            return false;
        } else if (accType == AccountType.PIRATE) {
            for (Account acc : accounts) {
                if (acc.type == AccountType.PIRATE && acc.name.equals(name)) {
                    return true;
                }
            }

            return false;
        } else if (accType == AccountType.ELYBY) {
            for (Account acc : accounts) {
                if (acc.type == AccountType.ELYBY && (acc.email.equalsIgnoreCase(name) || acc.name.equalsIgnoreCase(name))) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void upAccount(Account account) {
        int index = accounts.indexOf(account);
        if (index - 1 == -1) {
            for (Account acc : accounts) {
                Collections.swap(accounts, index, index + 1);
                index += 1;
                if (index == accounts.size() - 1) {
                    break;
                }
            }
        } else {
            Collections.swap(accounts, index, index - 1);
        }

        saveAccounts();
    }

    public static void downAccount(Account account) {
        int index = accounts.indexOf(account);
        if (index + 1 == accounts.size()) {
            for (Account acc : accounts) {
                Collections.swap(accounts, index, index - 1);
                index -= 1;
                if (index == 0) {
                    break;
                }
            }
        } else {
            Collections.swap(accounts, index, index + 1);
        }

        saveAccounts();
    }

    /**********************************************
     *                   MOJANG                   *
     **********************************************/

    public static String checkMojangAccount(String username, String password) throws RequestException, AuthenticationUnavailableException {
        AuthenticationResponse authResponse = Authenticator.authenticate(username, password);
        return authResponse.getSelectedProfile().getName();
    }

    public static AuthenticationResponse authMojangAccount(Account account) throws RequestException, AuthenticationUnavailableException {
        if (account == null) {
            return null;
        }
        AuthenticationResponse authResponse = Authenticator.authenticate(account.email, account.password, LauncherManager.launcher_id.toString());
        account.accessToken = authResponse.getAccessToken();

        updateAccount(account, authResponse.getSelectedProfile().getName());

        return authResponse;
    }

    public static RefreshResponse refreshMojangAccount(Account account) {
        try {
            boolean isValid = Authenticator.validate(account.accessToken);
            if (isValid) {
                RefreshResponse refreshResponse = Authenticator.refresh(account.accessToken, LauncherManager.launcher_id.toString());

                account.accessToken = refreshResponse.getAccessToken();

                updateAccount(account, refreshResponse.getSelectedProfile().getName());

                return refreshResponse;
            }
            return null;
        } catch (RequestException e) {
            return null;
        } catch (AuthenticationUnavailableException e) {
            return null;
        }
    }

    /**********************************************
     *                   ELY.BY                   *
     **********************************************/

    public static String checkElybyAccount(String username, String password) {
        try {
            AuthenticationResponse authResponse = AuthenticatorElyby.authenticate(username, password, LauncherManager.launcher_id.toString());
            return authResponse.getSelectedProfile().getName();
        } catch (RequestException e) {
            return null;
        } catch (AuthenticationUnavailableException e) {
            return null;
        }
    }

    public static AuthenticationResponse authElybyAccount(Account account) {
        if (account == null) {
            return null;
        }

        try {
            AuthenticationResponse authResponse = AuthenticatorElyby.authenticate(account.email, account.password, LauncherManager.launcher_id.toString());

            account.accessToken = authResponse.getAccessToken();

            updateAccount(account, authResponse.getSelectedProfile().getName());

            return authResponse;
        } catch (RequestException e) {
            return null;
        } catch (AuthenticationUnavailableException e) {
            return null;
        }
    }

    public static RefreshResponse refreshElybyAccount(Account account) {
        try {
            boolean isValid = AuthenticatorElyby.validate(account.accessToken);
            if (isValid) {
                RefreshResponse refreshResponse = AuthenticatorElyby.refresh(account.accessToken, LauncherManager.launcher_id.toString());

                account.accessToken = refreshResponse.getAccessToken();

                updateAccount(account, refreshResponse.getSelectedProfile().getName());

                return refreshResponse;
            }
            return null;
        } catch (RequestException e) {
            return null;
        } catch (AuthenticationUnavailableException e) {
            return null;
        }
    }
}
