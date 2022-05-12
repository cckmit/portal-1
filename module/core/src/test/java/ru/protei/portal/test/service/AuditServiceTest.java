package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dao.AuditObjectDAO;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.AuditQuery;
import ru.protei.portal.core.model.struct.AuditObject;
import ru.protei.portal.core.service.AuditService;
import ru.protei.sn.remote_services.configuration.RemoteServiceFactory;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.http.HttpConfigurationContext;
import ru.protei.winter.http.client.factory.HttpClientFactory;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;
import java.util.List;

/**
 * Created by butusov on 07.08.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class,
        DatabaseConfiguration.class, IntegrationTestsConfiguration.class, RemoteServiceFactory.class,
        HttpClientFactory.class, HttpConfigurationContext.class})
public class AuditServiceTest {

    @Test
    public void testCreateAndGetAudit() {

        AuditObject auditObject = new AuditObject();

        auditObject.setCreatorShortName( "Test creator" );
        auditObject.setCreated( new Date() );
        auditObject.setCreatorId( 1L );
        auditObject.setCreatorIp( "test IP" );
        auditObject.setType( En_AuditType.PRODUCT_CREATE );

        DevUnit product = new DevUnit();

        product.setName( "Test Product" );
        product.setCreated( new Date() );
        product.setCreatorId( 1L );
        product.setInfo( "Unit-test" );
        product.setStateId( En_DevUnitState.ACTIVE.getId() );
        product.setType( En_DevUnitType.PRODUCT );

        auditObject.setEntryInfo( product );

        Long id = auditObjectDAO.insertAudit( auditObject );
        Assert.assertNotNull( id );

        AuditQuery auditQuery = new AuditQuery(  );
        auditQuery.setId( id );
        Result< List< AuditObject > > result = auditService.auditObjectList( auditQuery );

        Assert.assertNotNull( result );

        Assert.assertNotNull( result.getData() );
        Assert.assertTrue( result.getData().size() == 1 );

        Assert.assertTrue( result.getData().get( 0 ).getEntryInfo() instanceof DevUnit );

        Assert.assertTrue( ((DevUnit)result.getData().get( 0 ).getEntryInfo()).getName().equals( "Test Product" ) );

        log.info( "{}", result.getData().get( 0 ).getEntryInfo() );

        Assert.assertTrue( auditObjectDAO.remove( auditObject ) );
    }

    @Autowired
    AuditService auditService;
    @Autowired
    AuditObjectDAO auditObjectDAO;

    private static final Logger log = LoggerFactory.getLogger(AuditServiceTest.class);
}
