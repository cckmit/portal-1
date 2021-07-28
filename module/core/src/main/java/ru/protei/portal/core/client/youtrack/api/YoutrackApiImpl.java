package ru.protei.portal.core.client.youtrack.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClient;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoObjectMapperProvider;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.youtrack.YtFieldDescriptor;
import ru.protei.portal.core.model.youtrack.dto.activity.YtActivityCategory;
import ru.protei.portal.core.model.youtrack.dto.activity.YtActivityItem;
import ru.protei.portal.core.model.youtrack.dto.activity.singlevalue.YtWorkItemDurationActivityItem;
import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtBundleElement;
import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtEnumBundleElement;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.YtIssueCustomField;
import ru.protei.portal.core.model.youtrack.dto.filterfield.YtFilterField;
import ru.protei.portal.core.model.youtrack.dto.issue.*;
import ru.protei.portal.core.model.youtrack.dto.project.YtProject;
import ru.protei.portal.core.model.youtrack.dto.user.YtUser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class YoutrackApiImpl implements YoutrackApi {

    @Override
    public Result<YtEnumBundleElement> createCompany(YtEnumBundleElement company) {
        return create(new YoutrackRequest<>(YtEnumBundleElement.class)
                .url(new YoutrackUrlProvider(getBaseUrl()).fieldDefaultsBundle(config.data().youtrack().getYoutrackCustomFieldCompanyId()))
                .save(company));
    }

    @Override
    public Result<YtEnumBundleElement> updateCompany(String companyId, YtEnumBundleElement company) {
        return update(new YoutrackRequest<>(YtEnumBundleElement.class)
                .url(new YoutrackUrlProvider(getBaseUrl()).fieldDefaultsValue(config.data().youtrack().getYoutrackCustomFieldCompanyId(), companyId))
                .save(company));
    }

    @Override
    public Result<List<YtEnumBundleElement>> getCompanyByName(String companyName) {
        return read(new YoutrackRequest<>(YtEnumBundleElement[].class)
                .url(new YoutrackUrlProvider(getBaseUrl()).fieldDefaultsBundle(config.data().youtrack().getYoutrackCustomFieldCompanyId()))
                .fillSimpleFields()
                .fillYtFields(YtBundleElement.class)
                .query(companyName))
                .map(Arrays::asList);
    }

    @Override
    public Result<YtIssue> createIssueAndReturnId(YtIssue issue) {
        return create(new YoutrackRequest<>(YtIssue.class)
                .url(new YoutrackUrlProvider(getBaseUrl()).issues())
                .save(issue));
    }

    @Override
    public Result<YtIssue> getIssueWithFieldsCommentsAttachments(String issueId) {
        return read(new YoutrackRequest<>(YtIssue.class)
                .url(new YoutrackUrlProvider(getBaseUrl()).issue(issueId))
                .fillSimpleFields()
                .fillYtFields(YtIssueComment.class, YtIssueAttachment.class, YtUser.class, YtIssueCustomField.class));
    }

    @Override
    public Result<YtIssue> updateIssueAndReturnWithFieldsCommentsAttachments(String issueId, YtIssue issue) {
        return update(new YoutrackRequest<>(YtIssue.class)
                .url(new YoutrackUrlProvider(getBaseUrl()).issue(issueId))
                .fillSimpleFields()
                .fillYtFields(YtIssueCustomField.class, YtIssueComment.class, YtIssueAttachment.class)
                .save(issue));
    }

    @Override
    public Result<YtIssue> removeIssueFieldAndReturnWithFieldsCommentsAttachments(String issueId, YtIssue issue, YtFieldDescriptor...fieldNamesToRemove) {
        return remove(new YoutrackRequest<>(YtIssue.class)
                .url(new YoutrackUrlProvider(getBaseUrl()).issue(issueId))
                .fillSimpleFields()
                .fillYtFields(YtIssueCustomField.class, YtIssueComment.class, YtIssueAttachment.class)
                .remove(issue, fieldNamesToRemove));
    }

    @Override
    public Result<List<YtProject>> getProjectIdByName(String projectName) {
        return read(new YoutrackRequest<>(YtProject[].class)
                .url(new YoutrackUrlProvider(getBaseUrl()).projects())
                .query(projectName))
                .map(Arrays::asList);
    }

    @Override
    public Result<List<YtIssue>> getIssueIdsByQuery(String query) {
        return read(new YoutrackRequest<>(YtIssue[].class)
                .url(new YoutrackUrlProvider(getBaseUrl()).issues())
                .query(query))
                .map(Arrays::asList);
    }

    @Override
    public Result<List<IssueWorkItem>> getWorkItems(Date from, Date to, int offset, int limit) {
        log.debug("getWorkItems(): from ={}, to={}, offset={}, limit={}", from, to, offset, limit);
        return read(new YoutrackRequest<>(IssueWorkItem[].class)
                .url(new YoutrackUrlProvider(getBaseUrl()).workItems())
                .params(new HashMap<String, String>() {{
                    if (from != null) put("start", String.valueOf(from.getTime()));
                    if (to != null) put("end", String.valueOf(to.getTime()));
                    put("$skip", String.valueOf(offset));
                    put("$top", String.valueOf(limit));
                }})
                .fillSimpleFields()
                .fillYtFields(YtWorkItemDurationActivityItem.class, IssueWorkItem.class, YtUser.class, DurationValue.class, YtIssue.class, YtIssueCustomField.class, YtProject.class)
        ).map(Arrays::asList);
    }

    @Override
    public Result<List<YtActivityItem>> getIssueCustomFieldActivityItems(String issueId) {
        return read(new YoutrackRequest<>(YtActivityItem[].class)
                .url(new YoutrackUrlProvider(getBaseUrl()).issueActivities(issueId))
                .params(new HashMap<String, String>() {{
                    put("categories", YtActivityCategory.CustomFieldCategory.getCategoryId());
                }})
                .fillSimpleFields()
                .fillYtFields(YtFilterField.class, YtBundleElement.class, YtUser.class))
                .map(Arrays::asList);
    }

    private <RES> Result<RES> read(YoutrackRequest<?, RES> request) {
        ensureFieldsParamSet(request);
        String url = buildUrl(request.getUrl(), request.getParams());
        return client.read(url, request.getResponse());
    }

    private <REQ, RES> Result<RES> create(YoutrackRequest<REQ, RES> request) {
        ensureFieldsParamSet(request);
        String url = buildUrl(request.getUrl(), request.getParams());
        return serializeDto(request.getRequest())
            .flatMap(body -> client.save(url, body, request.getResponse()));
    }

    private <REQ, RES> Result<RES> update(YoutrackRequest<REQ, RES> request) {
        ensureFieldsParamSet(request);
        String url = buildUrl(request.getUrl(), request.getParams());
        return serializeDto(request.getRequest())
            .flatMap(body -> client.save(url, body, request.getResponse()));
    }

    private <REQ, RES> Result<RES> remove(YoutrackRequest<REQ, RES> request) {
        ensureFieldsParamSet(request);
        String url = buildUrl(request.getUrl(), request.getParams());
        return serializeDto(request.getRequest(), request.getRequestFieldsToRemove())
            .flatMap(body -> client.save(url, body, request.getResponse()));
    }

    private void ensureFieldsParamSet(YoutrackRequest<?, ?> request) {
        if (!request.getParams().containsKey("fields")) {
            String fields = fieldsMapper.getFields(
                    request.getResponse(),
                    request.getResponseIncludeNotYtDtoFields(),
                    request.getResponseIncludeYtDtoFields()
            );
            request.getParams().put("fields", fields);
        }
    }

    private <T> Result<String> serializeDto(T dto, YtFieldDescriptor...forceIncludeFields) {
        try {
            List<YtFieldDescriptor> includeFields = forceIncludeFields == null
                    ? Collections.emptyList()
                    : Arrays.asList(forceIncludeFields);
            String body = YtDtoObjectMapperProvider.getMapper(fieldsMapper)
                    .writer(YtDtoObjectMapperProvider.getFilterProvider(includeFields))
                    .writeValueAsString(dto);
            return ok(body);
        } catch (JsonProcessingException e) {
            log.error(String.format("Failed to serialize dto : [%s]", dto), e);
            return error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    private String buildUrl(String base, Map<String, String> params) {
        List<String> queryList = new ArrayList<>();
        for (Map.Entry<String, String> entry : CollectionUtils.emptyIfNull(params).entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
                queryList.add(key + "=" + urlEncode(value));
            }
        }
        String url = base;
        if (CollectionUtils.isNotEmpty(queryList)) {
            url += "?" + String.join("&", queryList);
        }
        return url;
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("urlEncode(): Failed to urlencode (" + value + ")", e);
            return value;
        }
    }

    private String getBaseUrl() {
        return config.data().youtrack().getApiBaseUrl() + "/api";
    }

    @Autowired
    PortalConfig config;
    @Autowired
    YoutrackHttpClient client;
    @Autowired
    YtDtoFieldsMapper fieldsMapper;

    private final static Logger log = LoggerFactory.getLogger(YoutrackApiImpl.class);
}
