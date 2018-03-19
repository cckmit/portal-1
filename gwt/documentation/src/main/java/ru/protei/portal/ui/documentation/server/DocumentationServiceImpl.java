package ru.protei.portal.ui.documentation.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.DocumentationQuery;
import ru.protei.portal.ui.common.client.service.DocumentationService;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("DocumentationService")
public class DocumentationServiceImpl implements DocumentationService {
    @Override
    public List<Documentation> getDocumentations(DocumentationQuery query) throws RequestFailedException {
        log.debug("get documentations: offset={} | limit={}",
                query.getOffset(), query.getLimit());

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<List<Documentation>> response = documentationService.documentationList(descriptor.makeAuthToken(), query);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Documentation getDocumentation(Long id) throws RequestFailedException {
        log.debug("get documentation, id: {}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Documentation> response = documentationService.getDocumentation(descriptor.makeAuthToken(), id);
        log.debug("get documentation, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Documentation saveDocumentation(Documentation documentation) throws RequestFailedException {
        if (documentation == null) {
            log.warn("null documentation in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        log.debug("save documentation, id: {}", HelperFunc.nvlt(documentation.getId(), "new"));

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Documentation> response = documentationService.saveDocumentation(descriptor.makeAuthToken(), documentation);

        log.debug("save documentation, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.debug("save role, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Long getDocumentationCount(DocumentationQuery query) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug("get documentation count(): query={}", query);
        return documentationService.count(descriptor.makeAuthToken(), query).getData();
    }

    @Override
    public List<DocumentType> getDocumentTypeList() throws RequestFailedException {
        log.debug("get document type list");

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<List<DocumentType>> response = documentationService.documentTypeList(descriptor.makeAuthToken());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor(httpRequest);
        log.info("userSessionDescriptor={}", descriptor);
        if (descriptor == null) {
            throw new RequestFailedException(En_ResultStatus.SESSION_NOT_FOUND);
        }

        return descriptor;
    }


    @Autowired
    ru.protei.portal.core.service.DocumentationService documentationService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    private static final Logger log = LoggerFactory.getLogger("web");
}
