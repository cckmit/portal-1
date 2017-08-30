package ru.protei.portal.ui.official.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.OfficialQuery;
import ru.protei.portal.core.model.view.EntityOption;
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
    public OfficialMember getOfficialMember(Long id) {
        for (Official official: officialList) {
            List<OfficialMember> members = official.getMembers();
            for (OfficialMember member: members) {
                if (member.getId() == id) {
                    return member;
                }
            }
        }
        return null;
    }

    @Override
    public void saveOfficialMember(OfficialMember officialMember) {
        for (Official official: officialList) {
            List<OfficialMember> members = official.getMembers();
            for (OfficialMember member: members) {
                if (member.getId() == officialMember.getId()) {
                    int index = members.indexOf(member);
                    members.set(index, officialMember);
                }
            }
        }
    }


    private List<Official> officialList = new ArrayList<>();

    @Autowired
    private ru.protei.portal.core.service.OfficialService officialService;
    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;

}
