package com.alexandrov.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.nio.file.Path;

public class IssuesPage {
    private final Page page;

    private final Locator createButton;
    private final Locator subjectField;
    private final Locator submitButton;
    private final Locator successFlash;
    private final Locator issueRow;
    private final Locator csvLink;

    public IssuesPage(Page page) {
        this.page = page;
        this.createButton = page.locator("a.new-issue");
        this.subjectField = page.locator("#issue_subject");
        this.submitButton = page.locator("input[name='commit']");
        this.successFlash = page.locator("#flash_notice");
        this.issueRow = page.locator("table.list.issues tbody tr");
        this.csvLink = page.locator("a.csv");
    }

    public IssuesPage openProjectIssues(String projectId) {
        page.navigate("http://localhost:3000/projects/" + projectId + "/issues");
        page.waitForLoadState();
        return this;
    }

    public IssuesPage clickCreate() {
        createButton.click();
        page.waitForURL("**/issues/new");
        return this;
    }

    public IssuesPage fillSubject(String subject) {
        subjectField.fill(subject);
        return this;
    }

    public IssuesPage selectCustomField(String fieldName, String value) {
        page.locator("label:has-text('" + fieldName + "')")
                .locator("..")
                .locator("select, input")
                .first()
                .selectOption(value);
        return this;
    }

    public IssuesPage fillCustomFieldText(String fieldName, String value) {
        page.locator("label:has-text('" + fieldName + "')")
                .locator("..")
                .locator("input[type='text']")
                .fill(value);
        return this;
    }

    public IssuesPage submit() {
        submitButton.click();
        page.waitForURL("**/issues/**", new Page.WaitForURLOptions().setTimeout(10000));
        return this;
    }

    public boolean isSuccessDisplayed() {
        return successFlash.isVisible();
    }

    public int getIssuesCount() {
        return issueRow.count();
    }

    public Path downloadCsv() {
        var download = page.waitForDownload(() -> {
            csvLink.click();
        });
        return download.path();
    }
}