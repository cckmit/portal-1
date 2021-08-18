package ru.protei.portal.core.report.ytwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.YtWorkQuery;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.struct.WorkerEntryFacade;
import ru.protei.portal.core.model.struct.reportytwork.*;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkInfo;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.YtSingleEnumIssueCustomField;
import ru.protei.portal.core.model.youtrack.dto.issue.IssueWorkItem;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.tools.ChunkIterator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toSet;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;
import static ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem.NameWithId;
import static ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem.PersonInfo;
import static ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem.PersonInfo.nullDepartmentName;

public class ReportYtWorkImpl implements ReportYtWork {

    private static Logger log = LoggerFactory.getLogger(ReportYtWorkImpl.class);

    @Autowired
    Lang lang;
    @Autowired
    PortalConfig config;
    @Autowired
    ContractDAO contractDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    EmployeeShortViewDAO employeeShortViewDAO;
    @Autowired
    WorkerEntryShortViewDAO workerEntryShortViewDAO;
    @Autowired
    ReportDAO reportDAO;
    @Autowired
    CompanyDAO companyDAO;
    @Autowired
    YoutrackApi api;

    private final Map<String, List<String>> invertNiokrs;
    private final Map<String, List<String>> invertNmas;
    static private final String CLASSIFICATION_ERROR_PREFIX = "CLASSIFICATION ERROR - ";

    public ReportYtWorkImpl() {
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
        invertNiokrs = inverseMap(niokrs);

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

        invertNmas = inverseMap(nmas);
    }

