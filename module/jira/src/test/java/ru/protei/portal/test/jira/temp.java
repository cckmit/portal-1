package ru.protei.portal.test.jira;

import org.junit.Test;
import ru.protei.portal.core.utils.JiraUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.protei.portal.core.model.helper.CaseCommentUtils.makeJiraImageString;
import static ru.protei.portal.core.utils.JiraUtils.parseImageNode;

public class temp {

    @Test
    public void testRegexp() {
        String extLink = "20200416/11777";
        String str = "! отсудой коммент !\n" +
                "\n" +
                "!min_creepy_duck.jpeg!\n" +
                "\n" +
                "!connected_duck.jpeg!\n" +
                "\n" +
                "!image.png!\n" +
                "\n" +
                "ААА !!! ААА";
//        Pattern p = Pattern.compile("(^|\\s)![^!\\t\\n\\r]*[!]($|\\s)");
//        Pattern p = Pattern.compile("![^!\\t\\n\\r(\\.|())]*!");
        Pattern p = Pattern.compile("((?<![\\p{L}\\p{Nd}\\\\])|(?<=inltokxyzkdtnhgnsbdfinltok))\\!([^\\s\\!]((?!\\!)[\\p{L}\\p{Nd}\\p{Z}\\p{S}\\p{M}\\p{P}]*?[^\\s\\!])?)(?<!\\\\)\\!((?![\\p{L}\\p{Nd}])|(?=inltokxyzkdtnhgnsbdfinltok))");
        Matcher m = p.matcher(str);
        String result;
        if (!m.find()) {
            result = str;
        } else {
            StringBuffer buffer = new StringBuffer();
            int mark = 0;

            do {
                buffer.append(str, mark, m.start());
                mark = m.end();
                String originalString = m.group(2);
                JiraUtils.ImageNode node = parseImageNode(originalString);

                if (node != null) {
                    String str1 = makeJiraImageString(extLink, node.filename);
                    System.out.println(str1);
                    buffer.append(str1);
                } else {
                    buffer.append('!').append(originalString).append('!');
                }
            } while(m.find());

            result =  buffer.append(str, mark, str.length()).toString();
        }

        System.out.println(result);

//        int last = 0;
//        while (m.find(last)) {
//            String group = m.group();
//            JiraUtils.ImageNode imageNode = parseImageNode(group);
//            String imageString = makeJiraImageString(extLink, imageNode.link);
//            System.out.println( imageString );
//            last = m.start() + group.lastIndexOf("!")+1;
//        }
    }

    @Test
    public void testDupRegexp() {
        String extLink = "20200416/11777";
        String str = "!кинг      3.jpeg!!кинг      3.jpeg!!кинг      3.jpeg!";
        Pattern p = Pattern.compile("((?<![\\p{L}\\p{Nd}\\\\])|(?<=inltokxyzkdtnhgnsbdfinltok))\\!([^\\s\\!]((?!\\!)[\\p{L}\\p{Nd}\\p{Z}\\p{S}\\p{M}\\p{P}]*?[^\\s\\!])?)(?<!\\\\)\\!((?![\\p{L}\\p{Nd}])|(?=inltokxyzkdtnhgnsbdfinltok))");

//        Pattern p = Pattern.compile("![^!\\t\\n\\r]*!");
        Matcher m = p.matcher(str);
        String result;
        if (!m.find()) {
            result = str;
        } else {
            StringBuffer buffer = new StringBuffer();
            int mark = 0;

            do {
                buffer.append(str, mark, m.start());
                mark = m.end();
                String originalString = m.group(2);
                JiraUtils.ImageNode node = parseImageNode(originalString);

                if (node != null) {
                    String str1 = makeJiraImageString(extLink, node.filename);
                    System.out.println(str1);
                    buffer.append(str1);
                } else {
                    buffer.append('!').append(originalString).append('!');
                }
            } while(m.find());

            result =  buffer.append(str, mark, str.length()).toString();
        }

        System.out.println(result);
    }

    @Test
    public void testBigRegexp() {
        String extLink = "20200416/11777";
        Pattern p = Pattern.compile("((?<![\\p{L}\\p{Nd}\\\\])|(?<=inltokxyzkdtnhgnsbdfinltok))\\!([^\\s\\!]((?!\\!)[\\p{L}\\p{Nd}\\p{Z}\\p{S}\\p{M}\\p{P}]*?[^\\s\\!])?)(?<!\\\\)\\!((?![\\p{L}\\p{Nd}])|(?=inltokxyzkdtnhgnsbdfinltok))");
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
                "!2020-05/23442.png|alt=image-2020-05-20-10-01-25-377.png!\n" +
                "\n" +
                " \n" +
                "\n" +
                "2. \n" +
                "\n" +
                "_-- Время на MSC, SCP и OCS могут различаться_\n" +
                "\n" +
                "Ранее я уже приводил выдержку из трейса с указанием времени по локального MSC, а не иным точкам:\n" +
                "\n" +
                "!2020-05/23444.png|alt=image-2020-05-20-10-07-01-223.png!\n" +
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
                "!image-2020-         asd as d32 e23er 32re32 e r32er32 05-20-10-46-59-591.png!\n" +
                "\n" +
                " \n" +
                "\n" +
                "PS ** если есть трейс по одному \"проблемному\" вызову - то есть методика получения обмена.\n" +
                "\n" +
                "Прошу Вас предоставить еще несколько вариантов из указанных в обращении для выполнения детального анализа.";
        Matcher m = p.matcher(str);
        String result;
        if (!m.find()) {
            result = str;
        } else {
            StringBuffer buffer = new StringBuffer();
            int mark = 0;

            do {
                buffer.append(str, mark, m.start());
                mark = m.end();
                String originalString = m.group(2);
                JiraUtils.ImageNode node = parseImageNode(originalString);

                if (node != null) {
                    String str1 = makeJiraImageString(extLink, node.filename);
                    System.out.println(str1);
                    buffer.append(str1);
                } else {
                    buffer.append('!').append(originalString).append('!');
                }
            } while(m.find());

            result =  buffer.append(str, mark, str.length()).toString();
        }

        System.out.println(result);
    }
}
