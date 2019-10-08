package ru.protei.portal.ui.official.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.OfficialQuery;
import ru.protei.portal.core.service.OfficialService;
import ru.protei.portal.ui.common.client.service.OfficialController;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by serebryakov on 22/08/17.
 */
@Service("OfficialController")
public class OfficialControllerImpl implements OfficialController {

    @Override
    public Official getOfficial(Long id) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result< Official > response = officialService.getOfficial( descriptor.makeAuthToken(), id );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();

    }

    @Override
    public Map<String, List<Official>> getOfficialsByRegions(OfficialQuery query) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result< Map< String, List< Official > > > response = officialService.listOfficialsByRegions( descriptor.makeAuthToken(), query );
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

        Result< OfficialMember > response = officialService.getOfficialMember( descriptor.makeAuthToken(), id );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();

    }

    @Override
    public Long createOfficialMember(OfficialMember officialMember, Long parentId) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result< Long > response = officialService.createOfficialMember( descriptor.makeAuthToken(), officialMember, parentId);
        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();

    }

    @Override
    public OfficialMember saveOfficialMember(OfficialMember officialMember) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result<OfficialMember> response = officialService.saveOfficialMember( descriptor.makeAuthToken(), officialMember );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public Long createOfficial(Official official) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        Long creatorId = sessionService.getUserSessionDescriptor( httpServletRequest ).getPerson().getId();

        Result< Long > response = officialService.createOfficial( descriptor.makeAuthToken(), official, creatorId );
        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public Official updateOfficial(Official official) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result<Official> response = officialService.updateOfficial( descriptor.makeAuthToken(), official );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public boolean removeOfficial(Long id) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result< Boolean > response = officialService.removeOfficial( descriptor.makeAuthToken(), id);
        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public boolean removeOfficialMember(Long id) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result< Boolean > response = officialService.removeOfficialMember( descriptor.makeAuthToken(), id);
        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }


    @Autowired
    private OfficialService officialService;
    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;

    private static Logger log = LoggerFactory.getLogger(ru.protei.portal.core.service.OfficialServiceImpl.class);

}
