package ru.protei.portal.core.client.enterprise1c.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.enterprise1c.http.HttpClient1C;
import ru.protei.portal.core.client.enterprise1c.mapper.FieldsMapper1C;
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
    public Result<List<Contractor1C>> getContractor(Contractor1C contractor1C) {
        return Result.ok((List<Contractor1C>) client.read(buildGetContractorUrl(contractor1C), Response1C.class).getData().getValue());
    }

    @Override
    public Result<Contractor1C> saveContractor(Contractor1C contractor1C) throws JsonProcessingException {
        return Result.ok((Contractor1C) client.save(buildSaveContractorUrl(), jsonMapper.writeValueAsString(contractor1C), Response1C.class).getData().getValue());
    }

    @Override
    public Result<List<Country1C>> getCountry(Country1C Country1C) {
        return Result.ok((List<Country1C>) client.read(buildGetCountryUrl(Country1C), Response1C.class).getData().getValue());
    }

    @Override
    public Result<List<Country1C>> getCountryVocabulary() {
        Country1C country1C = new Country1C();
        country1C.setDeletionMark(false);
        return getCountry(country1C);
    }

    private String buildGetContractorUrl(Contractor1C contractor){
        String url = buildCommonUrl(contractor.getClass());
        url += URL_PARAM_SELECT + String.join(",", fieldsMapper.getFields(Contractor1C.class));
        url += URL_PARAM_FILTER + buildFiler(contractor.getClass(), contractor);

        return replaceSpaces(url);
    }

    private String buildSaveContractorUrl(){
        return buildCommonUrl(Contractor1C.class);
    }

    private String buildGetCountryUrl(Country1C country1C){
        String url = buildCommonUrl(country1C.getClass());
        url += URL_PARAM_SELECT + String.join(",", fieldsMapper.getFields(Contractor1C.class));
        url += URL_PARAM_FILTER + buildFiler(country1C.getClass(), country1C);

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

    private String buildFiler(Class<?> clazz, Object object){
        List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        List<String> params = new ArrayList<>();

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (fieldsMapper.getField(field) != null && StringUtils.isNotEmpty((String)field.get(object))){
                    params.add(fieldsMapper.getField(field) + " eq'" + field.get(object) + "'");
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
