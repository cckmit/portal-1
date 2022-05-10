package ru.protei.portal.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.DaoMockTestConfiguration;
import ru.protei.portal.config.PortalConfigTestConfiguration;
import ru.protei.portal.config.ServiceTestsConfiguration;
import ru.protei.portal.config.TestEventConfiguration;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ModuleDAO;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.sn.remote_services.configuration.RemoteServiceFactory;
import ru.protei.winter.http.HttpConfigurationContext;
import ru.protei.winter.http.client.factory.HttpClientFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PortalConfigTestConfiguration.class,
        ServiceTestsConfiguration.class,
        DaoMockTestConfiguration.class,
        ServiceTestsConfiguration.class,
        TestEventConfiguration.class,
        RemoteServiceFactory.class,
        HttpClientFactory.class,
        HttpConfigurationContext.class
})
public class ModuleServiceImplTest extends BaseServiceTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Autowired
    ModuleDAO moduleDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    ModuleService moduleService;

    @Test
    public void modulesRemove() {
        Set<Long> modulesToRemoveIds = new HashSet<>();
        modulesToRemoveIds.add(1L);
        modulesToRemoveIds.add(2L);

        when(moduleDAO.getListByKitId(anyLong())).thenReturn(new ArrayList<>());

        ArgumentCaptor<CaseObject> caseObjectArgumentCaptor = forClass(CaseObject.class);
        when(caseObjectDAO.partialMerge(caseObjectArgumentCaptor.capture(), eq("deleted"))).thenReturn(true);
        when(moduleDAO.removeByKeys(anySet())).thenReturn(modulesToRemoveIds.size());

        Result<Set<Long>> result = moduleService.removeModules(getAuthToken(), anyLong(), modulesToRemoveIds);

        verify(caseObjectDAO, atLeastOnce()).partialMerge(caseObjectArgumentCaptor.capture(), eq("deleted"));
        verify(moduleDAO, atLeastOnce()).removeByKeys(anySet());
        assertTrue("Expected removed modules count is equal or bigger than modules ids count",
                    modulesToRemoveIds.size() >= result.getData().size());
    }

    @Test
    public void getErrorWhenRemovedLessModulesThanExpected() {
        Set<Long> modulesToRemoveIds = new HashSet<>();
        modulesToRemoveIds.add(1L);
        modulesToRemoveIds.add(2L);

        when(moduleDAO.getListByKitId(anyLong())).thenReturn(new ArrayList<>());

        ArgumentCaptor<CaseObject> caseObjectArgumentCaptor = forClass(CaseObject.class);
        when(caseObjectDAO.partialMerge(caseObjectArgumentCaptor.capture(), eq("deleted"))).thenReturn(true);
        when(moduleDAO.removeByKeys(anySet())).thenReturn(modulesToRemoveIds.size() - 1);

        Result<Set<Long>> result = moduleService.removeModules(getAuthToken(), anyLong(), modulesToRemoveIds);

        verify(caseObjectDAO, atLeastOnce()).partialMerge(caseObjectArgumentCaptor.capture(), eq("deleted"));
        verify(moduleDAO, atLeastOnce()).removeByKeys(anySet());
        assertTrue("Removed less modules than expected", result.isError());
    }
}