package ru.protei.portal.core.client.enterprise1c.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.enterprise1c.http.HttpClient1C;
import ru.protei.portal.core.client.enterprise1c.mapper.FieldsMapper1C;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.enterprise1c.annotation.UrlName1C;
import ru.protei.portal.core.model.enterprise1c.dto.Country1C;
import ru.protei.portal.core.model.enterprise1c.Response1C;
import ru.protei.portal.core.model.enterprise1c.dto.Contractor1C;
import ru.protei.portal.core.model.helper.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Api1CImpl implements Api1C{

    @Override
    public Result<List<Contractor1C>> getContractors(Contractor1C contractor) {
        log.debug("getContractor(): contractor={}", contractor);

        return client.read(buildGetContractorUrl(contractor), Response1C.class)
                .ifOk( value -> log.info( "getContractor(): OK " ) )
                .ifError( result -> log.warn( "getContractor(): Can`t get contractor={}. {}", contractor, result ))
                .map(response -> jsonMapper.convertValue(response.getValue(), new TypeReference<List<Contractor1C>>() {}));
    }

    @Override
    public Result<Contractor1C> saveContractor(Contractor1C contractor) {
        log.debug("saveContractor(): contractor={}", contractor);

        try {
            return client.save(buildSaveContractorUrl(), jsonMapper.writeValueAsString(contractor), Contractor1C.class)
                    .ifOk(value -> log.info("saveContractor(): OK "))
                    .ifError(result -> log.warn("saveContractor(): Can`t save contractor={}. {}", contractor, result));
        } catch (JsonProcessingException e){
            log.error("saveContractor(): failed to serialize contractor", e);
            return Result.error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    @Override
    public Result<List<Country1C>> getCountries(Country1C country) {
        log.debug("getCountry(): country={}", country);

        return client.read(buildGetCountryUrl(country), Response1C.class)
                .ifOk( value -> log.info( "getCountry(): OK " ) )
                .ifError( result -> log.warn( "getCountry(): Can`t get country={}. {}", country, result ))
                .map(response -> jsonMapper.convertValue(response.getValue(), new TypeReference<List<Country1C>>() {}));
    }

    @Override
    public Result<List<Country1C>> getCountryVocabulary() {
        log.debug("getCountryVocabulary()");

        Country1C country = new Country1C();
        country.setDeletionMark(false);
        return getCountries(country);
    }

    private String buildGetContractorUrl(Contractor1C contractor){
        String url = buildCommonUrl(contractor.getClass());
        url += "&" + URL_PARAM_SELECT + String.join(",", fieldsMapper.getFields(Contractor1C.class));
        url += "&" + URL_PARAM_FILTER + buildFilter(contractor.getClass(), contractor);

        log.debug("buildGetContractorUrl(): url={}", replaceSpaces(url));

        return replaceSpaces(url);
    }

    private String buildSaveContractorUrl(){
        String url = buildCommonUrl(Contractor1C.class);
        log.debug("buildSaveContractorUrl(): url={}", url);
        return url;
    }

    private String buildGetCountryUrl(Country1C country1C){
        String url = buildCommonUrl(country1C.getClass());
        url += "&" + URL_PARAM_SELECT + String.join(",", fieldsMapper.getFields(Country1C.class));
        url += "&" + URL_PARAM_FILTER + buildFilter(country1C.getClass(), country1C);

        log.debug("buildGetCountryUrl(): url={}", replaceSpaces(url));

        return replaceSpaces(url);
    }

    private String buildCommonUrl (Class<?> clazz){
        String url = getBaseUrl();
        UrlName1C annotation = clazz.getAnnotation(UrlName1C.class);

        if (annotation == null || StringUtils.isEmpty(annotation.value())){
            log.error("buildCommonUrl(): class={} does not have annotation @UrlName1C", clazz.getSimpleName());
            return "";
        }

        url += "/" + annotation.value() + "?" + URL_PARAM_FORMAT;
        return url;
    }

    private String buildFilter(Class<?> clazz, Object object){
        List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        List<String> params = new ArrayList<>();

        try {
            for (Field field : fields) {
                field.setAccessible(true);

                String fieldName = fieldsMapper.getField(field);
                String fieldValue = field.get(object) == null ? null : String.valueOf(field.get(object));

                if (fieldName != null && StringUtils.isNotEmpty(fieldValue)){
                    params.add(fieldName
                            + " eq "
                            + (fieldsMapper.isId1C(field) ? "guid" :  "")
                            + (field.getType() == Boolean.class ? fieldValue : "'" + fieldValue + "'") );
                }
            }
        } catch (Exception e){
            log.error("buildFiler(): failed to read params", e);
            return "";
        }
        return String.join(" and ", params);
    }

    private String replaceSpaces(String s){
        return s.replaceAll(" ", "%20");
    }

    private String getBaseUrl() {
        return config.data().enterprise1C().getApiBaseUrl();
    }

    @Autowired
    PortalConfig config;
    @Autowired
    HttpClient1C client;
    @Autowired
    FieldsMapper1C fieldsMapper;

    private ObjectMapper jsonMapper = new ObjectMapper();

    private final String URL_PARAM_FORMAT = "$format=json;odata=nometadata";
    private final String URL_PARAM_SELECT = "$select=";
    private final String URL_PARAM_FILTER = "$filter=";

    private final static Logger log = LoggerFactory.getLogger(Api1CImpl.class);
}
