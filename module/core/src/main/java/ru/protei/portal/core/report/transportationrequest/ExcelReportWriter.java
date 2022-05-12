package ru.protei.portal.core.report.transportationrequest;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.*;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.service.YoutrackServiceImpl;
import ru.protei.portal.core.utils.JXLSHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.protei.portal.core.model.helper.StringUtils.*;

public class ExcelReportWriter
        implements
        ReportWriter<YtIssue>,
        JXLSHelper.ReportBook.Writer<YtIssue>
{

    private final static Logger log = LoggerFactory.getLogger(YoutrackServiceImpl.class);

    private final JXLSHelper.ReportBook<YtIssue> book;
    private final Lang.LocalizedLang lang;

    public ExcelReportWriter(Lang.LocalizedLang localizedLang) {
        this.book = new JXLSHelper.ReportBook<>(localizedLang, this);
        this.lang = localizedLang;
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
    public void write(int sheetNumber, List<YtIssue> objects) {
        book.write(sheetNumber, objects);
    }

    public void write(int sheetNumber, YtIssue object) {
        book.write(sheetNumber, object);
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
        return new int[]{
                6000,
                18000,
                6000,
                6000,
                6000,
                6000,
                6000,
                6000,
                6000,
                6000,
                6000,
                6000,
                6000,
                6000,
                6000,
                6000,
                6000,
                6000,
                10000,
                18000,
                6000,
        };
    }

    @Override
    public CellStyle getCellStyle(Workbook workbook, int columnIndex) {
        return book.makeCellStyle(0, cs -> {
            cs.setFont(book.getDefaultFont());
            cs.setVerticalAlignment(VerticalAlignment.CENTER);
            cs.setWrapText(true);
        });
    }

    @Override
    public String[] getLangColumnNames() {
        return new String[]{
                "tr_delivery_date",
                "tr_direction",
                "tr_from_to",
                "tr_req_number",
                "tr_count",
                "tr_weight",
                "tr_volume",
                "tr_volume_weight",
                "tr_cost",
                "tr_insurance_cost",
                "tr_insurance",
                "tr_vacation_req",
                "tr_fragility",
                "tr_tariff",
                "tr_oversized",
                "tr_sum",
                "tr_delivery_type",
                "tr_contract_number",
                "tr_delivery_number",
                "tr_appendix",
                "tr_sender"
        };
    }

    @Override
    public Object[] getColumnValues(YtIssue value) {
        List<Object> values = new ArrayList<>();

        String description = getFieldValueByKeyword(value.description, YtIssue.DlvryDescriptionAttr.description);
        String from = getFieldValueByKeyword(value.description, YtIssue.DlvryDescriptionAttr.from);
        String to = getFieldValueByKeyword(value.description, YtIssue.DlvryDescriptionAttr.to);
        String toAddress = getFieldValueByKeyword(value.description, YtIssue.DlvryDescriptionAttr.toAddress);

//        дата забора груза == Дата отправки
        values.add(getYTCustomFieldValue(value, YtIssue.CustomFieldNames.Delivery.deliveryDate, getDateValueFunction));
//      направление == Поле описание, значение колонки ‘Получатель’ + ‘Адрес получателя’
        values.add(to + " (" + toAddress + ")");
//        откуда/куда (фирма) == Поле описание, анализируем значение колонки ‘Получатель’ и ‘Отправитель’
        values.add(isProteiCompany(from) ? "Из НТЦ" : isProteiCompany(to) ? "В НТЦ" : "–");
//        номер заявки
        values.add(StringUtils.emptyIfNull(value.idReadable));
//        Оставляем пустыми для заполнения (12 полей):
//        кол-во мест, вес(кг.), обьем(м3.), обьемный вес(кг.), стоимость(руб.), страховая сумма(руб.)
//        страховка 0,5% (руб.), перевозка груза в выходные дни:+25% к стоимости (руб.),
//        хрупкий груз +20%(руб.), перевозка груза(руб.), негабарит +25%(руб.), итоговая стоимость (руб.)
        values.add(EMPTY);
        values.add(EMPTY);
        values.add(EMPTY);
        values.add(EMPTY);
        values.add(EMPTY);
        values.add(EMPTY);
        values.add(EMPTY);
        values.add(EMPTY);
        values.add(EMPTY);
        values.add(EMPTY);
        values.add(EMPTY);
        values.add(EMPTY);
//      вид доставки груза == Срочность доставки
        values.add(getYTCustomFieldValue(value, YtIssue.CustomFieldNames.Delivery.urgency, getEnumValueFunction));
//        договор/счет == Номер договора
        values.add(getYTCustomFieldValue(value, YtIssue.CustomFieldNames.Delivery.contractNumber, getStringValueFunction));
//        поставка
        values.add(getYTCustomFieldValue(value, YtIssue.CustomFieldNames.Delivery.deliveryNumber, getStringValueFunction));
//        доп.информация == Поле описание, значение колонки ‘Наименование’ в разделе характеристики груза
        values.add(description);
//        от какой фирмы отправляли == Компания
        values.add(getYTCustomFieldValue(value, YtIssue.CustomFieldNames.Delivery.company, getEnumValueFunction));

        return values.toArray();
    }

    private String getFieldValueByKeyword(String value, String keyword) {
        String regex = "^\\|.*" + keyword + ".*\\|([^\\n]+)\\|\\n*$";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            return matcher.group(1);
        }

        return EMPTY;
    }

    private String getYTCustomFieldValue(YtIssue value, String customFieldNames, Function<YtIssueCustomField, String> castAndGetValueFunction) {
        YtIssueCustomField customField = value.getField(customFieldNames);
        if (customField == null) {
            log.warn("Youtrack issue does not contains " + customFieldNames + " field issueId={}", value.id);
            return EMPTY;
        }

        return castAndGetValueFunction.apply(customField);
    }

    private Function<YtIssueCustomField, String> getStringValueFunction = customField -> {
        YtSimpleIssueCustomField field = (YtSimpleIssueCustomField) customField;
        return field.getValue() == null ? EMPTY : field.getValue();
    };

    private Function<YtIssueCustomField, String> getEnumValueFunction = customField -> {
        YtSingleEnumIssueCustomField field = (YtSingleEnumIssueCustomField) customField;
        return field.getValueAsString() == null ? EMPTY : field.getValueAsString();
    };

    private Function<YtIssueCustomField, String> getDateValueFunction = customField -> {
        YtDateIssueCustomField field = (YtDateIssueCustomField) customField;
        return field.getValue() == null ? EMPTY : dateToYtString(field.getValue());
    };

    private String dateToYtString(Date date) {
        if (date == null) return EMPTY;
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    private boolean isProteiCompany(String companyName) {
        if (StringUtils.isEmpty(companyName)) return false;
        return companyName.toLowerCase().contains("протей");
    }
}