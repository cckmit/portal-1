package ru.protei.portal.core.report;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface ReportWriter<T> extends AutoCloseable {

    int createSheet();

    void setSheetName(int sheetNumber, String name);

    void write(int sheetNumber, List<T> objects);

    void collect(OutputStream outputStream) throws IOException;
}
