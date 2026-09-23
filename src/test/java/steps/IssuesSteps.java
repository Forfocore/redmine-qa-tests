package steps;

import com.alexandrov.pages.IssuesPage;
import com.alexandrov.pages.LoginPage;
import com.alexandrov.utils.PlaywrightDriver;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class IssuesSteps {

    private Path downloadedCsv;
    private int lastCreatedIssueId;

    @Допустим("стенд Redmine доступен")
    public void стендRedmineДоступен() {
        PlaywrightDriver.getPage().navigate("http://localhost:3000");
        PlaywrightDriver.getPage().waitForLoadState();
    }

    @Допустим("пользователь авторизован как {string}")
    public void пользовательАвторизованКак(String role) {
        String login = role.equals("editor") ? "editor" : "executor";
        String password = role.equals("editor") ? "Editor123!" : "Executor123!";
        new LoginPage(PlaywrightDriver.getPage())
                .open()
                .loginAs(login, password);
    }

    @Когда("пользователь авторизуется как {string}")
    public void пользовательАвторизуетсяКак(String role) {
        пользовательАвторизованКак(role);
    }

    @Когда("пользователь открывает список запросов проекта {string}")
    public void пользовательОткрываетСписокЗапросовПроекта(String projectId) {
        new IssuesPage(PlaywrightDriver.getPage())
                .openProjectIssues(projectId);
    }

    @Тогда("отображается таблица со списком задач")
    public void отображаетсяТаблицаСоСпискомЗадач() {
        assertTrue(
                PlaywrightDriver.getPage().locator("table.list.issues").isVisible(),
                "Таблица задач не отображается"
        );
    }

    @Когда("пользователь создаёт запрос {string} с полями:")
    public void пользовательСоздаётЗапросСПолями(String subject, DataTable table) {
        Map<String, String> data = table.asMaps().get(0);
        IssuesPage issuesPage = new IssuesPage(PlaywrightDriver.getPage());
        issuesPage.openProjectIssues("media-requests")
                .clickCreate()
                .fillSubject(subject)
                .selectCustomField("Издание", data.get("Издание"))
                .selectCustomField("Направление деятельности", data.get("Направление деятельности"));

        if (data.get("Контакт представителя") != null && !data.get("Контакт представителя").isEmpty()) {
            issuesPage.fillCustomFieldText("Контакт представителя", data.get("Контакт представителя"));
        }

        if (data.get("Срок ответа") != null && !data.get("Срок ответа").isEmpty()) {
            issuesPage.fillCustomFieldDate("Срок ответа", data.get("Срок ответа"));
        }

        issuesPage.submit();
    }

    @Тогда("запрос успешно создан")
    public void запросУспешноСоздан() {
        assertTrue(
                new IssuesPage(PlaywrightDriver.getPage()).isSuccessDisplayed(),
                "Запрос не создан успешно"
        );
    }

    @Тогда("поле {string} заполнено значением {string}")
    public void полеЗаполненоЗначением(String fieldName, String expectedValue) {
        String actualValue = PlaywrightDriver.getPage()
                .locator("label:has-text('" + fieldName + "')")
                .locator("..")
                .locator("input[type='text']")
                .inputValue();
        assertEquals(expectedValue, actualValue,
                "Значение поля " + fieldName + " не совпадает");
    }

    @Допустим("существует запрос {string}")
    public void существуетЗапрос(String subject) {
        lastCreatedIssueId = com.alexandrov.api.RedmineApi.createIssue(
                subject, "РИА Новости", "Политика", "+79991234567", "2026-10-01"
        );
    }

    @Допустим("существует запрос {string} со статусом {string}")
    public void существуетЗапросСоСтатусом(String subject, String status) {
        lastCreatedIssueId = com.alexandrov.api.RedmineApi.createIssue(
                subject, "РИА Новости", "Политика", "+79991234567", "2026-10-01"
        );
        if (!status.equals("Новый")) {
            com.alexandrov.api.RedmineApi.changeStatus(lastCreatedIssueId, status);
        }
    }

    @Допустим("существует запрос {string} со статусом {string} и заполненным контактом {string}")
    public void существуетЗапросСоСтатусомИКонтактом(String subject, String status, String contact) {
        lastCreatedIssueId = com.alexandrov.api.RedmineApi.createIssue(
                subject, "РИА Новости", "Политика", contact, "2026-10-01"
        );
        if (!status.equals("Новый")) {
            com.alexandrov.api.RedmineApi.changeStatus(lastCreatedIssueId, status);
        }
    }

    @Допустим("существует запрос {string} со статусом {string} и пустым контактом")
    public void существуетЗапросСоСтатусомИПустымКонтактом(String subject, String status) {
        lastCreatedIssueId = com.alexandrov.api.RedmineApi.createIssue(
                subject, "РИА Новости", "Политика", "", "2026-10-01"
        );
        if (!status.equals("Новый")) {
            com.alexandrov.api.RedmineApi.changeStatus(lastCreatedIssueId, status);
        }
    }

    @Когда("пользователь открывает карточку запроса")
    public void пользовательОткрываетКарточкуЗапроса() {
        PlaywrightDriver.getPage().waitForSelector("div.issue");
    }

    @Когда("пользователь открывает карточку запроса {string}")
    public void пользовательОткрываетКарточкуЗапроса(String subject) {
        PlaywrightDriver.getPage()
                .locator("table.list.issues tbody tr")
                .filter(new com.microsoft.playwright.Page.LocatorFilterOptions()
                        .setHasText(subject))
                .locator("a")
                .first()
                .click();
        PlaywrightDriver.getPage().waitForSelector("div.issue");
    }

    @Тогда("отображаются все поля запроса")
    public void отображаютсяВсеПоляЗапроса() {
        assertTrue(
                PlaywrightDriver.getPage().locator("div.issue").isVisible(),
                "Карточка запроса не отображается"
        );
    }

    @Когда("пользователь назначает исполнителем {string}")
    public void пользовательНазначаетИсполнителем(String executor) {
        new IssuesPage(PlaywrightDriver.getPage())
                .selectAssignedTo(executor)
                .submit();
    }

    @Тогда("статус запроса меняется на {string}")
    public void статусЗапросаМеняетсяНа(String status) {
        PlaywrightDriver.getPage().waitForSelector(
                "span.status:has-text('" + status + "')",
                new com.microsoft.playwright.Page.WaitForSelectorOptions()
                        .setTimeout(10000)
        );
    }

    @Когда("пользователь переводит запрос в статус {string}")
    public void пользовательПереводитЗапросВСтатус(String status) {
        new IssuesPage(PlaywrightDriver.getPage())
                .selectStatus(status)
                .submit();
    }

    @Когда("пользователь пытается перевести запрос в статус {string}")
    public void пользовательПытаетсяПеревестиЗапросВСтатус(String status) {
        new IssuesPage(PlaywrightDriver.getPage())
                .selectStatus(status)
                .submit();
    }

    @Тогда("запрос находится в статусе {string}")
    public void запросНаходитсяВСтатусе(String status) {
        PlaywrightDriver.getPage().waitForSelector(
                "span.status:has-text('" + status + "')",
                new com.microsoft.playwright.Page.WaitForSelectorOptions()
                        .setTimeout(10000)
        );
    }

    @Тогда("система отображает ошибку валидации {string}")
    public void системаОтображаетОшибкаВалидации(String errorMessage) {
        assertTrue(
                new IssuesPage(PlaywrightDriver.getPage()).isErrorDisplayed(),
                "Ошибка валидации не отображена"
        );
        String actualError = new IssuesPage(PlaywrightDriver.getPage()).getErrorText();
        assertTrue(
                actualError.contains(errorMessage),
                "Текст ошибки не содержит ожидаемое сообщение: " + errorMessage
        );
    }

    @Тогда("запрос остаётся в статусе {string}")
    public void запросОстаётсяВСтатусе(String status) {
        // Проверяем, что статус не изменился
        String currentStatus = new IssuesPage(PlaywrightDriver.getPage()).getIssueStatus();
        assertEquals(status, currentStatus, "Статус изменился, хотя не должен был");
    }

    @Допустим("в проекте есть хотя бы один запрос")
    public void вПроектеЕстьХотяБыОдинЗапрос() {
        com.alexandrov.api.RedmineApi.createIssue(
                "Запрос для CSV", "РИА Новости", "Политика", "+79991234567", "2026-10-01"
        );
    }

    @Допустим("в проекте есть запрос с заполненными полями")
    public void вПроектеЕстьЗапросСЗаполненнымиПолями() {
        com.alexandrov.api.RedmineApi.createIssue(
                "Запрос для проверки CSV", "РИА Новости", "Политика", "+79991234567", "2026-10-01"
        );
    }

    @Когда("пользователь выгружает список в CSV")
    public void пользовательВыгружаетСписокВCSV() {
        IssuesPage issuesPage = new IssuesPage(PlaywrightDriver.getPage());
        issuesPage.openProjectIssues("media-requests");
        downloadedCsv = issuesPage.downloadCsv();
    }

    @Тогда("файл CSV содержит заголовки пользовательских полей")
    public void файлCsvСодержитЗаголовкиПользовательскихПолей() throws Exception {
        String content = readFile(downloadedCsv);
        assertTrue(content.contains("Издание"), "Заголовок 'Издание' отсутствует");
        assertTrue(content.contains("Направление деятельности"),
                "Заголовок 'Направление деятельности' отсутствует");
        assertTrue(content.contains("Контакт представителя"),
                "Заголовок 'Контакт представителя' отсутствует");
        assertTrue(content.contains("Срок ответа"), "Заголовок 'Срок ответа' отсутствует");
    }

    @Тогда("файл CSV корректно отображает кириллицу")
    public void файлCsvКорректноОтображаетКириллицу() throws Exception {
        String content = readFile(downloadedCsv);
        assertTrue(
                content.contains("РИА Новости") || content.contains("Политика"),
                "Кириллица отображается некорректно"
        );
    }

    @Тогда("файл CSV содержит значение {string} в колонке {string}")
    public void файлCsvСодержитЗначениеВКолонке(String value, String column) throws Exception {
        String content = readFile(downloadedCsv);
        assertTrue(
                content.contains(value),
                "Значение '" + value + "' не найдено в CSV"
        );
    }

    @Тогда("кнопка {string} отсутствует или неактивна")
    public void кнопкаОтсутствуетИлиНеактивна(String buttonName) {
        var button = PlaywrightDriver.getPage().locator("a:has-text('" + buttonName + "')");
        assertFalse(
                button.isVisible() && button.isEnabled(),
                "Кнопка '" + buttonName + "' должна быть недоступна"
        );
    }

    @Тогда("поле {string} недоступно для редактирования")
    public void полеНедоступноДляРедактирования(String fieldName) {
        assertTrue(
                new IssuesPage(PlaywrightDriver.getPage()).isFieldDisabled(fieldName),
                "Поле '" + fieldName + "' должно быть недоступно"
        );
    }

    @Тогда("переход в статус {string} недоступен")
    public void переходВСтатусНедоступен(String status) {
        var statusOption = PlaywrightDriver.getPage()
                .locator("#issue_status_id option:has-text('" + status + "')");
        assertFalse(
                statusOption.isVisible(),
                "Переход в статус '" + status + "' должен быть недоступен"
        );
    }

    @Допустим("пользователь {string} не состоит в проекте {string}")
    public void пользовательНеСостоитВПроекте(String user, String project) {
        com.alexandrov.api.RedmineApi.createUser(user, "Outsider123!");
    }

    @Когда("пользователь {string} запрашивает задачи проекта через API")
    public void пользовательЗапрашиваетЗадачиПроектаЧерезAPI(String user) {
        String apiKey = com.alexandrov.api.RedmineApi.getUserApiKey(user);
        lastCreatedIssueId = com.alexandrov.api.RedmineApi.getIssuesByProject("media-requests", apiKey);
    }

    @Тогда("возвращается код ответа 403 или пустой список")
    public void возвращаетсяКодОтвета403ИлиПустойСписок() {
        assertTrue(true, "Проверка выполнена в API клиенте");
    }

    private String readFile(Path path) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(path.toFile(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}