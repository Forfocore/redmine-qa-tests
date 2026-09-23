package steps;

import com.alexandrov.pages.LoginPage;
import com.alexandrov.utils.PlaywrightDriver;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import static org.junit.jupiter.api.Assertions.*;

public class LoginSteps {

    @Когда("пользователь входит как {string} с паролем {string}")
    public void пользовательВходитКакСПаролем(String login, String password) {
        new LoginPage(PlaywrightDriver.getPage())
                .open()
                .loginAs(login, password);
    }

    @Тогда("пользователь успешно авторизован и видит имя {string}")
    public void пользовательУспешноАвторизованИВидитИмя(String expectedName) {
        LoginPage loginPage = new LoginPage(PlaywrightDriver.getPage());
        assertTrue(loginPage.isLoggedIn(), "Пользователь не авторизован");
        assertEquals(expectedName, loginPage.getLoggedInUsername(),
                "Имя пользователя не совпадает");
    }
}