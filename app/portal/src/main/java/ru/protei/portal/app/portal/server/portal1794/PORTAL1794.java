package ru.protei.portal.app.portal.server.portal1794;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.YtSingleEnumIssueCustomField;
import ru.protei.portal.core.model.youtrack.dto.issue.IssueWorkItem;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.text.SimpleDateFormat;
import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.mergeMap;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;

public class PORTAL1794 {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);

        YoutrackApi api = ctx.getBean(YoutrackApi.class);
        PersonDAO personDAO = ctx.getBean(PersonDAO.class);

        Map<String, List<String>> researchesMap = new HashMap<>();
        researchesMap.put("Программа автоматического детектирования и распознавания автомобильных регистрационных знаков", Arrays.asList("EREQUESTS"));
        researchesMap.put("Программное обеспечение \"Детектор человека\"", Arrays.asList("VAD", "VP"));
        researchesMap.put("Программное обеспечение \"Детектор транспорта\"", Arrays.asList("VAD", "VP"));
        researchesMap.put("Многофункциональный комплекс связи «Гелиос-5»", Arrays.asList("HWGELIOS", "HWD", "HWS", "HWT", "QAH", "HW_EQ"));
        researchesMap.put("Программа сервера приложений TAS AS для реализации услуги PUSH-TO-TALK для критических применений (MCPTT AS)", Arrays.asList("MCPTT", "TAS"));
        researchesMap.put("программное обеспечение Системы PROTEI Signaling Firewall.", Arrays.asList("Mobile_SS7FW"));
        researchesMap.put("Разработка технологий производства и создания оборудования беспроводного широкополосного доступа на основе спецификаций 3GPP LTE " +
                "/ Программное обеспечение «Ядро сети + МСРТТ» (для Минпромторг)", Arrays.asList("MCPTT", "TAS"));
        researchesMap.put("Компас", Arrays.asList("eth_switch"));
        researchesMap.put("ПО для построения корпоративных услуг Unified Communication", Arrays.asList("UC"));
        researchesMap.put("ПО «AMF, AUSF, UDM/UDR»", Arrays.asList("Mobile_AUSF", "Mobile_UDM", "AMF"));
        researchesMap.put("ПО «SMF/UPF, NRF»", Arrays.asList("MobileCorePacketTester"));
        researchesMap.put("ПО «PCF»", Arrays.asList("PCRF"));
        researchesMap.put("Программное обеспечение \"Платформа Видеоаналитики\"", Arrays.asList("VAD", "VP"));
        researchesMap.put("ПО Protei TAS", Arrays.asList("MKD", "TAS", "ATE"));
        researchesMap.put("ПО Protei SCC-AS", Arrays.asList("SCC-AS", "TAS"));
        Map<String, List<String>> invertResearchesMap = inverseMap(researchesMap);

        Map<String, List<String>> softMap = new HashMap<>();
        softMap.put("СПО КТСО МУССОН", Arrays.asList("MC", "PHMONSOON", "HWMONSOON", "HWR", "EQA"));
        softMap.put("СПО \"Кругозор\"", Arrays.asList("VAD"));
        softMap.put("Специальное ПО Безопасный город", Arrays.asList("EAS", "AT", "VAD", "VP", "ER", "WP", "CC", "SB", "SN", "PNR", "CPE", "Mobile_SMSC", "DAD_DDF",
                "DAD_DAD", "DAD_EXP", "EWC", "POST", "sppr", "SC_int", "DAD_PKN", "DA", "GB", "EQA"));
        softMap.put("Система перехвата TDM трафика. LIME1", Arrays.asList("LI", "EMSC", "PHYSICAL", "ITG", "SNGI"));
        softMap.put("Система обработки информации о чрезвычайных ситуациях", Arrays.asList("ER", "CC", "SB", "SN", "TLG", "TM", "EACD", "RFW", "CPE", "PNR", "SNGI", "EQA"));
        softMap.put("Система мониторинга сигнальных каналов", Arrays.asList("Mobile_SigMonitor"));
        softMap.put("Система законного перехвата IP трафика. LIS", Arrays.asList("SORM", "LIS"));
        softMap.put("Программный модуль GTP-Probe", Arrays.asList("PLP"));
        softMap.put("Программное обеспечение Компонентов ядра сети IMS - PROTEI IMS Core", Arrays.asList("PCSCF", "ICSCF", "SCSCF", "SCCAS", "CLI", "IMST"));
        softMap.put("Программное обеспечение PROTEI EPC", Arrays.asList("MME", "SGW", "PDN", "GGSN", "Mobile_HLR", "PCRF", "CPE"));
        softMap.put("Программная подсистема обработки пакетного трафика", Arrays.asList("DPI", "DS", "DPI_TO"));
        softMap.put("Программа Центра обслуживания вызовов \"ПРОТЕЙ\"", Arrays.asList("CC", "SB", "SN", "TLG", "EACD", "RFW", "CPE"));
        softMap.put("Программа Системы глубокого анализа и применения политик для управления пакетным трафиком PROTEI_DPI", Arrays.asList("DPI", "DS", "DPI_TO"));
        softMap.put("Программа преобразований речевой и сигнальной информации Шлюза PRIN", Arrays.asList("HWR"));
        softMap.put("Программа комплекса ПРОТЕЙ-ВКС", Arrays.asList("MVP", "VCST", "VCSM", "VCSS", "CRS", "DBRL", "GB"));
        softMap.put("Программа для ЭВМ \"Система управления подключенными устройствами PROTEI M2M/C2M\"", Arrays.asList("GEO", "OB"));
        softMap.put("Программа для ЭВМ \"Домашний регистр местоположения/регистр абонентских данных PROTEI HLR/HSS (Версия 2)", Arrays.asList("Mobile_HLR"));
        softMap.put("Программа для ЭВМ \"Автоматизированная система расчетов PROTEI OCS\"", Arrays.asList("OB", "billing-qa"));
        softMap.put("ПО Узла PROTEI SCP", Arrays.asList("Mobile_CAPL"));
        softMap.put("ПО Системы PROTEI Signaling Firewall", Arrays.asList("Mobile_SS7FW"));
        softMap.put("ПО Система-112", Arrays.asList("ER", "CC", "SB", "SN", "TLG", "TM", "EACD", "RFW", "CPE", "SPLSIP", "GB", "EQA"));
        softMap.put("ПО Комплекса управления роумингом", Arrays.asList("RG", "GLR", "Mobile_MI", "WSMS"));
        softMap.put("ПО Комплекса управления дополнительными услугами", Arrays.asList("xVLR", "Mobile_Bulk", "SNGI"));
        softMap.put("ПО комплекса предоставления услуг Messaging (SMSC/USSDC/SMS Firewall/IP-SM-GW)", Arrays.asList("Mobile_SMSC", "Mobile_SMSFW", "Mobile_IPSMGW", "Mobile_SCL", "CBC", "SGW"));
        softMap.put("ПО комплекса автоматизации обработки вызовов экстренных оперативных служб по единому номеру 112", Arrays.asList("ER", "WP", "CC", "SB", "SN", "TLG", "TM", "EACD", "RFW", "CPE", "SPLSIP"));
        softMap.put("ПО комплекса предоставления услуг роуминга с использованием технологии Multi-IMSI", Arrays.asList("Mobile_MI"));
        softMap.put("ПО \"ПРОТЕЙ-GMSC\"", Arrays.asList("GMSC", "SSW4"));
        softMap.put("ПО \"Мониторинг потенциально опасных объектов\"", Arrays.asList("EAS"));
        softMap.put("ПК \"Протей-imSwitch\"", Arrays.asList("MKD", "MKD-Test"));
        softMap.put("ПК \"МАК\"", Arrays.asList("MAK", "ITG", "ITG.UI"));
        softMap.put("ПК \"ИП \"Протей\"", Arrays.asList("SB", "SN", "VO", "CPE", "PNR", "RP", "IVR", "PRBT", "pstorage", "TSIM", "VOT", "SDP", "TK", "SPLSIP"));
        softMap.put("ПК \"Единый центр оперативного реагирования\"", Arrays.asList("ER"));
        softMap.put("Пакетный шлюз PROTEI GGSN/PDN-GW", Arrays.asList("GGSN", "Mobile_HLR", "SGW"));
        softMap.put("Программа системы видеонаблюдения Видеопортал", Arrays.asList("VP", "CRS"));
        softMap.put("WIX", Arrays.asList("Mobile_WIX"));

        Map<String, List<String>> invertSoftMap = inverseMap(softMap);

        try {
//            String query = String.format(" work date: %s .. %s work author: porubov", dateToYtString(new Date(2021-1900, Calendar.JUNE, 28)), dateToYtString(new Date(2021-1900, Calendar.JUNE, 28)));
//            String query = "";
//            Result<List< YtActivityItem >> activities = api.getActivities(
//                    new Date(2021-1900, Calendar.JUNE, 1),
//                    new Date(2021-1900, Calendar.JUNE, 30),
//                    0L, 1000L,
//                    YtActivityCategory.WorkItemDurationCategory
//                    );
            Result<List<IssueWorkItem>> result = api.getWorkItems(
                    new Date(2021-1900, Calendar.JUNE, 20),
                    new Date(2021-1900, Calendar.JUNE, 30),
                    0L, 2000L
            );
            HashMap<String, PersonWithWorkItem> data = result.map(issueWorkItems -> stream(issueWorkItems)
                    .map(issueWorkItem -> {
                        PersonQuery query = new PersonQuery();
                        String email = issueWorkItem.author.email;
                        Person person = null;
                        if (isNotEmpty(email)) {
                            query.setEmail(email);
                            List<Person> persons = personDAO.getPersons(query);
                            if (persons.size() == 1) {
                                person = persons.get(0);
                                person.setLogins(Collections.singletonList(email));
                            }
                        }
                        return new PersonWithWorkItem2(person != null ? person : createTempPerson(email),
                                issueWorkItem.issue.idReadable,
                                getCustomerName(issueWorkItem.issue),
                                issueWorkItem.duration.minutes.longValue(),
                                issueWorkItem.issue.project.name
                        );
                    }).collect(HashMap::new, (k, v) -> collectItems(invertResearchesMap, invertSoftMap, k, v), PORTAL1794::mergeCollectedItems))
                    .getData();

            data.forEach((k, v) -> System.out.println(v));

//            issueReports.getData().forEach(issue -> System.out.println(makeInfo(issue)));
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            ctx.destroy();
        }
    }

    static String getCustomerName(YtIssue issue) {
        YtSingleEnumIssueCustomField field = (YtSingleEnumIssueCustomField) issue.getCustomerField();
        if (field == null) {
            return null;
        }
        return field.getValueAsString();
    }

    static Person createTempPerson(String email) {
        Person person = new Person();
        person.setLogins(Collections.singletonList(email));
        return person;
    }

    static void collectItems(Map<String, List<String>> invertResearchesMap, Map<String, List<String>> invertSoftMap,
                             HashMap<String, PersonWithWorkItem> map, PersonWithWorkItem2 item2) {
        PersonWithWorkItem personWithWorkItem = map.compute(item2.person.getLogins().get(0),
                (key, value) -> value != null ? value : new PersonWithWorkItem(item2.person));
        switch(selectWorkType(invertResearchesMap, invertSoftMap, item2.customer, item2.issue, item2.project)) {
            case NIOKR: personWithWorkItem.niokrSpentTime.merge( item2.project, item2.spentTime, Long::sum); break;
            case NMA: personWithWorkItem.nmaSpentTime.merge( item2.project, item2.spentTime, Long::sum); break;
            case CONTRACT: personWithWorkItem.contractSpentTime.merge( item2.project, item2.spentTime, Long::sum); break;
        }
        personWithWorkItem.issueSpentTime.merge( item2.issue, item2.spentTime, Long::sum);
    }

    static WorkType selectWorkType(Map<String, List<String>> invertResearchesMap, Map<String, List<String>> invertSoftMap,
                                   String customer, String issueName, String project) {
        if (isHomeCompany(customer)) {
            if (invertResearchesMap.containsKey(project)) {
                return WorkType.NIOKR;
            }
            if (invertSoftMap.containsKey(project)) {
                return WorkType.NMA;
            }
            return WorkType.NIOKR;
        } else {
            return WorkType.CONTRACT;
        }
    }

    static <K, V> Map<V, List<K>> inverseMap(Map<K, List<V>> map) {
        Map<V, List<K>> inverseMap = new HashMap<>();
        map.forEach((k, v) -> {
            v.forEach(value -> {
                inverseMap.compute(value, (k2, v2) -> {
                    if (v2 == null) v2 = new ArrayList<>();
                    v2.add(k);
                    return v2;
                });
            });
        });
        return inverseMap;
    }

    static void mergeCollectedItems(HashMap<String, PersonWithWorkItem> map1, HashMap<String, PersonWithWorkItem> map2) {
        mergeMap(map1, map2, (value1, value2) -> {
            mergeMap(value1.niokrSpentTime, value2.niokrSpentTime, Long::sum);
            mergeMap(value1.nmaSpentTime, value2.nmaSpentTime, Long::sum);
            mergeMap(value1.contractSpentTime, value2.contractSpentTime, Long::sum);
            mergeMap(value1.issueSpentTime, value2.issueSpentTime, Long::sum);
            return value1;
        });
    }

    static class PersonWithWorkItem2 {
        Person person;
        String issue;
        String customer;
        Long spentTime;
        String project;

        public PersonWithWorkItem2(Person person, String issue, String customer, Long spentTime, String project) {
            this.person = person;
            this.issue = issue;
            this.customer = customer;
            this.spentTime = spentTime;
            this.project = project;
        }
    }

    enum WorkType {
        NIOKR, NMA, CONTRACT
    }

    static class PersonWithWorkItem {
        Person person;
        Map<String, Long> issueSpentTime;
        Map<String, Long> niokrSpentTime;
        Map<String, Long> nmaSpentTime;
        Map<String, Long> contractSpentTime;

        public PersonWithWorkItem(Person person) {
            this.person = person;
            this.issueSpentTime = new HashMap<>();
            this.niokrSpentTime = new HashMap<>();
            this.nmaSpentTime = new HashMap<>();
            this.contractSpentTime = new HashMap<>();
        }

        @Override
        public String toString() {
            return "PersonWithWorkItem{" +
                    "person=" + person +
                    ", niokrSpentTime=" + niokrSpentTime +
                    ", nmaSpentTime=" + nmaSpentTime +
                    ", contractSpentTime=" + contractSpentTime +
                    '}';
        }
    }

    static String dateToYtString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    static String makeInfo(YtIssue issue) {
        return String.format("id = %s, idReadable = %s, summary = %s, client = %s",
                issue.id,
                issue.idReadable,
                issue.summary,
                stream(issue.customFields).filter(field -> "Заказчик".equals(field.name))
                        .findAny()
                        .map(field -> ((YtSingleEnumIssueCustomField)field).value)
                        .filter(Objects::nonNull)
                        .map(field -> field.name).orElse("no client"));
    }

    static Set<String> homeCompany = new HashSet<>(Arrays.asList(null, "НТЦ Протей", "Нет заказчика", "Протей СТ"));
    static boolean isHomeCompany(String company) {
        return homeCompany.contains(company);
    }

    static model.WorkType makeWorkType(String workType, String type, String workBase) {
        switch (workType) {
            case "Баг": return model.WorkType.BUG;
            case "Заказчик": return model.WorkType.CUSTOMER;
            case "Инфраструктура": return model.WorkType.ENVIRONMENT;
            case "Развитие": return model.WorkType.PRODUCT;
            case "Улучшение": return model.WorkType.TECH;
            case "Разное": return model.WorkType.ETC;
            default: break;
        }

        if ("Bug".equals(type))  {
            return model.WorkType.BUG;
        }

        switch (workBase) {
            case "Плановое развитие": return model.WorkType.PRODUCT;
            case "* Совещания, управление и пр.": return model.WorkType.ETC;
            case "Рефакторинг": return model.WorkType.TECH;
            case "Контракт (в соответствии с пунктом ТЗ)": return model.WorkType.CUSTOMER;
            case "Инфраструктура (в том числе для ТП)": return model.WorkType.ENVIRONMENT;
            case "Квота Менеджера": return model.WorkType.CUSTOMER;
            case "Контракт (пункт ТЗ)": return model.WorkType.CUSTOMER;
            case "Квота Проекта": return model.WorkType.CUSTOMER;
            case "Демонстрация": return model.WorkType.ETC;
            default: break;
        }

        return model.WorkType.UNKNOWN;
    }
}
