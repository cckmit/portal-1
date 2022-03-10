package ru.protei.portal.core.report.dutylog;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.EnumLangUtil;
import ru.protei.portal.core.utils.ExcelFormatUtils;
import ru.protei.portal.core.utils.JXLSHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReportWriter implements
        ReportWriter<DutyLog>,
        JXLSHelper.ReportBook.Writer<DutyLog> {

    private final JXLSHelper.ReportBook<DutyLog> book;
    private final Lang.LocalizedLang lang;
    private final String[] formats;
    private final EnumLangUtil enumLangUtil;

    public ExcelReportWriter(Lang.LocalizedLang lang, EnumLangUtil enumLangUtil) {
        this.book = new JXLSHelper.ReportBook<>(lang, this);
        this.lang = lang;
        this.formats = getFormats();
        this.enumLangUtil = enumLangUtil;
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
    public void write(int sheetNumber, List<DutyLog> objects) {
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
        return new int[] {  4200, 4200, 6500, 5800 };
    }

    @Override
    public String[] getLangColumnNames() {
        return new String[] { "dl_from_time", "dl_till_time", "dl_duty",  "dl_type"};
    }

    @Override
    public Object[] getColumnValues(DutyLog object) {
        List<Object> values = new ArrayList<>();
        values.add(object.getFrom() != null ? object.getFrom() : "");
        values.add(object.getTo() != null ? object.getTo() : "");
        values.add(HelperFunc.isNotEmpty(object.getPersonDisplayName()) ? object.getPersonDisplayName() : "");
        values.add(object.getType() != null ? enumLangUtil.dutyLogTypeLang(object.getType(), lang.getLanguageTag()) : "");
        return values.toArray();
    }

    private String[] getFormats() {
        return new String[] {
                ExcelFormatUtils.ExcelFormat.FULL_DATE_TIME, ExcelFormatUtils.ExcelFormat.FULL_DATE_TIME,
                ExcelFormatUtils.ExcelFormat.STANDARD, ExcelFormatUtils.ExcelFormat.STANDARD
        };
    }
}