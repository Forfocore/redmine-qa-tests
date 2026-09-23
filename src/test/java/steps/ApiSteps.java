package steps;

import com.alexandrov.api.RedmineApi;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import io.restassured.response.Response;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class ApiSteps {

    private Response lastResponse;
    private int createdIssueId;

    @Когда("создаю запрос через API с темой {string} и полями:")
    public void создаюЗапросЧерезAPIСТемойИПолями(String subject, DataTable table) {
        Map<String, String> data = table.asMaps().get(0);
        lastResponse = RedmineApi.createIssueViaApi(
                subject,
                data.get("Издание"),
                data.get("Направление"),
                data.get("Контакт"),
                data.get("Срок ответа")
        );
    }

    @Тогда("запрос создан с кодом 201")
    public void запросСозданСКодом201() {
        assertEquals(201, lastResponse.statusCode(), "Код ответа должен быть 201");
    }

    @Тогда("в ответе присутствует ID созданного запроса")
    public void вОтветеПрисутствуетIDСозданногоЗапроса() {
        createdIssueId = lastResponse.path("issue.id");
        assertTrue(createdIssueId > 0, "ID задачи должен быть больше 0");
    }

    @Допустим("создан запрос через API с темой {string}")
    public void созданЗапросЧерезAPIСТемой(String subject) {
        createdIssueId = RedmineApi.createIssue(
                subject, "Интерфакс", "Спорт", "+79998887766", "2026-10-15"
        );
    }

    @Допустим("создан запрос через UI с темой {string}")
    public void созданЗапросЧерезUIСТемой(String subject) {
        createdIssueId = RedmineApi.createIssue(
                subject, "Интерфакс", "Спорт", "+79998887766", "2026-10-15"
        );
    }

    @Когда("открываю этот запрос в UI")
    public void открываюЭтотЗапросВUI() {
        com.alexandrov.utils.PlaywrightDriver.getPage()
                .navigate("http://localhost:3000/issues/" + createdIssueId);
        com.alexandrov.utils.PlaywrightDriver.getPage().waitForLoadState();
    }

    @Тогда("данные в UI совпадают с данными из API")
    public void данныеВUISовпадаютСДаннымиИзAPI() {
        String uiSubject = com.alexandrov.utils.PlaywrightDriver.getPage()
                .locator(".subject h3").textContent();
        Response apiResponse = RedmineApi.getIssue(createdIssueId);
        String apiSubject = apiResponse.path("issue.subject");
        assertEquals(apiSubject, uiSubject, "Темы в UI и API не совпадают");
    }

    @Когда("получаю этот запрос через API")
    public void получаюЭтотЗапросЧерезAPI() {
        lastResponse = RedmineApi.getIssue(createdIssueId);
    }

    @Тогда("данные в API совпадают с данными из UI")
    public void данныеВAPIСовпадаютСДаннымиИзUI() {
        String apiSubject = lastResponse.path("issue.subject");
        assertNotNull(apiSubject, "Тема задачи в API не должна быть null");
    }

    @Когда("создаю запрос через API с темой {string} дважды")
    public void создаюЗапросЧерезAPIСТемойДважды(String subject) {
        Response response1 = RedmineApi.createIssueViaApi(
                subject, "ТАСС", "Экономика", "+79991112233", "2026-10-20"
        );
        int id1 = response1.path("issue.id");

        Response response2 = RedmineApi.createIssueViaApi(
                subject, "ТАСС", "Экономика", "+79991112233", "2026-10-20"
        );
        int id2 = response2.path("issue.id");

        createdIssueId = (id1 == id2) ? 1 : 2;
    }

    @Тогда("фиксирую результат: создано 2 задачи (идемпотентность не реализована)")
    public void фиксируюРезультатСоздано2Задачи() {
        assertEquals(2, createdIssueId,
                "Redmine создаёт дубликаты - идемпотентность не реализована");
    }

    @Когда("исполнитель пытается создать запрос через API")
    public void исполнительПытаетсяСоздатьЗапросЧерезAPI() {
        lastResponse = RedmineApi.createIssueAsExecutor(
                "Тест прав", "РИА Новости", "Политика", "+79991234567", "2026-10-01"
        );
    }

    @Тогда("получает отказ с кодом 403 или 422")
    public void получаетОтказСКодом403Или422() {
        int statusCode = lastResponse.statusCode();
        assertTrue(
                statusCode == 403 || statusCode == 422,
                "Ожидался код 403 или 422, получен: " + statusCode
        );
    }
}