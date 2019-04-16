package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.model.dao.AuditObjectDAO;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.AuditQuery;
import ru.protei.portal.core.model.struct.AuditObject;
import ru.protei.portal.core.service.AuditService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;
import java.util.List;

/**
 * Created by butusov on 07.08.17.
 */
public class AuditServiceTest {

    static ApplicationContext ctx;

    @BeforeClass
    public static void init() {
        ctx = new AnnotationConfigApplicationContext( CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, MainTestsConfiguration.class );
    }

    @Test
    public void testCreateAndGetAudit() {

        AuditObject auditObject = new AuditObject();

        auditObject.setCreatorShortName( "Test creator" );
        auditObject.setCreated( new Date() );
        auditObject.setCreatorId( 1L );
        auditObject.setCreatorIp( "test IP" );
        auditObject.setTypeId( En_AuditType.PRODUCT_CREATE.getId() );

        DevUnit product = new DevUnit();

        product.setName( "Test Product" );
        product.setCreated( new Date() );
        product.setCreatorId( 1L );
        product.setInfo( "Unit-test" );
        product.setStateId( En_DevUnitState.ACTIVE.getId() );
        product.setTypeId( En_DevUnitType.PRODUCT.getId() );

        auditObject.setEntryInfo( product );

        Long id = ctx.getBean( AuditObjectDAO.class ).insertAudit( auditObject );
        Assert.assertNotNull( id );

        AuditQuery auditQuery = new AuditQuery(  );
        auditQuery.setId( id );
        CoreResponse< List< AuditObject > > result = ctx.getBean( AuditService.class ).auditObjectList( auditQuery );

        Assert.assertNotNull( result );
        Assert.assertTrue( result.getDataAmountTotal() > 0 );

        Assert.assertNotNull( result.getData() );
        Assert.assertTrue( result.getData().size() == 1 );

        Assert.assertTrue( result.getData().get( 0 ).getEntryInfo() instanceof DevUnit );

        Assert.assertTrue( ((DevUnit)result.getData().get( 0 ).getEntryInfo()).getName().equals( "Test Product" ) );

        System.out.println( result.getData().get( 0 ).getEntryInfo() );

        Assert.assertNotNull( ctx.getBean( AuditObjectDAO.class ).remove( auditObject ) );
    }
}
