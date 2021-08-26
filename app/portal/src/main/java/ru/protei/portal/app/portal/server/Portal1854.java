package ru.protei.portal.app.portal.server;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.client.enterprise1c.http.HttpClient1CWork;
import ru.protei.portal.core.model.enterprise1c.dto.WorkPersonInfo1C;
import ru.protei.portal.core.model.enterprise1c.query.WorkQuery1C;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Calendar;
import java.util.Date;

public class Portal1854 {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);

        HttpClient1CWork api = ctx.getBean(HttpClient1CWork.class);
        if (true)
            while (true) {
                WorkQuery1C query1C = new WorkQuery1C();
                query1C.setDateFrom(new Date(2021 - 1900, Calendar.JUNE, 1));
                query1C.setDateTo(new Date(2021 - 1900, Calendar.JUNE, 2));
                query1C.setPersonNumber("0000000816");
                Result<WorkPersonInfo1C> proteiWorkPersonInfo = api.getProteiWorkPersonInfo(query1C);
                proteiWorkPersonInfo.ifOk(System.out::println);
                proteiWorkPersonInfo.ifError(System.out::println);
            }

        ctx.destroy();
    }
}
