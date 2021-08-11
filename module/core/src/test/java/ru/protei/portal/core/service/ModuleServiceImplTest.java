package ru.protei.portal.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import ru.protei.portal.test.service.BaseServiceTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PortalConfigTestConfiguration.class,
        ServiceTestsConfiguration.class,
        DaoMockTestConfiguration.class,
        ServiceTestsConfiguration.class,
        TestEventConfiguration.class
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
        Set<Long> modulesIdsToRemove = new HashSet<>();
        modulesIdsToRemove.add(1L);
        modulesIdsToRemove.add(2L);

        int removedModulesExpectedCount = modulesIdsToRemove.size();

        when(moduleDAO.getListByKitId(anyLong())).thenReturn(new ArrayList<>());
        when(caseObjectDAO.removeByKeys(anySet())).thenReturn(removedModulesExpectedCount);

        Result<Set<Long>> result = moduleService.removeModules(getAuthToken(), anyLong(), modulesIdsToRemove);

        verify(caseObjectDAO, atLeastOnce()).removeByKeys(modulesIdsToRemove);
        assertTrue("Expected removed modules count is equal or bigger than modules ids count",
                    removedModulesExpectedCount >= result.getData().size());
    }

    @Test
    public void getErrorWhenRemovedLessModulesThanExpected() {
        Set<Long> modulesIdsToRemove = new HashSet<>();
        modulesIdsToRemove.add(1L);
        modulesIdsToRemove.add(2L);

        int removedModulesExpectedCount = modulesIdsToRemove.size();

        when(moduleDAO.getListByKitId(anyLong())).thenReturn(new ArrayList<>());
        when(caseObjectDAO.removeByKeys(anySet())).thenReturn(removedModulesExpectedCount - 1);

        Result<Set<Long>> result = moduleService.removeModules(getAuthToken(), anyLong(), modulesIdsToRemove);

        verify(caseObjectDAO, atLeastOnce()).removeByKeys(modulesIdsToRemove);
        assertTrue("Removed less modules than expected", result.isError());
    }
}