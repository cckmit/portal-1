package ru.protei.portal.ui.official.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.ent.OfficialMember;
import ru.protei.portal.core.model.query.OfficialQuery;
import ru.protei.portal.core.service.OfficialService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.OfficialController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by serebryakov on 22/08/17.
 */
@Service("OfficialController")
public class OfficialControllerImpl implements OfficialController {

    @Override
    public Official getOfficial(Long id) throws RequestFailedException {

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< Official > response = officialService.getOfficial( token, id );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();

    }

    @Override
    public Map<String, List<Official>> getOfficialsByRegions(OfficialQuery query) throws RequestFailedException {

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< Map< String, List< Official > > > response = officialService.listOfficialsByRegions( token, query );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public OfficialMember getOfficialMember(Long id) throws RequestFailedException {

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< OfficialMember > response = officialService.getOfficialMember( token, id );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();

    }

    @Override
    public Long createOfficialMember(OfficialMember officialMember, Long parentId) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< Long > response = officialService.createOfficialMember( token, officialMember, parentId);
        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();

    }

    @Override
    public OfficialMember saveOfficialMember(OfficialMember officialMember) throws RequestFailedException {

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<OfficialMember> response = officialService.saveOfficialMember( token, officialMember );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public Long createOfficial(Official official) throws RequestFailedException {

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Long creatorId = token.getPersonId();

        Result< Long > response = officialService.createOfficial( token, official, creatorId );
        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public Official updateOfficial(Official official) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Official> response = officialService.updateOfficial( token, official );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public boolean removeOfficial(Long id) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< Boolean > response = officialService.removeOfficial( token, id);
        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public boolean removeOfficialMember(Long id) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< Boolean > response = officialService.removeOfficialMember( token, id);
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
