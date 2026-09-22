package com.alexandrov.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class LoginPage {
    private final Page page;

    private final Locator usernameField;
    private final Locator passwordField;
    private final Locator loginButton;
    private final Locator errorMessage;
    private final Locator loggedInUser;

    public LoginPage(Page page) {
        this.page = page;
        this.usernameField = page.locator("#username");
        this.passwordField = page.locator("#password");
        this.loginButton = page.locator("input[name='login']");
        this.errorMessage = page.locator("#errorExplanation");
        this.loggedInUser = page.locator(".logged-in a.user");
    }

    public LoginPage open() {
        page.navigate("http://localhost:3000/login");
        return this;
    }

    public LoginPage loginAs(String username, String password) {
        usernameField.fill(username);
        passwordField.fill(password);
        loginButton.click();
        page.waitForURL("**/my/page", new Page.WaitForURLOptions().setTimeout(10000));
        return this;
    }

    public boolean isErrorDisplayed() {
        return errorMessage.isVisible();
    }

    public String getErrorText() {
        return errorMessage.textContent();
    }

    public boolean isLoggedIn() {
        return loggedInUser.isVisible();
    }

    public String getLoggedInUsername() {
        return loggedInUser.textContent();
    }
}