    @Override
    public boolean writeReport(OutputStream buffer,
                               Report report,
                               YtWorkQuery query,
                               Predicate<Long> isCancel) throws IOException {

        log.debug("writeReport : reportId={} to process", report.getId());

        Interval interval = makeInterval(query.getDateRange());
        ChunkIterator<IssueWorkItem> iterator = new ChunkIterator<>(
                (offset, limit) -> api.getWorkItems(interval.from, interval.to, offset, limit),
                () -> isCancel.test(report.getId()),
                config.data().reportConfig().getChunkSize()
        );

        ReportYtWorkCollector collector = new ReportYtWorkCollector(
                invertNiokrs, invertNmas,
                name -> contractDAO.getByCustomerAndProject(name),
                new Date(), makeHomeCompanySet(),
                CLASSIFICATION_ERROR_PREFIX
        );

        log.debug("writeReport : reportId={} start process", report.getId());
        Map<String, ReportYtWorkRowItem> data = stream(iterator)
                .map(this::makeYtReportItem)
                .collect(collector);

        switch(iterator.getStatus()) {
            case OK: {
                log.debug("writeReport : collect data with isOk status : reportId={}", report.getId());
                break;
            }
            case CANCELED: {
                log.info("writeReport : canceled : reportId={}", report.getId());
                return true;
            }
            default: {
                log.error("writeReport : error : reportId={}, status = {}", report.getId(), iterator.getStatus());
                return false;
            }
        }

        data.forEach((email, ytWorkItem) -> ytWorkItem.setPersonInfo(makePersonInfo(email)));

        Map<Boolean, List<ReportYtWorkRowItem>> partitionByHasWorkEntry = stream(data.values())
                .collect(partitioningBy(item -> item.getPersonInfo().hasWorkEntry()));

        Map<NameWithId, DepartmentTreeAndValues> groupingByCompanyDepartment =
                groupingByCompanyDepartment(partitionByHasWorkEntry.get(true));

        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));
        try (ReportWriter<ReportYtWorkRow> writer = new ExcelReportWriter(localizedLang, collector.getProcessedWorkTypes())) {
            log.debug("writeReport : start write sheet");
            groupingByCompanyDepartment.forEach((companyName, companyTreeAndValues) -> {
                int sheetNumber = writer.createSheet();
                writer.setSheetName(sheetNumber, companyName.getString());
                writer.write(sheetNumber, makeReportCompanyData(companyTreeAndValues));
            });

            int sheetNumber = writer.createSheet();
            writer.setSheetName(sheetNumber, "NO COMPANY");
            writer.write(sheetNumber, new ArrayList<>(partitionByHasWorkEntry.get(false)));

            writer.collect(buffer);
            return true;
        } catch (Throwable th) {
            log.error("writeReport : fail to write : reportId={}, th={}", report.getId(), th);
            return false;
        }
    }

    public Map<NameWithId, DepartmentTreeAndValues> groupingByCompanyDepartment(List<ReportYtWorkRowItem> hasWorkEntry) {
        Map<NameWithId, DepartmentTreeAndValues> companyMap = new HashMap<>();
        hasWorkEntry.forEach(item -> {
            PersonInfo personInfo = item.getPersonInfo();
            DepartmentTreeAndValues treeAndValues = companyMap.compute(personInfo.getCompanyName(),
                    (companyName, companyTree) -> companyTree != null ? companyTree : new DepartmentTreeAndValues());
            treeAndValues.getValues().compute(personInfo.getDepartmentName(), (name, values) -> {
                if (values == null) {
                    treeAndValues.getTree().addNode(personInfo.getDepartmentParentName(), personInfo.getDepartmentName());
                    values = new ArrayList<>();
                }
                values.add(item);
                return values;
            });
        });

        return companyMap;
    }

    public List<ReportYtWorkRow> makeReportCompanyData(DepartmentTreeAndValues treeAndValues) {
        List<ReportYtWorkRow> list = new ArrayList<>();
        treeAndValues.getTree().deepFirstSearchTraversal(node -> {
            int level = node.getLevel();
            list.add(new ReportYtWorkRowHeader(levelMark.getOrDefault(level, "?") + node.getNameWithId().getString()));

            List<ReportYtWorkRowItem> reportYtWorkRowItems = treeAndValues.getValues().get(node.getNameWithId());
            list.addAll(stream(reportYtWorkRowItems)
                    .sorted(Comparator.comparing(item -> item.getPersonInfo().getDisplayName()))
                    .collect(Collectors.toList())
            );
        });

        return list;
    }

    static private final Map<Integer, String> levelMark = new HashMap<>();
    static {
        levelMark.put(0, "== ");
        levelMark.put(1, "==== ");
        levelMark.put(2, "====== ");
        levelMark.put(3, "======== ");
    }

    private Set<String> makeHomeCompanySet() {
        Set<String> homeCompany = companyDAO.getAllHomeCompanies().stream()
                .map(Company::getCname).collect(toSet());
        homeCompany.add(null);
        log.debug("makeHomeCompanySet : set={}", homeCompany);
        return homeCompany;
    }

    private PersonInfo makePersonInfo(String email) {
        EmployeeShortView employeeShortView = null;
        if (isNotEmpty(email)) {
            EmployeeQuery query = new EmployeeQuery();
            query.setEmailByLike(email);
            query.setDeleted(false);
            query.setFired(false);
            List<EmployeeShortView> personShortViews = employeeShortViewDAO.getEmployees(query);
            if (personShortViews.size() >= 1) {
                employeeShortView = personShortViews.get(0);
                employeeShortView.setWorkerEntries(workerEntryShortViewDAO.listByPersonIds(setOf(employeeShortView.getId())));
            }
        }
        return employeeShortView != null ? createPersonInfo(employeeShortView) : createTempPersonInfo(email);
    }

    static private PersonInfo createPersonInfo(EmployeeShortView employeeShortView) {
        WorkerEntryShortView mainEntry = new WorkerEntryFacade(employeeShortView.getWorkerEntries()).getMainEntry();
        if (mainEntry == null) {
            return new PersonInfo(
                    employeeShortView.getDisplayShortName(),
                    employeeShortView.getId()
            );
        } else {
            return new PersonInfo(
                    employeeShortView.getDisplayShortName(),
                    employeeShortView.getId(),
                    mainEntry.getCompanyName() != null ?
                            new NameWithId(mainEntry.getCompanyName(), mainEntry.getCompanyId())
                            : PersonInfo.nullCompanyName,
                    mainEntry.getDepartmentParentName() != null ?
                            new NameWithId(mainEntry.getDepartmentParentName(), mainEntry.getParentDepId())
                            : PersonInfo.nullDepartmentParentName,
                    mainEntry.getDepartmentName() != null ?
                            new NameWithId(mainEntry.getDepartmentName(), mainEntry.getDepId())
                            : nullDepartmentName
            );
        }
    }

    static private PersonInfo createTempPersonInfo(String email) {
        return new PersonInfo(email, null);
    }

    private ReportYtWorkInfo makeYtReportItem(IssueWorkItem issueWorkItem) {
        return new ReportYtWorkInfo(
                issueWorkItem.author.email != null ? issueWorkItem.author.email : issueWorkItem.author.login,
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

    static private class DepartmentTreeAndValues {
        private final DepartmentTree tree = new DepartmentTree();
        private final Map<NameWithId, List<ReportYtWorkRowItem>> values = new HashMap<>();

        public DepartmentTree getTree() {
            return tree;
        }

        public Map<NameWithId, List<ReportYtWorkRowItem>> getValues() {
            return values;
        }
    }
}