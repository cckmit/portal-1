package ru.protei.portal.core.client.enterprise1c.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.client.enterprise1c.http.HttpClient1CWork;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.enterprise1c.dto.WorkPersonInfo1C;
import ru.protei.portal.core.model.enterprise1c.query.WorkQuery1C;

import static ru.protei.portal.api.struct.Result.error;

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

    @Autowired
    HttpClient1CWork client;

    private final static Logger log = LoggerFactory.getLogger(Api1CWorkImpl.class);
}
