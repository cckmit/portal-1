package ru.protei.portal.test.youtrack;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.protei.portal.core.model.helper.StringUtils.EMPTY;

public class YtSummaryParseTest {

    @Test
    public void parseDlvrySummaryForReport() {
        String description = "### Заявка на отправку\n" +
                "\n" +
                "Просим осуществить прием и перевозку указанного ниже груза.\n" +
                "Точность и достоверность предоставленных сведений гарантируем.\n" +
                "С правилами перевозки Клиент ознакомлен. Обязуемся не отправлять грузы, содержащие опасные и наркотические вещества, а также предметы, принятие к Перевозке которых запрещено Законами РФ.\n" +
                "\n" +
                "**Данные отправителя**\n" +
                "\n" +
                "|  |  |\n" +
                "| --- | --- |\n" +
                "| **Отправитель** | ООО \"ПРОТЕЙ СТ\" |\n" +
                "| **Адрес отправителя** | 194044, Санкт-Петербург, Б. Сампсониевский пр. д.60 литера А, БЦ «Телеком» |\n" +
                "| **Контакты отправителя** | Бруцкая Анастасия (812) 449-4727, доб. 5-485 |\n" +
                "| **Дополнительная информация** |**Готовность 12-00**|\n" +
                "\n" +
                "**Данные получателя**\n" +
                "\n" +
                "|  |  |\n" +
                "| --- | --- |\n" +
                "| **Получатель** | АО «Технологии радиоконтроля» |\n" +
                "| **Адрес получателя** | 195220, пр. Непокоренных, д. 17, корп. 4, литер В |\n" +
                "| **Контакты получателя** | Малинин Ярослав Борисович, - склад (812) 244-33-21 доб. 2719, моб. 911-706-57-08 |\n" +
                "| **Дополнительная информация** |**Доставить до 13:00.**  Время работы с 09:00 до 18:00 (**пятница до 17:00**), обед 13:00-14:00.  |\n" +
                "\n" +
                "**Характеристики груза**\n" +
                "\n" +
                "|  |  |\n" +
                "| --- | --- |\n" +
                "| **Наименование** | Заявка № 43Г: Cometa3.1А 590.008.025, PSM1.2 134.008.183  |\n" +
                "| **Наличие в грузе АКБ/ИБП** | - |\n" +
                "| **Модель АКБ/ИБП** | - |\n" +
                "| **Количество мест** | 1 |\n" +
                "| **Общий вес, кг** | 1 кг. |\n" +
                "| **Габариты, см** | 43х31х15|\n" +
                "| **Стоимость груза, руб.** | - |\n" +
                "| **Страхование груза    (да, нет)** | нет |\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Подпись Клиента ____________________ /__________________________/\n" +
                "подпись                            (Ф.И.О.)\n" +
                "М.П.\n";

        String desc = getFieldValueByKeyword(description, "Наименование");
        Assert.assertEquals(desc, " Заявка № 43Г: Cometa3.1А 590.008.025, PSM1.2 134.008.183  ");
        String from = getFieldValueByKeyword(description, "Отправитель");
        Assert.assertEquals(from, " ООО \"ПРОТЕЙ СТ\" ");
        String to = getFieldValueByKeyword(description, "Получатель");
        Assert.assertEquals(to, " АО «Технологии радиоконтроля» ");
        String toAddr = getFieldValueByKeyword(description, "Адрес получателя");
        Assert.assertEquals(toAddr, " 195220, пр. Непокоренных, д. 17, корп. 4, литер В ");
    }

    private String getFieldValueByKeyword(String value, String keyword) {
        String regex = "^\\|.*" + keyword + ".*\\|([^\\n]+)\\|\\n*$";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            return matcher.group(1);
        }

        return EMPTY;
    }
}
