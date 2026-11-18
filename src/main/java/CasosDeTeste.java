
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.BooleanSupplier;

public class CasosDeTeste {

    static final String LOGIN =
            "https://practicetestautomation.com/practice-test-login/";

    static final String FORM =
            "https://www.selenium.dev/selenium/web/web-form.html";

    static WebDriver driver;
    static WebDriverWait wait;

    static int passou = 0;
    static int total = 0;

    public static void main(String[] args) {

        ChromeOptions options = new ChromeOptions();
        options.setBinary("/usr/bin/google-chrome");
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            executar("CT01 Login válido redireciona", CasosDeTeste::ct01);
            executar("CT02 Login válido exibe boas-vindas", CasosDeTeste::ct02);
            executar("CT03 Usuário inválido", CasosDeTeste::ct03);
            executar("CT04 Senha inválida", CasosDeTeste::ct04);
            executar("CT05 Logout", CasosDeTeste::ct05);
            executar("CT06 Envio do formulário", CasosDeTeste::ct06);
            executar("CT07 Campos bloqueados", CasosDeTeste::ct07);
            executar("CT08 Combobox", CasosDeTeste::ct08);
            executar("CT09 Checkboxes", CasosDeTeste::ct09);
            executar("CT10 Radio buttons", CasosDeTeste::ct10);

        } finally {
            driver.quit();

            System.out.println("\nPlacar: " + passou
                    + " de " + total + " casos passaram");
        }
    }

    static void executar(String nome, BooleanSupplier caso) {

        total++;
        boolean ok;

        try {
            ok = caso.getAsBoolean();

        } catch (Exception e) {
            ok = false;
            System.out.println("Erro em " + nome + ": "
                    + e.getMessage());
        }

        if (ok) {
            passou++;
        }

        System.out.println((ok ? "PASSOU " : "FALHOU ") + nome);
    }

    static void fazerLogin(String usuario, String senha) {

        driver.get(LOGIN);

        driver.findElement(By.id("username")).sendKeys(usuario);
        driver.findElement(By.id("password")).sendKeys(senha);
        driver.findElement(By.id("submit")).click();
    }

    // CT01 - Login válido redireciona
    static boolean ct01() {

        fazerLogin("student", "Password123");

        wait.until(ExpectedConditions.urlContains(
                "logged-in-successfully"));

        return driver.getCurrentUrl()
                .contains("logged-in-successfully");
    }

    // CT02 - Login válido exibe boas-vindas
    static boolean ct02() {

        fazerLogin("student", "Password123");

        WebElement titulo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.tagName("h1")));

        return titulo.getText().equals("Logged In Successfully")
                && driver.getPageSource()
                .contains("Congratulations student");
    }

    // CT03 - Usuário inválido
    static boolean ct03() {

        fazerLogin("incorrectUser", "Password123");

        WebElement mensagem = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("error")));

        return mensagem.getText()
                .contains("Your username is invalid!");
    }

    // CT04 - Senha inválida
    static boolean ct04() {

        fazerLogin("student", "incorrectPassword");

        WebElement mensagem = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("error")));

        return mensagem.getText()
                .contains("Your password is invalid!");
    }

    // CT05 - Logout
    static boolean ct05() {

        fazerLogin("student", "Password123");

        wait.until(ExpectedConditions.urlContains(
                "logged-in-successfully"));

        driver.findElement(By.linkText("Log out")).click();

        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("username"))).isDisplayed();
    }

    // CT06 - Envio do formulário
    static boolean ct06() {

        driver.get(FORM);

        driver.findElement(By.id("my-text-id"))
                .sendKeys("Aluno UNIFIO");

        driver.findElement(By.cssSelector("button"))
                .click();

        return wait.until(
                ExpectedConditions.textToBePresentInElementLocated(
                        By.tagName("body"), "Received!"));
    }

    // CT07 - Campos bloqueados
    static boolean ct07() {

        driver.get(FORM);

        WebElement desabilitado = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("input:disabled")));

        WebElement somenteLeitura =
                driver.findElement(By.name("my-readonly"));

        boolean primeiroBloqueado =
                !desabilitado.isEnabled();

        boolean segundoSomenteLeitura =
                somenteLeitura.getDomAttribute("readonly") != null;

        return primeiroBloqueado && segundoSomenteLeitura;
    }

    // CT08 - Combobox
    static boolean ct08() {

        driver.get(FORM);

        WebElement elemento = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.name("my-select")));

        Select dropdown = new Select(elemento);

        dropdown.selectByVisibleText("Two");

        WebElement selecionado =
                dropdown.getFirstSelectedOption();

        return selecionado.getText().trim().equals("Two")
                && selecionado.getAttribute("value").equals("2");
    }

    // CT09 - Checkboxes
    static boolean ct09() {

        driver.get(FORM);

        WebElement primeira =
                driver.findElement(By.id("my-check-1"));

        WebElement segunda =
                driver.findElement(By.id("my-check-2"));

        boolean estadoInicial =
                primeira.isSelected() && !segunda.isSelected();

        primeira.click();
        segunda.click();

        boolean estadoFinal =
                !primeira.isSelected() && segunda.isSelected();

        return estadoInicial && estadoFinal;
    }

    // CT10 - Radio buttons
    static boolean ct10() {

        driver.get(FORM);

        WebElement padrao =
                driver.findElement(By.id("my-radio-1"));

        WebElement marcado =
                driver.findElement(By.id("my-radio-2"));

        padrao.click();

        return padrao.isSelected()
                && !marcado.isSelected();
    }
}
