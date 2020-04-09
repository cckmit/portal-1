package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.JiraSLAMapEntry;
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/SLAController")
public interface SLAController extends RemoteService {

    List<JiraSLAMapEntry> getJiraSLAEntries(long mapId) throws RequestFailedException;

    JiraSLAMapEntry getJiraSLAEntry(long mapId, String issueType, String severity) throws RequestFailedException;

    List<ProjectSla> getSlaByPlatformId(Long platformId) throws RequestFailedException;
}
