package ru.protei.portal.core.client.enterprise1c.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.enterprise1c.http.HttpClient1CWork;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.enterprise1c.annotation.UrlName1C;
import ru.protei.portal.core.model.enterprise1c.dto.WorkPersonInfo1C;
import ru.protei.portal.core.model.enterprise1c.query.WorkQuery1C;
import ru.protei.portal.core.model.helper.StringUtils;

import java.util.Optional;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.core.model.util.CrmConstants.Company.MAIN_HOME_COMPANY_NAME;
import static ru.protei.portal.core.model.util.CrmConstants.Company.PROTEI_ST_HOME_COMPANY_NAME;

public class Api1CWorkImpl implements Api1CWork{

    @Override
    public Result<WorkPersonInfo1C> getProteiWorkPersonInfo(WorkQuery1C query) {
        log.debug("getProteiWorkPersonInfo(): query={}", query);

        if (query == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return client.getProteiWorkPersonInfo(query)
                .ifOk( value -> log.info( "getProteiWorkPersonInfo(): OK " ) )
                .ifError( result -> log.warn( "getProteiWorkPersonInfo(): Can`t get WorkPersonInfo1C={}", result ));
    }

    @Override
    public Result<WorkPersonInfo1C> getProteiStWorkPersonInfo(WorkQuery1C query) {
        log.debug("getProteiStWorkPersonInfo(): query={}", query);

        if (query == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return client.getProteiStWorkPersonInfo(query)
                .ifOk( value -> log.info( "getProteiStWorkPersonInfo(): OK " ) )
                .ifError( result -> log.warn( "getProteiStWorkPersonInfo(): Can`t get WorkPersonInfo1C={}", result ));
    }

    @Override
    public Result<String> getEmployeeRestVacationDays(String workerExtId, String companyName) {
        log.debug("getEmployeeRestVacationDays(): workerExtId={}, companyName={}", workerExtId, companyName);

        return buildGetRestVacationDaysByKeyUrl(workerExtId, companyName)
                .map(url1C -> client.read(url1C, Result.class)
                        .ifOk(value -> log.info("getEmployeeRestVacationDays(): OK "))
                        .ifError(result -> log.warn("getEmployeeRestVacationDays(): Can`t get restVacationDays for worker with external id {}. Result={}", workerExtId, result))
                        .map(result -> String.valueOf(result.getData())))
                .orElse(Result.error(En_ResultStatus.INTERNAL_ERROR));
    }

    private Optional<String> buildGetRestVacationDaysByKeyUrl(String workerExtId, String companyName){
        return buildGetRestVacationDaysBaseUrl(companyName)
                .map(url -> url + "/" + workerExtId);
    }

    private Optional<String> buildGetRestVacationDaysBaseUrl (String companyName) {
        if (MAIN_HOME_COMPANY_NAME.equals(companyName)) {
            return Optional.ofNullable(config.data().enterprise1C().getWorkRestVacationDaysProteiUrl());
        } else if (PROTEI_ST_HOME_COMPANY_NAME.equals(companyName)) {
            return Optional.ofNullable(config.data().enterprise1C().getWorkRestVacationDaysProteiStUrl());
        }
        return Optional.empty();
    }

    @Autowired
    HttpClient1CWork client;
    @Autowired
    PortalConfig config;

    private final static Logger log = LoggerFactory.getLogger(Api1CWorkImpl.class);
}
