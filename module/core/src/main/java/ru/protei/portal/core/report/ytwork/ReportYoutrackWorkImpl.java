package ru.protei.portal.core.report.ytwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.client.enterprise1c.api.Api1CWork;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.ent.YoutrackWorkDictionary;
import ru.protei.portal.core.model.enterprise1c.dto.WorkPersonInfo1C;
import ru.protei.portal.core.model.enterprise1c.query.WorkQuery1C;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.YoutrackWorkQuery;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.struct.WorkerEntryFacade;
import ru.protei.portal.core.model.struct.reportytwork.*;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.YtSingleEnumIssueCustomField;
import ru.protei.portal.core.model.youtrack.dto.issue.IssueWorkItem;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.portal.tools.ChunkIterator;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

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

public class ReportYoutrackWorkImpl implements ReportYoutrackWork {

    private static Logger log = LoggerFactory.getLogger(ReportYoutrackWorkImpl.class);

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
    CaseCommentDAO caseCommentDAO;
    @Autowired
    YoutrackWorkDictionaryDAO youtrackWorkDictionaryDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    YoutrackApi api;
    @Autowired
    Api1CWork api1CWork;

    static private final String CLASSIFICATION_ERROR = "CLASSIFICATION ERROR";
    static private final String NO_COMPANY = "NO COMPANY";
    static private final Set<String> workerCompanyName = setOf(CrmConstants.Company.MAIN_HOME_COMPANY_NAME, CrmConstants.Company.PROTEI_ST_HOME_COMPANY_NAME);

