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
import ru.protei.portal.core.model.dict.lang.En_1CParamType;
import ru.protei.portal.core.model.enterprise1c.annotation.UrlName1C;
import ru.protei.portal.core.model.enterprise1c.dto.Country1C;
import ru.protei.portal.core.model.enterprise1c.Response1C;
import ru.protei.portal.core.model.enterprise1c.dto.Contractor1C;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.ContractorUtils;
import ru.protei.portal.core.model.util.CrmConstants;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class Api1CImpl implements Api1C{

    @Override
    public Result<List<Contractor1C>> getContractors(Contractor1C contractor, String homeCompanyName) {
        log.debug("getContractor(): contractor={}, homeCompanyName={}", contractor, homeCompanyName);

        if (contractor == null || homeCompanyName == null){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return client.read(buildGetContractorUrl(contractor, homeCompanyName), Response1C.class)
                .ifOk( value -> log.info( "getContractor(): OK " ) )
                .ifError( result -> log.warn( "getContractor(): Can`t get contractor={}. {}", contractor, result ))
                .map(response -> jsonMapper.convertValue(response.getValue(), new TypeReference<List<Contractor1C>>() {}));
    }

    @Override
    public Result<Contractor1C> saveContractor(Contractor1C contractor, String homeCompanyName) {
        log.debug("saveContractor(): contractor={}, homeCompanyName={}", contractor, homeCompanyName);

        if (!validateContractor(contractor)){
            log.warn("saveContractor(): contractor is not valid");
            return Result.error(En_ResultStatus.INCORRECT_PARAMS);
        }

        contractor.setComment(CONTRACTOR_COMMENT);
        if (CrmConstants.Company.PROTEI_ST_HOME_COMPANY_NAME.equals(homeCompanyName)) {
            contractor.setParentKey(config.data().enterprise1C().getParentKeyST());
        }

        if (CrmConstants.Company.MAIN_HOME_COMPANY_NAME.equals(homeCompanyName)){
            if (checkResident(contractor,homeCompanyName)){
                contractor.setParentKey(config.data().enterprise1C().getParentKeyResident());
            } else {
                contractor.setParentKey(config.data().enterprise1C().getParentKeyNotResident());
            }
        }

        try {
            return client.save(buildSaveContractorUrl(homeCompanyName), jsonMapper.writeValueAsString(contractor), Contractor1C.class)
                    .ifOk(value -> log.info("saveContractor(): OK "))
                    .ifError(result -> log.warn("saveContractor(): Can`t save contractor={}. {}", contractor, result));
        } catch (JsonProcessingException e){
            log.error("saveContractor(): failed to serialize contractor", e);
            return Result.error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    @Override
    public Result<List<Country1C>> getCountries(Country1C country, String homeCompanyName) {
        log.debug("getCountry(): country={}, homeCompanyName={}", country, homeCompanyName);

        if (country == null || homeCompanyName == null){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return client.read(buildGetCountryUrl(country, homeCompanyName), Response1C.class)
                .ifOk( value -> log.info( "getCountry(): OK " ) )
                .ifError( result -> log.warn( "getCountry(): Can`t get country={}. {}", country, result ))
                .map(response -> jsonMapper.convertValue(response.getValue(), new TypeReference<List<Country1C>>() {}));
    }

    @Override
    public Result<List<Country1C>> getCountryVocabulary(String homeCompanyName) {
        log.debug("getCountryVocabulary()");

        if (homeCompanyName == null){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Country1C country = new Country1C();
        country.setDeletionMark(false);
        return getCountries(country, homeCompanyName);
    }

    @Override
    public Result<Boolean> isResident(Contractor1C contractor, String homeCompanyName){
        log.debug("isResident(): contractor={}, homeCompanyName={}", contractor, homeCompanyName);

        if (contractor == null || homeCompanyName == null){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return ok(checkResident(contractor, homeCompanyName));
    }

    private boolean checkResident(Contractor1C contractor, String homeCompanyName){
        log.debug("checkResident(): contractor={}, homeCompanyName={}", contractor, homeCompanyName);

        Country1C country = new Country1C();
        country.setRefKey(contractor.getRegistrationCountryKey());

        Result<List<Country1C>> result = getCountries(country, homeCompanyName);

        if (result.isError()){
            return false;
        }

        if (result.getData() == null || result.getData().size() != 1){
            return false;
        }

        return CrmConstants.Company.HOME_COUNTRY.equalsIgnoreCase(result.getData().get(0).getFullName());
    }

    private boolean validateContractor(Contractor1C contractor) {
        if (contractor == null){
            log.warn("validateContractor(): contractor is null");
            return false;
        }

        if (!checkText(CrmConstants.Masks.CONTRACTOR_NAME, contractor.getName())){
            log.warn("validateContractor(): contractor name not valid");
            return false;
        }

        if (!checkText(CrmConstants.Masks.CONTRACTOR_FULLNAME, contractor.getFullName())){
            log.warn("validateContractor(): contractor full name not valid");
            return false;
        }

        if (!ContractorUtils.checkInn(contractor.getInn())){
            log.warn("validateContractor(): contractor inn is not correct");
            return false;
        }

        if (!checkText(CrmConstants.Masks.CONTRACTOR_KPP, contractor.getKpp())){
            log.warn("validateContractor(): contractor full name not valid");
            return false;
        }

        if (StringUtils.isEmpty(contractor.getRegistrationCountryKey())){
            log.warn("validateContractor(): contractor country key is mandatory");
            return false;
        }

        return true;
    }

    private String buildGetContractorUrl(Contractor1C contractor, String homeCompanyName){
        String url = buildCommonUrl(contractor.getClass(), homeCompanyName);
        url += "&" + URL_PARAM_SELECT + String.join(",", fieldsMapper.getFields(Contractor1C.class));
        url += "&" + URL_PARAM_FILTER + fillFilter(contractor.getClass(), contractor);

        log.debug("buildGetContractorUrl(): url={}", replaceSpaces(url));

        return replaceSpaces(url);
    }

    private String buildSaveContractorUrl(String homeCompanyName){
        String url = buildCommonUrl(Contractor1C.class, homeCompanyName);
        log.debug("buildSaveContractorUrl(): url={}", url);
        return url;
    }

    private String buildGetCountryUrl(Country1C country1C, String homeCompanyName){
        String url = buildCommonUrl(country1C.getClass(), homeCompanyName);
        url += "&" + URL_PARAM_SELECT + String.join(",", fieldsMapper.getFields(Country1C.class));
        url += "&" + URL_PARAM_FILTER + fillFilter(country1C.getClass(), country1C);

        log.debug("buildGetCountryUrl(): url={}", replaceSpaces(url));

        return replaceSpaces(url);
    }

    private String buildCommonUrl (Class<?> clazz, String homeCompanyName){
        String url = getBaseUrl(homeCompanyName);
        UrlName1C annotation = clazz.getAnnotation(UrlName1C.class);

        if (annotation == null || StringUtils.isEmpty(annotation.value())){
            log.error("buildCommonUrl(): class={} does not have annotation @UrlName1C", clazz.getSimpleName());
            return "";
        }

        url += "/" + annotation.value() + "?" + URL_PARAM_FORMAT;
        return url;
    }

    private String fillFilter(Class<?> clazz, Object object){
        List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        List<String> params = new ArrayList<>();

        try {
            for (Field field : fields) {
                field.setAccessible(true);

                String fieldName = fieldsMapper.getField(field);
                String fieldValue = field.get(object) == null ? null : String.valueOf(field.get(object));
                En_1CParamType filedType = fieldsMapper.getSpecialParamType(field);

                if (fieldName != null && StringUtils.isNotEmpty(fieldValue)){
                    if (filedType == null){
                        params.add(fieldName
                                + " eq "
                                + (field.getType() == Boolean.class ? fieldValue : "'" + fieldValue + "'"));
                    }
                    else {
                        switch (filedType) {
                            case ID:
                                params.add(fieldName
                                        + " eq guid"
                                        + "'" + fieldValue + "'");
                                break;

                            case TEXT:
                                params.add(
                                        "substringof("
                                                + "'" + fieldValue + "'"
                                                + "," + fieldName
                                                + ") eq true");

                                break;
                        }
                    }
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

    private String getBaseUrl(String homeCompanyName) {
        if (CrmConstants.Company.PROTEI_ST_HOME_COMPANY_NAME.equals(homeCompanyName)) {
            return config.data().enterprise1C().getApiBaseProteiStUrl();
        }

        if (CrmConstants.Company.MAIN_HOME_COMPANY_NAME.equals(homeCompanyName)) {
            return config.data().enterprise1C().getApiBaseProteiUrl();
        }

        return "";
    }

    private boolean checkText(String regexp, String text){
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    @Autowired
    PortalConfig config;
    @Autowired
    HttpClient1C client;
    @Autowired
    FieldsMapper1C fieldsMapper;

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private final String URL_PARAM_FORMAT = "$format=json;odata=nometadata";
    private final String URL_PARAM_SELECT = "$select=";
    private final String URL_PARAM_FILTER = "$filter=";

    private final String CONTRACTOR_COMMENT = "Создано порталом";

    private final static Logger log = LoggerFactory.getLogger(Api1CImpl.class);
}
