package ru.protei.portal.test.jira;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;

import java.util.*;

import static ru.protei.portal.core.utils.JiraUtils.*;

public class JiraImageReplacement {

    @Test
    public void toJira() {
        String str = "! отсудой коммент !\n" +
                "\n" +
                "!20200416/11777.jpeg!\n" +
                "\n" +
                "ААА !!! ААА";

        Attachment a = new Attachment();
        a.setFileName("min_creepy_duck.jpeg");
        a.setExtLink("20200416/11777.jpeg");

        String textWithReplacedImages = getTextWithReplacedImagesToJira(str, Collections.singletonList(a));

        Assert.assertEquals(
                "! отсудой коммент !\n" +
                        "\n" +
                        "!min_creepy_duck.jpeg|alt=min_creepy_duck.jpeg!\n" +
                        "\n" +
                        "ААА !!! ААА",
                textWithReplacedImages);
    }

    @Test
    public void fromJiraDescription() {
        String str = "Description " +
                "!min_creepy_duck.jpeg!\n" +
                "ААА !!! ААА";

        Attachment a = new Attachment();
        a.setFileName("min_creepy_duck.jpeg");
        a.setExtLink("20200416/11777");

        String descriptionWithReplacedImagesFromJira = getDescriptionWithReplacedImagesFromJira(str, Arrays.asList(a));

        Assert.assertEquals(
                "Description !20200416/11777|alt=min_creepy_duck.jpeg!\n" +
                        "ААА !!! ААА",
                descriptionWithReplacedImagesFromJira);
    }

    @Test
    public void fromJira() {
        String str = "! отсудой коммент !\n" +
                "\n" +
                "!min_creepy_duck.jpeg!\n" +
                "\n" +
                "ААА !!! ААА";
        CaseComment caseComment = new CaseComment();
        caseComment.setText(str);

        Attachment a = new Attachment();
        a.setFileName("min_creepy_duck.jpeg");
        a.setExtLink("20200416/11777");

        setTextWithReplacedImagesFromJira(caseComment, Arrays.asList(a), new ArrayList<>());

        Assert.assertEquals(
                "! отсудой коммент !\n" +
                        "\n" +
                        "!20200416/11777|alt=min_creepy_duck.jpeg!\n" +
                        "\n" +
                        "ААА !!! ААА",
                caseComment.getText());
    }

    @Test
    public void fromJiraDuplicate() {
        String str = "!кинг      3.jpeg!!кинг      3.jpeg!!кинг      3.jpeg!";

        CaseComment caseComment = new CaseComment();
        caseComment.setText(str);

        Attachment a = new Attachment();
        a.setFileName("кинг      3.jpeg");
        a.setExtLink("20200416/11777");

        setTextWithReplacedImagesFromJira(caseComment, Arrays.asList(a), new ArrayList<>());

        Assert.assertEquals(
                "!20200416/11777|alt=кинг      3.jpeg!!20200416/11777|alt=кинг      3.jpeg!!20200416/11777|alt=кинг      3.jpeg!",
                caseComment.getText());
    }

