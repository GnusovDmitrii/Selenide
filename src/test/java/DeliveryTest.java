import com.codeborne.selenide.ElementsCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class DeliveryTest {
    public String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999/");
        $("[data-test-id='date'] input").sendKeys(Keys.CONTROL, "a", Keys.DELETE);
    }

    @Test
    void shouldValidValues() {
        $("[data-test-id='city'] input").val("Москва");
        $("[data-test-id='date'] input").val(generateDate(3));
        $("[data-test-id='name'] input").val("Петр Петров");
        $("[data-test-id='phone'] input").val("+79001234567");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $("[data-test-id='notification'] .notification__title").shouldBe(text("Успешно!"), Duration.ofSeconds(15));
        $("[data-test-id='notification'] .notification__content").shouldBe(text("Встреча успешно забронирована на " + generateDate(3)));
    }

    @Test
    void shouldInvalidValueOfTheCity() {
        $("[data-test-id='city'] input").val("Таганрог");
        $("[data-test-id='date'] input").val(generateDate(3));
        $("[data-test-id='name'] input").val("Петр Петров");
        $("[data-test-id='phone'] input").val("+79001234567");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $("[data-test-id='city'].input_invalid .input__sub").shouldHave(ownText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldInvalidTimeValue() {
        $("[data-test-id='city'] input").val("Москва");
        $("[data-test-id='date'] input").val(generateDate(0));
        $("[data-test-id='name'] input").val("Вася Пупкин");
        $("[data-test-id='phone'] input").val("+79001234567");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $("[data-test-id='date'] .input_invalid .input__sub").shouldHave(ownText("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldInvalidNameValue() {
        $("[data-test-id='city'] input").val("Москва");
        $("[data-test-id='date'] input").val(generateDate(3));
        $("[data-test-id='name'] input").val("Petr Petrov");
        $("[data-test-id='phone'] input").val("+79001234567");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        // Исправлено: указаные (с одной 'н') - как в реальном сообщении
        $("[data-test-id='name'].input_invalid .input__sub").shouldHave(ownText("Имя и Фамилия указаные неверно."));
    }

    @Test
    void shouldInvalidPhoneValue() {
        $("[data-test-id='city'] input").val("Москва");
        $("[data-test-id='date'] input").val(generateDate(3));
        $("[data-test-id='name'] input").val("Петр Петров");
        $("[data-test-id='phone'] input").val("79001234567");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $("[data-test-id='phone'].input_invalid .input__sub").shouldHave(ownText("Телефон указан неверно."));
    }

    @Test
    void shouldUncheckedCheckBox() {
        $("[data-test-id='city'] input").val("Москва");
        $("[data-test-id='date'] input").val(generateDate(3));
        $("[data-test-id='name'] input").val("Петр Петров");
        $("[data-test-id='phone'] input").val("+79001234567");
        $(".button").click();
        $("[data-test-id='agreement'].input_invalid .checkbox__text").shouldHave(ownText("Я соглашаюсь с условиями обработки"));
    }

    @Test
    void shouldDropdownListOfCities() {
        $("[data-test-id='city'] input").val("Мо");
        ElementsCollection listOfCities = $$("[class='popup__container'] span");
        listOfCities.findBy(text("Москва")).click();
        $("[data-test-id='date'] input").val(generateDate(3));
        $("[data-test-id='name'] input").val("Петр Петров");
        $("[data-test-id='phone'] input").val("+79001234567");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $("[data-test-id='notification'] .notification__title").shouldBe(text("Успешно!"), Duration.ofSeconds(15));
        $("[data-test-id='notification'] .notification__content").shouldBe(text("Встреча успешно забронирована на " + generateDate(3)));
    }

    @Test
    void shouldDropDownCalendar() {
        $("[data-test-id='city'] input").val("Москва");
        $("[data-test-id='date'] input").click();
        ElementsCollection dates = $$("[class='popup__container'] [data-day]");

        String dayOfMonth = LocalDate.now().plusDays(14).format(DateTimeFormatter.ofPattern("d"));
        dates.findBy(exactText(dayOfMonth)).click();

        $("[data-test-id='name'] input").val("Петр Петров");
        $("[data-test-id='phone'] input").val("+79001234567");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $("[data-test-id='notification'] .notification__title").shouldBe(text("Успешно!"), Duration.ofSeconds(15));
        $("[data-test-id='notification'] .notification__content").shouldBe(text("Встреча успешно забронирована на " + generateDate(14)));
    }

    @Test
    void shouldEmptyCityField() {
        $("[data-test-id='date'] input").val(generateDate(3));
        $("[data-test-id='name'] input").val("Петр Петров");
        $("[data-test-id='phone'] input").val("+79001234567");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $("[data-test-id='city'].input_invalid .input__sub").shouldHave(ownText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldEmptyDateField() {
        $("[data-test-id='city'] input").val("Москва");
        $("[data-test-id='name'] input").val("Петр Петров");
        $("[data-test-id='phone'] input").val("+79001234567");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        // Исправлено: убран .input_invalid из селектора
        $("[data-test-id='date'] .input__sub").shouldHave(ownText("Неверно введена дата"));
    }

    @Test
    void shouldEmptyNameField() {
        $("[data-test-id='city'] input").val("Москва");
        $("[data-test-id='date'] input").val(generateDate(3));
        $("[data-test-id='phone'] input").val("+79001234567");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $("[data-test-id='name'].input_invalid .input__sub").shouldHave(ownText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldEmptyPhoneField() {
        $("[data-test-id='city'] input").val("Москва");
        $("[data-test-id='date'] input").val(generateDate(3));
        $("[data-test-id='name'] input").val("Петр Петров");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(".button").click();
        $("[data-test-id='phone'].input_invalid .input__sub").shouldHave(ownText("Поле обязательно для заполнения"));
    }
}