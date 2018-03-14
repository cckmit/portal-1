package ru.protei.portal.ui.documentation.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
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
    public Long getDocumentationCount(DocumentationQuery query) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug("get documentation count(): query={}", query);
        return documentationService.count(descriptor.makeAuthToken(), query).getData();
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