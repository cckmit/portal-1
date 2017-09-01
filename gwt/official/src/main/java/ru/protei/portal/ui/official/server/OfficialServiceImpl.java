package ru.protei.portal.ui.official.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.OfficialQuery;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by serebryakov on 22/08/17.
 */
@Service("OfficialService")
public class OfficialServiceImpl implements ru.protei.portal.ui.common.client.service.OfficialService {

    @Override
    public Official getOfficial(Long id) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< Official > response = officialService.getOfficial( descriptor.makeAuthToken(), id );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();

    }

    @Override
    public Map<String, List<Official>> getOfficialsByRegions(OfficialQuery query) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< Map< String, List< Official > > > response = officialService.listOfficialsByRegions( descriptor.makeAuthToken(), query );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpServletRequest );
        if ( descriptor == null ) {
            throw new RequestFailedException( En_ResultStatus.SESSION_NOT_FOUND );
        }

        return descriptor;
    }

    @Override
    public OfficialMember getOfficialMember(Long id) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< OfficialMember > response = officialService.getOfficialMember( descriptor.makeAuthToken(), id );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();

    }

    @Override
    public OfficialMember saveOfficialMember(OfficialMember officialMember) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<OfficialMember> response = officialService.saveOfficialMember( descriptor.makeAuthToken(), officialMember );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public Long createOfficial(Official official) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        Long creatorId = sessionService.getUserSessionDescriptor( httpServletRequest ).getPerson().getId();

        CoreResponse< Long > response = officialService.createOfficial( descriptor.makeAuthToken(), official, creatorId );
        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public Official updateOfficial(Official official) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Official> response = officialService.updateOfficial( descriptor.makeAuthToken(), official );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }


    @Autowired
    private ru.protei.portal.core.service.OfficialService officialService;
    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;

    private static Logger log = LoggerFactory.getLogger(ru.protei.portal.core.service.OfficialServiceImpl.class);

}