    @Override
    public boolean writeReport(OutputStream buffer,
                               Report report,
                               YoutrackWorkQuery query,
                               Predicate<Long> isCancel) throws IOException {

        log.debug("writeReport : reportId={} to process", report.getId());

        List<YoutrackWorkDictionary> dictionaries = youtrackWorkDictionaryDAO.getAll();
        jdbcManyRelationsHelper.fill(dictionaries, YoutrackWorkDictionary.Fields.YOUTRACK_PROJECTS);
        Map<En_YoutrackWorkType, Map<String, List<String>>> dictionariesMap = makeYoutrackWorkTypeMap(dictionaries);

        Interval interval = makeInterval(query.getDateRange());
        ChunkIterator<IssueWorkItem> iterator = new ChunkIterator<>(
                (offset, limit) -> api.getWorkItems(interval.from, interval.to, offset, limit),
                () -> isCancel.test(report.getId()),
                config.data().reportConfig().getChunkSize()
        );

        ReportYtWorkCollector collector = new ReportYtWorkCollector(
                dictionariesMap.get(En_YoutrackWorkType.NIOKR),
                dictionariesMap.get(En_YoutrackWorkType.NMA),
                name -> contractDAO.getByCustomerAndProject(name),
                new Date(), makeHomeCompanySet()
        );

        log.debug("writeReport : reportId={} start process", report.getId());
        ReportYtWorkCollector.ErrorsAndItems data = stream(iterator)
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

        Map<String, ReportYtWorkRowItem> items = data.getItems();

        fillPersonInfo(items);

        List<ReportYtWorkCaseCommentTimeElapsedSum> caseCommentReportYtWork = caseCommentDAO.getCaseCommentReportYtWork(interval);
        caseCommentReportYtWork.forEach(cc -> {
            log.info(cc.toString());
        });

        Map<Boolean, List<ReportYtWorkRowItem>> partitionByHasWorkEntry = stream(items.values())
                .collect(partitioningBy(item -> item.getPersonInfo().hasWorkEntry()));

        Map<NameWithId, CompanyReportInfo> groupingByCompanyInfo =
                groupingByCompanyInfo(partitionByHasWorkEntry.get(true));

        groupingByCompanyInfo.forEach((companyName, info) -> {
            if (workerCompanyName.contains(companyName.getString())) {
                info.getValues().values().forEach(list -> fillWorkedHours(interval, companyName.getString(), list));
            }
        });

        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));
        try (ExcelReportWriter writer = new ExcelReportWriter(localizedLang)) {
            log.debug("writeReport : start write sheet");
            groupingByCompanyInfo.forEach((companyName, companyReportInfo) ->
                    writeCompanySheets(companyName, companyReportInfo, writer));

            writeNoCompanySheet(new ArrayList<>(partitionByHasWorkEntry.get(false)), writer);

            writeClassificationErrorSheet(data.getErrors(), writer);

            writer.collect(buffer);
            log.debug("writeReport : reportId={} to end", report.getId());
            return true;
        } catch (Throwable th) {
            log.error("writeReport : fail to write : reportId={}, th={}", report.getId(), th);
            return false;
        }
    }

    private void writeCompanySheets(NameWithId companyName, CompanyReportInfo companyReportInfo, ExcelReportWriter writer) {
        writer.setValueSheet(companyReportInfo.getProcessedWorkTypes());
        int sheetNumber = writer.createSheet();
        writer.setSheetName(sheetNumber, companyName.getString());
        writer.write(sheetNumber, makeReportCompanyData(companyReportInfo));
    }

    private void writeNoCompanySheet(List<ReportYtWorkRow> noCompanyItems, ExcelReportWriter writer) {
        Map<En_YoutrackWorkType, Set<String>> noCompanyProcessedWorkTypes = createProcessedWorkTypes();
        noCompanyItems.forEach(item -> collectProcessedWorkTypes(noCompanyProcessedWorkTypes, item));
        writer.setValueSheet(noCompanyProcessedWorkTypes);
        int sheetNumber = writer.createSheet();
        writer.setSheetName(sheetNumber, NO_COMPANY);
        writer.write(sheetNumber, noCompanyItems);
    }

    private void writeClassificationErrorSheet(Set<ReportYtWorkClassificationError> errors, ExcelReportWriter writer) {
        writer.setClassificationErrorSheet();
        int sheetNumber = writer.createSheet();
        writer.setSheetName(sheetNumber, CLASSIFICATION_ERROR);
        List<ReportYtWorkRow> sortedErrors = errors.stream()
                .sorted(Comparator.comparing(ReportYtWorkClassificationError::getIssue))
                .collect(Collectors.toList());
        writer.write(sheetNumber, sortedErrors);
    }

    private void fillPersonInfo(Map<String, ReportYtWorkRowItem> items) {
        items.forEach((email, ytWorkItem) -> ytWorkItem.setPersonInfo(makePersonInfo(email)));
    }

    private void fillWorkedHours(Interval interval, String companyName, List<ReportYtWorkRowItem> items) {
        items.forEach(item -> {
            String workerId = item.getPersonInfo().getWorkerId();
            if (workerId != null) {
                WorkQuery1C query1C = new WorkQuery1C();
                query1C.setDateFrom(interval.from);
                query1C.setDateTo(interval.to);
                query1C.setPersonNumber(workerId);

                item.setWorkedHours(getWorkedHours(companyName, query1C));
            }
        });
    }

    private Integer getWorkedHours(String companyName, WorkQuery1C query) {
        Result<WorkPersonInfo1C> proteiWorkPersonInfo = null;
        if (CrmConstants.Company.MAIN_HOME_COMPANY_NAME.equals(companyName)) {
            proteiWorkPersonInfo = api1CWork.getProteiWorkPersonInfo(query);
        }
        if (CrmConstants.Company.PROTEI_ST_HOME_COMPANY_NAME.equals(companyName)) {
            proteiWorkPersonInfo = api1CWork.getProteiStWorkPersonInfo(query);
        }
        if (proteiWorkPersonInfo != null && proteiWorkPersonInfo.isOk()) {
            return proteiWorkPersonInfo.getData().getWorkedHours();
        } else {
            return 0;
        }
    }

    private Map<NameWithId, CompanyReportInfo> groupingByCompanyInfo(List<ReportYtWorkRowItem> hasWorkEntry) {
        Map<NameWithId, CompanyReportInfo> companyMap = new HashMap<>();
        hasWorkEntry.forEach(item -> {
            PersonInfo personInfo = item.getPersonInfo();
            CompanyReportInfo companyReportInfo = companyMap.compute(personInfo.getCompanyName(),
                    (companyName, companyTree) -> companyTree != null ? companyTree : new CompanyReportInfo());
            companyReportInfo.getValues().compute(personInfo.getDepartmentName(), (name, values) -> {
                if (values == null) {
                    companyReportInfo.getTree().addNode(personInfo.getDepartmentParentName(), personInfo.getDepartmentName());
                    values = new ArrayList<>();
                }
                values.add(item);
                return values;
            });
            collectProcessedWorkTypes(companyReportInfo.processedWorkTypes, item);
        });

        return companyMap;
    }

    private void collectProcessedWorkTypes(Map<En_YoutrackWorkType, Set<String>> map, ReportYtWorkRow row) {
        if (row instanceof ReportYtWorkRowItem) {
            ReportYtWorkRowItem item = (ReportYtWorkRowItem)row;
            map.compute(En_YoutrackWorkType.NIOKR,
                    (type, set) -> set != null ? set : new HashSet<>()).addAll(item.getNiokrSpentTime().keySet());
            map.compute(En_YoutrackWorkType.NMA,
                    (type, set) -> set != null ? set : new HashSet<>()).addAll(item.getNmaSpentTime().keySet());
            map.compute(En_YoutrackWorkType.CONTRACT,
                    (type, set) -> set != null ? set : new HashSet<>()).addAll(item.getContractSpentTime().keySet());
            map.compute(En_YoutrackWorkType.GUARANTEE,
                    (type, set) -> set != null ? set : new HashSet<>()).addAll(item.getGuaranteeSpentTime().keySet());
        }
    }

    private List<ReportYtWorkRow> makeReportCompanyData(CompanyReportInfo treeAndValues) {
        List<ReportYtWorkRow> list = new ArrayList<>();
        treeAndValues.getTree().deepFirstSearchTraversal(node -> {
            list.add(new ReportYtWorkRowHeader(node.getLevel(), node.getNameWithId().getString()));

            List<ReportYtWorkRowItem> reportYtWorkRowItems = treeAndValues.getValues().get(node.getNameWithId());
            list.addAll(stream(reportYtWorkRowItems)
                    .sorted(Comparator.comparing(item -> item.getPersonInfo().getDisplayName()))
                    .collect(Collectors.toList())
            );
        });

        return list;
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
                    mainEntry.getWorkerExtId(),
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

    static private String getCustomerName(YtIssue issue) {
        YtSingleEnumIssueCustomField field = (YtSingleEnumIssueCustomField) issue.getCustomerField();
        if (field == null) {
            return null;
        }
        return field.getValueAsString();
    }

    static private class CompanyReportInfo {
        private final DepartmentTree tree = new DepartmentTree();
        private final Map<NameWithId, List<ReportYtWorkRowItem>> values = new HashMap<>();
        private final Map<En_YoutrackWorkType, Set<String>> processedWorkTypes = createProcessedWorkTypes();
        public DepartmentTree getTree() {
            return tree;
        }
        public Map<NameWithId, List<ReportYtWorkRowItem>> getValues() {
            return values;
        }
        public Map<En_YoutrackWorkType, Set<String>> getProcessedWorkTypes() {
            return processedWorkTypes;
        }
    }

    static private Map<En_YoutrackWorkType, Set<String>> createProcessedWorkTypes() {
        Map<En_YoutrackWorkType, Set<String>> processedWorkTypes = new LinkedHashMap<>();
        for (En_YoutrackWorkType value : En_YoutrackWorkType.values()) {
            processedWorkTypes.put(value, new HashSet<>());
        }
        return processedWorkTypes;
    }

    private Map<En_YoutrackWorkType, Map<String, List<String>>> makeYoutrackWorkTypeMap(List<YoutrackWorkDictionary> dictionaries) {
        return dictionaries.stream().collect(HashMap::new,
                (map, dictionary) -> {
                    Map<String, List<String>> typeToMap = map.compute(dictionary.getType(), (type, innerMap) -> {
                        if (innerMap == null) {
                            innerMap = new HashMap<>();
                        }
                        return innerMap;
                    });
                    dictionary.getYoutrackProjects().forEach(project -> {
                        typeToMap.compute(project.getShortName(), (projectKey, list) -> {
                            if (list == null) {
                                list = new ArrayList<>();
                            }
                            list.add(dictionary.getName());
                            return list;
                        });
                    });
                },
                (map1, map2) -> mergeMap(map1, map2, (innerMap1, innerMap2) -> {
                    mergeMap(innerMap1, innerMap2, (list1, list2) -> {
                        list1.addAll(list2);
                        return list1;
                    });
                    return innerMap1;
                }));
    }
}
