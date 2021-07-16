package ru.protei.portal.core.report.absence;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.ExcelFormatUtils.ExcelFormat;
import ru.protei.portal.core.utils.JXLSHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReportWriter implements
        ReportWriter<PersonAbsence>,
        JXLSHelper.ReportBook.Writer<PersonAbsence> {

    private final JXLSHelper.ReportBook<PersonAbsence> book;
    private final Lang.LocalizedLang lang;
    private final String[] formats;

    public ExcelReportWriter(Lang.LocalizedLang lang) {
        this.book = new JXLSHelper.ReportBook<>(lang, this);
        this.lang = lang;
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
    public void write(int sheetNumber, List<PersonAbsence> objects) {
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
        return new int[] { 6500, 4200, 4200, 5800, 6500 };
    }

    @Override
    public String[] getLangColumnNames() {
        return new String[] { "ar_employee", "ar_from_time", "ar_till_time", "ar_reason", "ar_comment" };
    }

    @Override
    public Object[] getColumnValues(PersonAbsence object) {
        List<Object> values = new ArrayList<>();
        values.add(object.getPerson() != null && HelperFunc.isNotEmpty(object.getPerson().getName()) ? object.getPerson().getName() : "");
        values.add(object.getFromTime() != null ? object.getFromTime() : "");
        values.add(object.getTillTime() != null ? object.getTillTime() : "");
        values.add(object.getReason() != null ? lang.get("absenceReasonValue" + object.getReason().getId()) : "");
        values.add(HelperFunc.isNotEmpty(object.getUserComment()) ? object.getUserComment() : "");
        return values.toArray();
    }

    private String[] getFormats() {
        return new String[] {
                ExcelFormat.STANDARD, ExcelFormat.FULL_DATE_TIME, ExcelFormat.FULL_DATE_TIME, ExcelFormat.STANDARD, ExcelFormat.STANDARD
        };
    }
}
