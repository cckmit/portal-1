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
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.IpReservationService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class,
        DatabaseConfiguration.class, IntegrationTestsConfiguration.class
})
public class IpReservationServiceTest extends BaseServiceTest {

    @Test
    public void testSubnet(){
        /* create */
        Subnet subnet = createTestSubnet("255.255.255");
        Assert.assertNotNull(ipReservationService.createSubnet(getAuthToken(), subnet));

        /* update */
        subnet.setComment("Unit-test. Update subnet");
        Result updated = ipReservationService.updateSubnet(null, subnet);
        Assert.assertNotNull(updated);

        /* remove */
        Result<Long> result = ipReservationService.removeSubnet(null, subnet, true);
        Assert.assertNotNull(result);
    }

    protected Subnet createTestSubnet (String address){
        Subnet subnet = new Subnet();
        subnet.setAddress(address);
        subnet.setMask(CrmConstants.IpReservation.SUBNET_MASK);
        subnet.setCreated(new Date());
        subnet.setCreatorId(1L);
        subnet.setComment("Unit-test. Create subnet");
        return subnet;
    }

    @Autowired
    IpReservationService ipReservationService;

    private static final Logger log = LoggerFactory.getLogger(IpReservationServiceTest.class);
}
