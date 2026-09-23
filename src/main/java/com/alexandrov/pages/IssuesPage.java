package com.alexandrov.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.nio.file.Path;

public class IssuesPage {
    private final Page page;

    private final Locator createButton;
    private final Locator subjectField;
    private final Locator descriptionField;
    private final Locator submitButton;
    private final Locator successFlash;
    private final Locator issueRow;
    private final Locator csvLink;
    private final Locator issueStatus;
    private final Locator issueAssignedTo;
    private final Locator errorMessage;

    public IssuesPage(Page page) {
        this.page = page;
        this.createButton = page.locator("a.new-issue");
        this.subjectField = page.locator("#issue_subject");
        this.descriptionField = page.locator("#issue_description");
        this.submitButton = page.locator("input[name='commit']");
        this.successFlash = page.locator("#flash_notice");
        this.issueRow = page.locator("table.list.issues tbody tr");
        this.csvLink = page.locator("a.csv");
        this.issueStatus = page.locator("#issue_status_id");
        this.issueAssignedTo = page.locator("#issue_assigned_to_id");
        this.errorMessage = page.locator("#errorExplanation");
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

    public IssuesPage fillDescription(String description) {
        descriptionField.fill(description);
        return this;
    }

    public IssuesPage selectCustomField(String fieldName, String value) {
        page.locator("label:has-text('" + fieldName + "')")
                .locator("..")
                .locator("select")
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

    public IssuesPage fillCustomFieldDate(String fieldName, String date) {
        page.locator("label:has-text('" + fieldName + "')")
                .locator("..")
                .locator("input[type='text']")
                .fill(date);
        return this;
    }

    public IssuesPage selectStatus(String status) {
        issueStatus.selectOption(status);
        return this;
    }

    public IssuesPage selectAssignedTo(String user) {
        issueAssignedTo.selectOption(user);
        return this;
    }

    public IssuesPage submit() {
        submitButton.click();
        page.waitForLoadState();
        return this;
    }

    public boolean isSuccessDisplayed() {
        return successFlash.isVisible();
    }

    public boolean isErrorDisplayed() {
        return errorMessage.isVisible();
    }

    public String getErrorText() {
        return errorMessage.textContent();
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

    public String getIssueStatus() {
        return issueStatus.inputValue();
    }

    public boolean isFieldDisabled(String fieldName) {
        return page.locator("#issue_assigned_to_id").isDisabled();
    }
}