    @Test
    public void fromJiraBig() {
        String str = "_commented by Pisarets, Anton - 20/May/20 3:50 AM_\n" +
                "[~Pavel.Kacharava]\n" +
                "\n" +
                "1.\n" +
                "\n" +
                "_-- BRT - это проприетарный протокол_\n" +
                "\n" +
                " \n" +
                "\n" +
                "В нотации компании Nexign - BRT является сервером контроля балансов и управления услугами в режиме реального времени.\n" +
                "\n" +
                "В штатном решении BRT сервер по связан с Camel-GW, который в конфигурации BRT представлен SCP-точкой, а попротолу Diameter с оборудованием по передачи данных в виде CTF-точки.\n" +
                "\n" +
                " \n" +
                "\n" +
                "_-- Без этого сделать никаких корректных выводов по BRT обмену вы не сможете_\n" +
                "\n" +
                "Тем самым вы хотите сказать, что приведенные мной выдержки из трейса являются \"некорректно\" дешифрованными???\n" +
                "\n" +
                "Как я вижу, в них явно указано, какие протоколы используются и для каких сообщений:\n" +
                "\n" +
                "!image-2020-05-20-10-01-25-377.png!\n" +
                "\n" +
                " \n" +
                "\n" +
                "2. \n" +
                "\n" +
                "_-- Время на MSC, SCP и OCS могут различаться_\n" +
                "\n" +
                "Ранее я уже приводил выдержку из трейса с указанием времени по локального MSC, а не иным точкам:\n" +
                "\n" +
                "!image-2020-05-20-10-07-01-223.png!\n" +
                "\n" +
                "что дает 2020-05-12 20:54:05.\n" +
                "\n" +
                "И странно, что номера абонентов А и В удачно дешифорваны ... и тело сообщения по протоколу Camelv2.\n" +
                "\n" +
                " \n" +
                "\n" +
                "_-- обычно \"временем совершения вызова\"_\n" +
                "\n" +
                "У биллинговой системы есть жесткие рамки, в которые она должна уложится при расчете он-лайн вызова - это 2 секунды.\n" +
                "\n" +
                "После превышения этого времени в лог BRT выводится сообщение, что превышего время расчета - сессия Expired ... и со стороны сетевого задействуется сценарий по умолчанию для такого случая.\n" +
                "\n" +
                "В этой связи 20:54:05 + 2 секунды дает 20:56:05 - но повторюсь: в настоящее время оба BRT (master&slave) работают в режиме повышенного логирования - но никаких следов этого вызова (и других из списка) нет в логах (ни по IMSI, ни по msisdn A/B)!\n" +
                "\n" +
                " \n" +
                "\n" +
                "3.\n" +
                "\n" +
                "_-- Насколько мне известно, BRT инициирует процедуру списание зарезервированных под вызов ресурсов_\n" +
                "\n" +
                "Для этого биллинговая система должна получить запрос на осуществление он-лайн контроля, но конткретно в данном случае BRT_M/S такого запроса не получили:\n" +
                " * BRT_Master назначен IP-адрес 172.17.10.3 и, как следует из трейса, никаких запросов по протоколу Camel на данный адрес не поступает - Camel.InitialDP доходит только до хоста 172.17.10.41.\n" +
                " * Хост 172.17.10.41 в доступном мне дизайне сети клиента является SCP-1,\n" +
                " * я повторюсь: Вам известно, что это за оборудование с адресом 172.17.10.41? Администрирование его Вы выполняете?\n" +
                "\n" +
                "PS может получиться так, что и хост 172.17.10.41 в Вашей нотации так же является BRT - но это не так. Выдержки из дизайна сети клиента:\n" +
                "\n" +
                "!image-2020-05-20-10-29-48-833.png!\n" +
                "\n" +
                " \n" +
                "\n" +
                "!image-2020-05-20-10-31-51-898.png!\n" +
                "\n" +
                " \n" +
                "\n" +
                "4.\n" +
                "\n" +
                "_-- SCP отправил такой EndReason по причине того, что от MSC завершил tcap-транзакцию в рамках которой осуществлялся CAP диалог между SCP и MSC при нахождении вызова в разговорной фазе_\n" +
                "\n" +
                "Я так то же думал, но согласно информации на биллинговой системе ближайшие вызова:\n" +
                "\n" +
                "!image-2020-05-20-10-38-16-569.png!\n" +
                "\n" +
                "2020-05-12 20:50:07 длительностью 33 секунды, что дает время его окончания 20:50:40, а проблемный вызов случился в 20:54:05, т.е. через 3m25s.\n" +
                "\n" +
                " \n" +
                "\n" +
                "Мое четкое убеждение, что:\n" +
                " * без разбора влияния хоста 172.17.10.41,\n" +
                " * на основе только одного трейса\n" +
                "\n" +
                "данный CLM решить будет сложно.\n" +
                "\n" +
                " \n" +
                "\n" +
                "PS * вывод нескольких процессов командой top с хоста 172.17.10.41:\n" +
                "\n" +
                "!image-2020-05-20-10-46-59-591.png!\n" +
                "\n" +
                " \n" +
                "\n" +
                "PS ** если есть трейс по одному \"проблемному\" вызову - то есть методика получения обмена.\n" +
                "\n" +
                "Прошу Вас предоставить еще несколько вариантов из указанных в обращении для выполнения детального анализа.";

        List<Attachment> list = new ArrayList<>();

        Attachment a = new Attachment();
        a.setFileName("image-2020-05-20-10-01-25-377.png");
        a.setExtLink("20200416/1");
        list.add(a);

        a = new Attachment();
        a.setFileName("image-2020-05-20-10-07-01-223.png");
        a.setExtLink("20200416/2");
        list.add(a);

        a = new Attachment();
        a.setFileName("image-2020-05-20-10-29-48-833.png");
        a.setExtLink("20200416/3");
        list.add(a);

        a = new Attachment();
        a.setFileName("image-2020-05-20-10-31-51-898.png");
        a.setExtLink("20200416/4");
        list.add(a);

        a = new Attachment();
        a.setFileName("image-2020-05-20-10-38-16-569.png");
        a.setExtLink("20200416/5");
        list.add(a);

        a = new Attachment();
        a.setFileName("image-2020-05-20-10-46-59-591.png");
        a.setExtLink("20200416/6");
        list.add(a);

        CaseComment caseComment = new CaseComment();
        caseComment.setText(str);

        setTextWithReplacedImagesFromJira(caseComment, list, new ArrayList<>());

        Assert.assertEquals(
                "_commented by Pisarets, Anton - 20/May/20 3:50 AM_\n" +
                        "[~Pavel.Kacharava]\n" +
                        "\n" +
                        "1.\n" +
                        "\n" +
                        "_-- BRT - это проприетарный протокол_\n" +
                        "\n" +
                        " \n" +
                        "\n" +
                        "В нотации компании Nexign - BRT является сервером контроля балансов и управления услугами в режиме реального времени.\n" +
                        "\n" +
                        "В штатном решении BRT сервер по связан с Camel-GW, который в конфигурации BRT представлен SCP-точкой, а попротолу Diameter с оборудованием по передачи данных в виде CTF-точки.\n" +
                        "\n" +
                        " \n" +
                        "\n" +
                        "_-- Без этого сделать никаких корректных выводов по BRT обмену вы не сможете_\n" +
                        "\n" +
                        "Тем самым вы хотите сказать, что приведенные мной выдержки из трейса являются \"некорректно\" дешифрованными???\n" +
                        "\n" +
                        "Как я вижу, в них явно указано, какие протоколы используются и для каких сообщений:\n" +
                        "\n" +
                        "!20200416/1|alt=image-2020-05-20-10-01-25-377.png!\n" +
                        "\n" +
                        " \n" +
                        "\n" +
                        "2. \n" +
                        "\n" +
                        "_-- Время на MSC, SCP и OCS могут различаться_\n" +
                        "\n" +
                        "Ранее я уже приводил выдержку из трейса с указанием времени по локального MSC, а не иным точкам:\n" +
                        "\n" +
                        "!20200416/2|alt=image-2020-05-20-10-07-01-223.png!\n" +
                        "\n" +
                        "что дает 2020-05-12 20:54:05.\n" +
                        "\n" +
                        "И странно, что номера абонентов А и В удачно дешифорваны ... и тело сообщения по протоколу Camelv2.\n" +
                        "\n" +
                        " \n" +
                        "\n" +
                        "_-- обычно \"временем совершения вызова\"_\n" +
                        "\n" +
                        "У биллинговой системы есть жесткие рамки, в которые она должна уложится при расчете он-лайн вызова - это 2 секунды.\n" +
                        "\n" +
                        "После превышения этого времени в лог BRT выводится сообщение, что превышего время расчета - сессия Expired ... и со стороны сетевого задействуется сценарий по умолчанию для такого случая.\n" +
                        "\n" +
                        "В этой связи 20:54:05 + 2 секунды дает 20:56:05 - но повторюсь: в настоящее время оба BRT (master&slave) работают в режиме повышенного логирования - но никаких следов этого вызова (и других из списка) нет в логах (ни по IMSI, ни по msisdn A/B)!\n" +
                        "\n" +
                        " \n" +
                        "\n" +
                        "3.\n" +
                        "\n" +
                        "_-- Насколько мне известно, BRT инициирует процедуру списание зарезервированных под вызов ресурсов_\n" +
                        "\n" +
                        "Для этого биллинговая система должна получить запрос на осуществление он-лайн контроля, но конткретно в данном случае BRT_M/S такого запроса не получили:\n" +
                        " * BRT_Master назначен IP-адрес 172.17.10.3 и, как следует из трейса, никаких запросов по протоколу Camel на данный адрес не поступает - Camel.InitialDP доходит только до хоста 172.17.10.41.\n" +
                        " * Хост 172.17.10.41 в доступном мне дизайне сети клиента является SCP-1,\n" +
                        " * я повторюсь: Вам известно, что это за оборудование с адресом 172.17.10.41? Администрирование его Вы выполняете?\n" +
                        "\n" +
                        "PS может получиться так, что и хост 172.17.10.41 в Вашей нотации так же является BRT - но это не так. Выдержки из дизайна сети клиента:\n" +
                        "\n" +
                        "!20200416/3|alt=image-2020-05-20-10-29-48-833.png!\n" +
                        "\n" +
                        " \n" +
                        "\n" +
                        "!20200416/4|alt=image-2020-05-20-10-31-51-898.png!\n" +
                        "\n" +
                        " \n" +
                        "\n" +
                        "4.\n" +
                        "\n" +
                        "_-- SCP отправил такой EndReason по причине того, что от MSC завершил tcap-транзакцию в рамках которой осуществлялся CAP диалог между SCP и MSC при нахождении вызова в разговорной фазе_\n" +
                        "\n" +
                        "Я так то же думал, но согласно информации на биллинговой системе ближайшие вызова:\n" +
                        "\n" +
                        "!20200416/5|alt=image-2020-05-20-10-38-16-569.png!\n" +
                        "\n" +
                        "2020-05-12 20:50:07 длительностью 33 секунды, что дает время его окончания 20:50:40, а проблемный вызов случился в 20:54:05, т.е. через 3m25s.\n" +
                        "\n" +
                        " \n" +
                        "\n" +
                        "Мое четкое убеждение, что:\n" +
                        " * без разбора влияния хоста 172.17.10.41,\n" +
                        " * на основе только одного трейса\n" +
                        "\n" +
                        "данный CLM решить будет сложно.\n" +
                        "\n" +
                        " \n" +
                        "\n" +
                        "PS * вывод нескольких процессов командой top с хоста 172.17.10.41:\n" +
                        "\n" +
                        "!20200416/6|alt=image-2020-05-20-10-46-59-591.png!\n" +
                        "\n" +
                        " \n" +
                        "\n" +
                        "PS ** если есть трейс по одному \"проблемному\" вызову - то есть методика получения обмена.\n" +
                        "\n" +
                        "Прошу Вас предоставить еще несколько вариантов из указанных в обращении для выполнения детального анализа.",
                caseComment.getText());
    }
}
