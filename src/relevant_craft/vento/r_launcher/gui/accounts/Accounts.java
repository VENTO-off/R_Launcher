package relevant_craft.vento.r_launcher.gui.accounts;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.util.Callback;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.gui.notify.NotifyType;
import relevant_craft.vento.r_launcher.gui.notify.NotifyWindow;
import relevant_craft.vento.r_launcher.manager.account.Account;
import relevant_craft.vento.r_launcher.manager.account.AccountManager;
import relevant_craft.vento.r_launcher.manager.account.AccountType;
import relevant_craft.vento.r_launcher.manager.picture.PictureManager;
import relevant_craft.vento.r_launcher.minecraft.RunMinecraft;
import relevant_craft.vento.r_launcher.minecraft.auth.exceptions.AuthenticationUnavailableException;
import relevant_craft.vento.r_launcher.minecraft.auth.exceptions.InvalidCredentialsException;
import relevant_craft.vento.r_launcher.minecraft.auth.exceptions.RequestException;
import relevant_craft.vento.r_launcher.minecraft.auth.exceptions.UserMigratedException;
import relevant_craft.vento.r_launcher.minecraft.auth.responses.LicenceNotBoughtException;
import relevant_craft.vento.r_launcher.utils.DesktopUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class Accounts implements Initializable {

    public static AnchorPane loadAccountsWindow(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        AnchorPane accounts = VENTO.loadGUI(VENTO.GUI.Accounts.path);
        accounts.setLayoutX(width.getValue() / 2 - accounts.getPrefWidth() / 2);
        accounts.setLayoutY(height.getValue() / 2 - accounts.getPrefHeight() / 2);

        return accounts;
    }

    public void onClose_Account(MouseEvent mouseEvent) {
        AccountManager.initLastAccount();
        VENTO.closeGUI();
    }

    @FXML
    private JFXListView<Account> accountList;
    @FXML
    private JFXButton deleteAccount;
    @FXML
    private JFXButton upAccount;
    @FXML
    private JFXButton downAccount;
    @FXML
    private JFXButton showPassword;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountList.getItems().setAll(AccountManager.getAccounts());

        accountList.setCellFactory(new Callback<ListView<Account>, ListCell<Account>>() {
            @Override
            public JFXListCell<Account> call(ListView<Account> param) {
                return new JFXListCell<Account>() {
                    @Override
                    protected void updateItem(Account account, boolean empty) {
                        super.updateItem(account, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            if (account.type == AccountType.MOJANG) {
                                setGraphic(new ImageView(PictureManager.loadImage("mojang.png")));
                            } else if (account.type == AccountType.PIRATE) {
                                setGraphic(new ImageView(PictureManager.loadImage("pirate.png")));
                            } else if (account.type == AccountType.ELYBY) {
                                setGraphic(new ImageView(PictureManager.loadImage("elyby.png")));
                            }

                            setText(account.name);
                            setFont(Font.font(15));
                        }
                    }
                };
            }
        });

        loginField.setText(null);
        passwordField.setText(null);

        //pirate account by default
        onClick_Pirate(new ActionEvent());  //fire click
        PirateAccount.setSelected(true);
    }


    private Account selectedAccount = null;

    public void onAccountChoose(MouseEvent mouseEvent) {
        if (accountList.getSelectionModel() == null) {
            return;
        }
        if (accountList.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        selectedAccount = accountList.getSelectionModel().getSelectedItem();
        if (selectedAccount != null) {
            deleteAccount.setDisable(false);
            upAccount.setDisable(false);
            downAccount.setDisable(false);
        }
    }

    public void onAccountDelete(ActionEvent actionEvent) {
        if (selectedAccount != null && !deleteAccount.isDisable()) {
            AccountManager.removeAccount(selectedAccount);
            accountList.getItems().setAll(AccountManager.getAccounts());
            accountList.getSelectionModel().clearSelection();
            AccountManager.initLastAccount();
            selectedAccount = null;

            deleteAccount.setDisable(true);
            upAccount.setDisable(true);
            downAccount.setDisable(true);
        }
    }


    public void onAccount_Up(ActionEvent actionEvent) {
        if (selectedAccount != null && !upAccount.isDisable()) {
            AccountManager.upAccount(selectedAccount);
            accountList.getItems().setAll(AccountManager.getAccounts());
            accountList.getSelectionModel().select(selectedAccount);
        }
    }

    public void onAccount_Down(ActionEvent actionEvent) {
        if (selectedAccount != null && !downAccount.isDisable()) {
            AccountManager.downAccount(selectedAccount);
            accountList.getItems().setAll(AccountManager.getAccounts());
            accountList.getSelectionModel().select(selectedAccount);
        }
    }

    @FXML
    private JFXRadioButton MojangAccount;
    @FXML
    private JFXRadioButton PirateAccount;
    @FXML
    private JFXRadioButton ElybyAccount;
    @FXML
    private JFXTextField loginField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private FontAwesomeIconView loginText;
    @FXML
    private FontAwesomeIconView passwordText;
    @FXML
    private JFXButton addAccount;
    @FXML
    private JFXSpinner loading;

    public void onClick_Mojang(ActionEvent actionEvent) {
        if (!PirateAccount.isSelected() && !ElybyAccount.isSelected()) {
            MojangAccount.setSelected(true);
            return;
        }
        passwordField.setVisible(true);
        loginText.setText("E-mail:");
        passwordText.setText("Пароль:");

        PirateAccount.setSelected(false);
        ElybyAccount.setSelected(false);

        showPassword.setVisible(true);
        setPasswordEye(true);
    }

    public void onClick_Pirate(ActionEvent actionEvent) {
        if (!MojangAccount.isSelected() && !ElybyAccount.isSelected()) {
            PirateAccount.setSelected(true);
            return;
        }
        passwordField.setVisible(false);
        loginText.setText("Ник:");
        passwordText.setText("");

        MojangAccount.setSelected(false);
        ElybyAccount.setSelected(false);

        showPassword.setVisible(false);
    }

    public void onClick_Elyby(ActionEvent actionEvent) {
        if (!MojangAccount.isSelected() && !PirateAccount.isSelected()) {
            ElybyAccount.setSelected(true);
            return;
        }
        passwordField.setVisible(true);
        loginText.setText("Логин или E-mail:");
        passwordText.setText("Пароль:");

        MojangAccount.setSelected(false);
        PirateAccount.setSelected(false);

        showPassword.setVisible(true);
        setPasswordEye(true);
    }

    public void onAccountAdd(ActionEvent actionEvent) {
        if (MojangAccount.isSelected()) {
            if (loginField.getText() == null || passwordField.getText() == null) {
                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                notify.setTitle("Ошибка");
                notify.setMessage("Поле с почтой или паролем не должно быть пустым.");
                notify.showNotify();
                return;
            }

            String username = loginField.getText();
            String password = passwordField.getText();

            if (AccountManager.containsAccount(AccountType.MOJANG, username)) {
                NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                notify.setTitle("Ошибка");
                notify.setMessage("Такой аккаунт уже добавлен.");
                notify.showNotify();
                return;
            }

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    Platform.runLater(() -> {
                        addAccount.setDisable(true);
                        loading.setVisible(true);
                    });

                    String nick;
                    try {
                        nick = AccountManager.checkMojangAccount(username, password);
                    } catch (AuthenticationUnavailableException e) {
                        Platform.runLater(() -> {
                            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                            notify.setTitle("Ошибка авторизации");
                            notify.setMessage("Серверы Mojang сейчас недоступны. Попробуйте позже.");
                            notify.showNotify();
                        });
                        return null;
                    } catch (RequestException e) {
                        if (e instanceof LicenceNotBoughtException) {
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка аккаунта");
                                notify.setMessage("На Вашем Mojang аккаунте `не куплена лицензия` Minecraft.");
                                notify.showNotify();
                            });
                            return null;
                        } else if (e instanceof UserMigratedException) {
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка авторизации");
                                notify.setMessage("Для авторизации в аккаунт `нужно использовать E-mail` вместо никнейма.");
                                notify.showNotify();
                            });
                            return null;
                        } else if (e instanceof InvalidCredentialsException) {
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка авторизации");
                                notify.setMessage("`Неверный логин/пароль` от Вашего аккаунта.\nПопробуйте авторизоваться с этими данными на сайте `www.minecraft.net`.");
                                notify.showNotify();
                            });
                            return null;
                        } else {
                            Platform.runLater(() -> {
                                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                                notify.setTitle("Ошибка");
                                notify.setMessage("Во время авторизации в Mojang аккаунт произошла неизвестная ошибка.");
                                notify.showNotify();
                            });
                            return null;
                        }
                    } finally {
                        Platform.runLater(() -> {
                            addAccount.setDisable(false);
                            loading.setVisible(false);
                        });
                    }

                    AccountManager.addAccount(AccountType.MOJANG, username, password, nick);

                    Platform.runLater(() -> {
                        accountList.getItems().setAll(AccountManager.getAccounts());
                        loginField.setText(null);
                        passwordField.setText(null);

                        accountList.getSelectionModel().clearSelection();
                        upAccount.setDisable(true);
                        downAccount.setDisable(true);
                        deleteAccount.setDisable(true);
                    });

                    return null;
                }

            };
            new Thread(task).start();
        } else if (PirateAccount.isSelected()) {
            if (loginField.getText() == null) {
                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                notify.setTitle("Ошибка");
                notify.setMessage("Поле с ником не должно быть пустым.");
                notify.showNotify();
                return;
            }

            if (loginField.getText().length() > 16) {
                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                notify.setTitle("Ошибка");
                notify.setMessage("Длинна ника должна быть не больше 16 символов.");
                notify.showNotify();
                return;
            }

            String nick = loginField.getText();

            if (AccountManager.containsAccount(AccountType.PIRATE, nick)) {
                NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                notify.setTitle("Ошибка");
                notify.setMessage("Такой аккаунт уже добавлен.");
                notify.showNotify();
                return;
            }

            AccountManager.addAccount(AccountType.PIRATE,null, null, nick);

            accountList.getItems().setAll(AccountManager.getAccounts());
            loginField.setText(null);

            accountList.getSelectionModel().clearSelection();
            upAccount.setDisable(true);
            downAccount.setDisable(true);
            deleteAccount.setDisable(true);
        } else if (ElybyAccount.isSelected()) {
            if (loginField.getText() == null || passwordField.getText() == null) {
                NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                notify.setTitle("Ошибка");
                notify.setMessage("Поле с почтой или паролем не должно быть пустым.");
                notify.showNotify();
                return;
            }

            String username = loginField.getText();
            String password = passwordField.getText();

            if (AccountManager.containsAccount(AccountType.ELYBY, username)) {
                NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                notify.setTitle("Ошибка");
                notify.setMessage("Такой аккаунт уже добавлен.");
                notify.showNotify();
                return;
            }

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    Platform.runLater(() -> {
                        addAccount.setDisable(true);
                        loading.setVisible(true);
                    });

                    String nick = AccountManager.checkElybyAccount(username, password);

                    Platform.runLater(() -> {
                        addAccount.setDisable(false);
                        loading.setVisible(false);
                    });

                    if (AccountManager.containsAccount(AccountType.ELYBY, nick)) {
                        Platform.runLater(() -> {
                            NotifyWindow notify = new NotifyWindow(NotifyType.INFO);
                            notify.setTitle("Ошибка");
                            notify.setMessage("Такой аккаунт уже добавлен.");
                            notify.showNotify();
                        });
                        return null;
                    }

                    if (nick == null) {
                        Platform.runLater(() -> {
                            NotifyWindow notify = new NotifyWindow(NotifyType.WARNING);
                            notify.setTitle("Ошибка авторизации");
                            notify.setMessage("`Неверный логин/пароль` от Вашего аккаунта.\nПопробуйте авторизоваться с этими данными на сайте `www.ely.by`.");
                            notify.showNotify();
                            return;
                        });
                        return null;
                    }

                    AccountManager.addAccount(AccountType.ELYBY, username, password, nick);

                    Platform.runLater(() -> {
                        accountList.getItems().setAll(AccountManager.getAccounts());
                        loginField.setText(null);
                        passwordField.setText(null);

                        accountList.getSelectionModel().clearSelection();
                        upAccount.setDisable(true);
                        downAccount.setDisable(true);
                        deleteAccount.setDisable(true);
                    });

                    return null;
                }
            };
            new Thread(task).start();
        }
    }

    public void onHelp_Account(ActionEvent actionEvent) {
        DesktopUtils.openUrl("https://r-launcher.su/faq#faq_4");
    }

    public void onPasswordShow(MouseEvent mouseEvent) {
        setPasswordEye(false);

        String password = passwordField.getText();
        passwordField.clear();
        passwordField.setPromptText(password);
    }

    public void onPasswordHide(MouseEvent mouseEvent) {
        setPasswordEye(true);

        String password = passwordField.getPromptText();
        passwordField.setPromptText(null);
        passwordField.setText(password);
    }

    private void setPasswordEye(boolean isHidden) {
        MaterialDesignIconView eye;
        if (isHidden) {
            eye = new MaterialDesignIconView(MaterialDesignIcon.EYE_OFF);
        } else {
            eye = new MaterialDesignIconView(MaterialDesignIcon.EYE);
        }

        eye.setGlyphSize(20);
        showPassword.setGraphic(eye);
    }
}
