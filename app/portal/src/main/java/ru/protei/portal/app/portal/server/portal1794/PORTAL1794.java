package ru.protei.portal.app.portal.server.portal1794;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.model.dao.ContractDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.YtSingleEnumIssueCustomField;
import ru.protei.portal.core.model.youtrack.dto.issue.IssueWorkItem;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.inverseMap;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;

public class PORTAL1794 {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);

        YoutrackApi api = ctx.getBean(YoutrackApi.class);
        PersonDAO personDAO = ctx.getBean(PersonDAO.class);
        ContractDAO contractDAO = ctx.getBean(ContractDAO.class);

        Map<String, List<String>> niokrs = new HashMap<>();
        niokrs.put("Программа автоматического детектирования и распознавания автомобильных регистрационных знаков", Arrays.asList("EREQUESTS"));
        niokrs.put("Программное обеспечение \"Детектор человека\"", Arrays.asList("VAD", "VP"));
        niokrs.put("Программное обеспечение \"Детектор транспорта\"", Arrays.asList("VAD", "VP"));
        niokrs.put("Многофункциональный комплекс связи «Гелиос-5»", Arrays.asList("HWGELIOS", "HWD", "HWS", "HWT", "QAH", "HW_EQ"));
        niokrs.put("Программа сервера приложений TAS AS для реализации услуги PUSH-TO-TALK для критических применений (MCPTT AS)", Arrays.asList("MCPTT", "TAS"));
        niokrs.put("программное обеспечение Системы PROTEI Signaling Firewall.", Arrays.asList("Mobile_SS7FW"));
        niokrs.put("Разработка технологий производства и создания оборудования беспроводного широкополосного доступа на основе спецификаций 3GPP LTE " +
                "/ Программное обеспечение «Ядро сети + МСРТТ» (для Минпромторг)", Arrays.asList("MCPTT", "TAS"));
        niokrs.put("Компас", Arrays.asList("eth_switch"));
        niokrs.put("ПО для построения корпоративных услуг Unified Communication", Arrays.asList("UC"));
        niokrs.put("ПО «AMF, AUSF, UDM/UDR»", Arrays.asList("Mobile_AUSF", "Mobile_UDM", "AMF"));
        niokrs.put("ПО «SMF/UPF, NRF»", Arrays.asList("MobileCorePacketTester"));
        niokrs.put("ПО «PCF»", Arrays.asList("PCRF"));
        niokrs.put("Программное обеспечение \"Платформа Видеоаналитики\"", Arrays.asList("VAD", "VP"));
        niokrs.put("ПО Protei TAS", Arrays.asList("MKD", "TAS", "ATE"));
        niokrs.put("ПО Protei SCC-AS", Arrays.asList("SCC-AS", "TAS"));
        Map<String, List<String>> invertResearchesMap = inverseMap(niokrs);

        Map<String, List<String>> nmas = new HashMap<>();
        nmas.put("СПО КТСО МУССОН", Arrays.asList("MC", "PHMONSOON", "HWMONSOON", "HWR", "EQA"));
        nmas.put("СПО \"Кругозор\"", Arrays.asList("VAD"));
        nmas.put("Специальное ПО Безопасный город", Arrays.asList("EAS", "AT", "VAD", "VP", "ER", "WP", "CC", "SB", "SN", "PNR", "CPE", "Mobile_SMSC", "DAD_DDF",
                "DAD_DAD", "DAD_EXP", "EWC", "POST", "sppr", "SC_int", "DAD_PKN", "DA", "GB", "EQA"));
        nmas.put("Система перехвата TDM трафика. LIME1", Arrays.asList("LI", "EMSC", "PHYSICAL", "ITG", "SNGI"));
        nmas.put("Система обработки информации о чрезвычайных ситуациях", Arrays.asList("ER", "CC", "SB", "SN", "TLG", "TM", "EACD", "RFW", "CPE", "PNR", "SNGI", "EQA"));
        nmas.put("Система мониторинга сигнальных каналов", Arrays.asList("Mobile_SigMonitor"));
        nmas.put("Система законного перехвата IP трафика. LIS", Arrays.asList("SORM", "LIS"));
        nmas.put("Программный модуль GTP-Probe", Arrays.asList("PLP"));
        nmas.put("Программное обеспечение Компонентов ядра сети IMS - PROTEI IMS Core", Arrays.asList("PCSCF", "ICSCF", "SCSCF", "SCCAS", "CLI", "IMST"));
        nmas.put("Программное обеспечение PROTEI EPC", Arrays.asList("MME", "SGW", "PDN", "GGSN", "Mobile_HLR", "PCRF", "CPE"));
        nmas.put("Программная подсистема обработки пакетного трафика", Arrays.asList("DPI", "DS", "DPI_TO"));
        nmas.put("Программа Центра обслуживания вызовов \"ПРОТЕЙ\"", Arrays.asList("CC", "SB", "SN", "TLG", "EACD", "RFW", "CPE"));
        nmas.put("Программа Системы глубокого анализа и применения политик для управления пакетным трафиком PROTEI_DPI", Arrays.asList("DPI", "DS", "DPI_TO"));
        nmas.put("Программа преобразований речевой и сигнальной информации Шлюза PRIN", Arrays.asList("HWR"));
        nmas.put("Программа комплекса ПРОТЕЙ-ВКС", Arrays.asList("MVP", "VCST", "VCSM", "VCSS", "CRS", "DBRL", "GB"));
        nmas.put("Программа для ЭВМ \"Система управления подключенными устройствами PROTEI M2M/C2M\"", Arrays.asList("GEO", "OB"));
        nmas.put("Программа для ЭВМ \"Домашний регистр местоположения/регистр абонентских данных PROTEI HLR/HSS (Версия 2)", Arrays.asList("Mobile_HLR"));
        nmas.put("Программа для ЭВМ \"Автоматизированная система расчетов PROTEI OCS\"", Arrays.asList("OB", "billing-qa"));
        nmas.put("ПО Узла PROTEI SCP", Arrays.asList("Mobile_CAPL"));
        nmas.put("ПО Системы PROTEI Signaling Firewall", Arrays.asList("Mobile_SS7FW"));
        nmas.put("ПО Система-112", Arrays.asList("ER", "CC", "SB", "SN", "TLG", "TM", "EACD", "RFW", "CPE", "SPLSIP", "GB", "EQA"));
        nmas.put("ПО Комплекса управления роумингом", Arrays.asList("RG", "GLR", "Mobile_MI", "WSMS"));
        nmas.put("ПО Комплекса управления дополнительными услугами", Arrays.asList("xVLR", "Mobile_Bulk", "SNGI"));
        nmas.put("ПО комплекса предоставления услуг Messaging (SMSC/USSDC/SMS Firewall/IP-SM-GW)", Arrays.asList("Mobile_SMSC", "Mobile_SMSFW", "Mobile_IPSMGW", "Mobile_SCL", "CBC", "SGW"));
        nmas.put("ПО комплекса автоматизации обработки вызовов экстренных оперативных служб по единому номеру 112", Arrays.asList("ER", "WP", "CC", "SB", "SN", "TLG", "TM", "EACD", "RFW", "CPE", "SPLSIP"));
        nmas.put("ПО комплекса предоставления услуг роуминга с использованием технологии Multi-IMSI", Arrays.asList("Mobile_MI"));
        nmas.put("ПО \"ПРОТЕЙ-GMSC\"", Arrays.asList("GMSC", "SSW4"));
        nmas.put("ПО \"Мониторинг потенциально опасных объектов\"", Arrays.asList("EAS"));
        nmas.put("ПК \"Протей-imSwitch\"", Arrays.asList("MKD", "MKD-Test"));
        nmas.put("ПК \"МАК\"", Arrays.asList("MAK", "ITG", "ITG.UI"));
        nmas.put("ПК \"ИП \"Протей\"", Arrays.asList("SB", "SN", "VO", "CPE", "PNR", "RP", "IVR", "PRBT", "pstorage", "TSIM", "VOT", "SDP", "TK", "SPLSIP"));
        nmas.put("ПК \"Единый центр оперативного реагирования\"", Arrays.asList("ER"));
        nmas.put("Пакетный шлюз PROTEI GGSN/PDN-GW", Arrays.asList("GGSN", "Mobile_HLR", "SGW"));
        nmas.put("Программа системы видеонаблюдения Видеопортал", Arrays.asList("VP", "CRS"));
        nmas.put("WIX", Arrays.asList("Mobile_WIX"));

        Map<String, List<String>> invertSoftMap = inverseMap(nmas);

        try {
            long start = System.currentTimeMillis();

            Iterator1794 iterator1794 = new Iterator1794((offset, limit) ->
                    api.getWorkItems(new Date(2021 - 1900, Calendar.JUNE, 20), new Date(2021 - 1900, Calendar.JULY, 1), offset, limit),
                    100);

            Collector1794 collector1794 = new Collector1794(
                    invertResearchesMap,
                    invertSoftMap,
                    contractDAO::getByCustomerName,
                    email -> getPersonByEmail(email, personDAO)
            );

            Map<String, ReportDTO> data = stream(iterator1794)
                    .map(PORTAL1794::makeYtReportItem)
                    .collect(collector1794);

            if (iterator1794.getStatus() != En_ResultStatus.OK) {
                System.out.println("error = " + iterator1794.getStatus());
            }

            System.out.println("time = " + (System.currentTimeMillis() - start));

            // вывод как должно выглядеть в отчете
            data.forEach((employee, values) -> {
                StringBuilder sb = new StringBuilder();
                sb.append(employee).append(" = ");
                sb.append("NIOKR : ");
                values.niokrSpentTime.forEach((k, v) -> {
                    sb.append(k).append(" = ").append(v).append("; ");
                });
                sb.append("NMA : ");
                values.nmaSpentTime.forEach((k, v) -> {
                    sb.append(k).append(" = ").append(v).append("; ");
                });
                sb.append("CONTRACT : ");
                values.contractSpentTime.forEach((k, v) -> {
                    sb.append(k).append(" = ").append(v).append("; ");
                });
                System.out.println(sb);
                sb.append("GARANT : ");
                values.getGuaranteeSpentTime().forEach((k, v) -> {
                    sb.append(k).append(" = ").append(v).append("; ");
                });
                System.out.println(sb);
            });

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            ctx.destroy();
        }
    }

    static Person getPersonByEmail(String email, PersonDAO personDAO) {
        PersonQuery query = new PersonQuery();
        Person person = null;
        if (isNotEmpty(email)) {
            query.setEmail(email);
            List<Person> persons = personDAO.getPersons(query);
            if (persons.size() == 1) {
                person = persons.get(0);
                person.setLogins(Collections.singletonList(email));
            }
        }
        return person != null ? person : createTempPerson(email);
    }

    static YtReportItem makeYtReportItem(IssueWorkItem issueWorkItem) {
        return new YtReportItem(
                issueWorkItem.author.email,
                issueWorkItem.issue.idReadable,
                getCustomerName(issueWorkItem.issue),
                issueWorkItem.duration.minutes.longValue(),
                issueWorkItem.issue.project.shortName
        );
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
        person.setLogins(Collections.singletonList("CLASSIFICATION ERROR - " + email));
        return person;
    }

    static class YtReportItem {
        final String email;
        final String issue;
        final String customer;
        final Long spentTime;
        final String project;

        public YtReportItem(String email, String issue, String customer, Long spentTime, String project) {
            this.email = email;
            this.issue = issue;
            this.customer = customer;
            this.spentTime = spentTime;
            this.project = project;
        }

        public String getEmail() {
            return email;
        }

        public String getIssue() {
            return issue;
        }

        public String getCustomer() {
            return customer;
        }

        public Long getSpentTime() {
            return spentTime;
        }

        public String getProject() {
            return project;
        }
    }

    enum WorkType {
        NIOKR, NMA, CONTRACT, GUARANTEE
    }

    static class RepresentTime {
        final Long minutes;
        final String represent;

        public RepresentTime(Long minutes) {
            this.minutes = minutes;
            this.represent = makeRepresent(this.minutes);
        }

        public RepresentTime sum(RepresentTime other) {
            return new RepresentTime(this.minutes + other.minutes);
        }

        private String makeRepresent(Long minutes) {
            long h = minutes / 60;
            return String.format("%s ч. %s m.", h, minutes - h*60);
        }
    }

    static class ReportDTO {
        private Person person;        
        // список обработанных YtWorkItems для статистики / дебага
        final private Map<String, RepresentTime> issueSpentTime;
        // Map<Project, Map<NIOKR, SpentTime>>
        final private Map<String, Long> niokrSpentTime;
        // Map<Project, Map<NMA, SpentTime>>
        final private Map<String, Long> nmaSpentTime;
        // Map<Project, Map<CONTRACT, SpentTime>>
        final private  Map<String, Long> contractSpentTime;
        // Map<Project, Map<GUARANTEE, SpentTime>>
        final private Map<String, Long> guaranteeSpentTime;

        public ReportDTO() {
            this.issueSpentTime = new HashMap<>();
            this.niokrSpentTime = new HashMap<>();
            this.nmaSpentTime = new HashMap<>();
            this.contractSpentTime = new HashMap<>();
            this.guaranteeSpentTime = new HashMap<>();
        }

        public Map<String, RepresentTime> getIssueSpentTime() {
            return issueSpentTime;
        }

        public Map<String, Long> getNiokrSpentTime() {
            return niokrSpentTime;
        }

        public Map<String, Long> getNmaSpentTime() {
            return nmaSpentTime;
        }

        public Map<String, Long> getContractSpentTime() {
            return contractSpentTime;
        }

        public Map<String, Long> getGuaranteeSpentTime() {
            return guaranteeSpentTime;
        }

        public void setPerson(Person person) {
            this.person = person;
        }
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
