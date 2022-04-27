package ru.protei.portal.core.report.contract;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.EnumLangUtil;
import ru.protei.portal.core.utils.JXLSHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.joining;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.StringUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;
import static ru.protei.portal.core.utils.ExcelFormatUtils.ExcelFormat.CURRENCY_COST;

public class ExcelReportWriter implements
        ReportWriter<Contract>,
        JXLSHelper.ReportBook.Writer<Contract> {

    static final private int COST_CELL_NUMBER = 3;

    private final JXLSHelper.ReportBook<Contract> book;
    private final Lang.LocalizedLang lang;
    private final EnumLangUtil enumLangUtil;
    private final DateFormat dateFormat;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang, EnumLangUtil enumLangUtil, DateFormat dateFormat) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
        this.enumLangUtil = enumLangUtil;
        this.dateFormat = dateFormat;
    }

    @Override
    public int createSheet() {
        return book.createSheet();
    }

    @Override
    public void setSheetName(int sheetNumber, String name) {
        book.setSheetName(sheetNumber, name);
    }

    @Override
    public void write(int sheetNumber, List<Contract> objects) {
        book.write(sheetNumber, objects);
    }

    @Override
    public void collect(OutputStream outputStream) throws IOException {
        book.collect(outputStream);
    }

    @Override
    public void close() throws Exception {
        book.close();
    }

    @Override
    public int[] getColumnsWidth() {
        return new int[] {
                15000,
                10000,
                10000,
                3000,
                3000,
                12000,
                4000,
                8000,
                15000,
                6000
        };
    }

    @Override
    public CellStyle getCellStyle(Workbook workbook, int columnIndex) {
        return book.makeCellStyle(columnIndex, cs -> {
            cs.setFont(book.getDefaultFont());
            cs.setVerticalAlignment(VerticalAlignment.CENTER);
            cs.setWrapText(true);
            if (columnIndex == COST_CELL_NUMBER) {
                cs.setQuotePrefixed(false);
                cs.setDataFormat(workbook.createDataFormat().getFormat(CURRENCY_COST));
            }
        });
    }

    @Override
    public String[] getLangColumnNames() {
        return new String[] {
                "cr_number",
                "cr_contractor",
                "cr_description",
                "cr_cost",
                "cr_currency",
                "cr_delivery_and_payments",
                "cr_direction",
                "cr_state",
                "cr_expenditure_contracts",
                "cr_curator"
        };
    }

    @Override
    public Object[] getColumnValues(Contract contract) {
        List<Object> values = new ArrayList<>();
        values.add(makeContractNumber(contract));
        values.add(makeContractorName(contract));
        values.add(emptyIfNull(contract.getDescription()));
        values.add(makeCost(contract));     // в getCellStyle выставляется отдельный стиль для ячейки COST_CELL_NUMBER
        values.add(makeCurrency(contract));
        values.add(makeContractDates(contract));
        values.add(joining(contract.getProductDirections(), ", ", DevUnit::getName));
        values.add(makeState(contract));
        values.add(makeExpenditureContracts(contract));
        values.add(emptyIfNull(contract.getCuratorShortName()));
        return values.toArray();
    }

    private String makeContractNumber(Contract contract) {
        String number = enumLangUtil.contractTypeLang(contract.getContractType(), lang.getLanguageTag()) + " " + contract.getNumber();
        String signingDate = contract.getDateSigning() != null
                ? dateFormat.format(contract.getDateSigning())
                : "";
        String text = number;
        if (isNotEmpty(signingDate)) {
            text += " " + lang.get("from") + " " + signingDate;
        }
        return text;
    }

    private String makeContractorName(Contract contract) {
        if (contract.getContractor() == null) {
            return "";
        }
        return emptyIfNull(contract.getContractor().getName());
    }

    private Double makeCost(Contract contract) {
        return Optional.ofNullable(contract.getCost())
                .map(money -> money.getFull() / 100.0)
                .orElse(0.0);
    }

    private String makeCurrency(Contract contract) {
        if (contract.getCurrency() == null) {
            return "";
        }
        return contract.getCurrency().getCode();
    }

    private String makeContractDates(Contract contract) {
        return stream(contract.getContractDates())
                .map(contractDate -> {
                    String type = enumLangUtil.contractDatesTypeLang(contractDate.getType(), lang.getLanguageTag());
                    String date = contractDate.getDate() != null
                            ? dateFormat.format(contractDate.getDate())
                            : "";
                    String comment = emptyIfNull(contractDate.getComment());
                    String text = type;
                    if (isNotEmpty(date)) {
                        text += " - " + date;
                    }
                    if (isNotEmpty(comment)) {
                        text += " (" + comment + ")";
                    }
                    return text;
                })
                .collect(Collectors.joining("\n"));
    }

    private String makeState(Contract contract) {
        if (contract.getStateId() == null) {
            return "";
        }
        return enumLangUtil.contractStateLang(contract.getStateName(), lang.getLanguageTag());
    }

    private String makeExpenditureContracts(Contract contract) {
        return stream(contract.getChildContracts())
                .map(this::makeContractNumber)
                .collect(Collectors.joining("\n"));
    }
}
