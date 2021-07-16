package ru.protei.portal.core.report.nightwork;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.ent.CaseCommentNightWork;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.ListBuilder;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.ExcelFormatUtils.ExcelFormat;
import ru.protei.portal.core.utils.JXLSHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.toPrimitiveIntegerArray;
import static ru.protei.portal.core.model.util.TransliterationUtils.transliterate;
import static ru.protei.portal.core.utils.ExcelFormatUtils.toExcelTimeFormat;

public class ExcelReportWriter implements
        ReportWriter<CaseCommentNightWork>,
        JXLSHelper.ReportBook.Writer<CaseCommentNightWork> {

    private final JXLSHelper.ReportBook<CaseCommentNightWork> book;
    private final Lang.LocalizedLang lang;
    private final String locale;
    private final String[] formats;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
        this.locale = localizedLang.getLanguageTag();
        this.formats = getFormats();
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
    public void write(int sheetNumber, List<CaseCommentNightWork> objects) {
        book.write(sheetNumber, objects);
    }

    @Override
    public void collect(OutputStream outputStream) throws IOException {
        book.collect(outputStream);
    }

    @Override
    public void close() throws IOException {
        book.close();
    }

    @Override
    public CellStyle getCellStyle(Workbook workbook, int columnIndex) {
        return book.makeCellStyle(columnIndex, cs -> {
            cs.setFont(book.getDefaultFont());
            cs.setVerticalAlignment(VerticalAlignment.CENTER);
            cs.setDataFormat(workbook.createDataFormat()
                    .getFormat(formats[columnIndex]));
        });
    }

    @Override
    public int[] getColumnsWidth() {
        List<Integer> columnsWidthList = new ListBuilder<Integer>()
                .add(3650).add(3430)
                .add(6600).add(3430).add(6600).add(6600)
                .add(4600).add(4600).add(10000)
                .build();

        return toPrimitiveIntegerArray(columnsWidthList);
    }

    @Override
    public String[] getLangColumnNames() {
        List<String> columnNames = new ListBuilder<String>()
                .add("ir_date").add("ir_night_work_time_elapsed_sum")
                .add("ir_manager").add("ir_caseno").add("ir_customer").add("ir_initiator")
                .add("ir_product").add("ir_last_comment_date").add("ir_last_comment_text")
                .build();

        return columnNames.toArray(new String[]{});
    }

    @Override
    public Object[] getColumnValues(CaseCommentNightWork object) {
        List<Object> columnValues = new ListBuilder<>()
                .add(object.getDay())
                .add(toExcelTimeFormat(object.getTimeElapsedSum()))
                .add(HelperFunc.isNotEmpty(object.getAuthorDisplayName()) ? transliterate(object.getAuthorDisplayName(), locale) : "")
                .add("CRM-" + object.getCaseNumber())
                .add(HelperFunc.isNotEmpty(object.getCaseCompanyName()) ? transliterate(object.getCaseCompanyName(), locale) : "")
                .add(HelperFunc.isNotEmpty(object.getInitiatorDisplayName()) ? transliterate(object.getInitiatorDisplayName(), locale) : "")
                .add(HelperFunc.isNotEmpty(object.getProductName()) ? object.getProductName() : "")
                .add(object.getLastCaseComment().getCreated())
                .add(HelperFunc.isNotEmpty(object.getLastCaseComment().getText())? object.getLastCaseComment().getText() : "")
                .build();

        return columnValues.toArray();
    }

    private String[] getFormats() {
        List<String> columnsWidthList = new ListBuilder<String>()
                .add(ExcelFormat.FULL_DATE).add(ExcelFormat.INFINITE_HOURS_MINUTES)
                .add(ExcelFormat.STANDARD).add(ExcelFormat.STANDARD).add(ExcelFormat.STANDARD).add(ExcelFormat.STANDARD)
                .add(ExcelFormat.STANDARD).add(ExcelFormat.FULL_DATE_TIME).add(ExcelFormat.STANDARD)
                .build();

        return columnsWidthList.toArray(new String[]{});
    }
}
