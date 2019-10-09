package ru.protei.portal.test.hpsm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.protei.portal.config.CoreMailConfiguration;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.hpsm.api.HpsmMessageFactory;
import ru.protei.portal.hpsm.config.HpsmConfigurationContext;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;
import ru.protei.portal.hpsm.utils.CompanyBranchMap;
import ru.protei.portal.hpsm.utils.HpsmTestUtils;
import ru.protei.portal.hpsm.utils.TestServiceInstance;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

/**
 * Created by michael on 19.04.17.
 */
@Configuration
@Import({CoreConfigurationContext.class,
        JdbcConfigurationContext.class,
        IntegrationTestsConfiguration.class,
        AddonConfiguration.class,
        HpsmConfigurationContext.class,
        CoreMailConfiguration.class})
public class HpsmTestConfiguration {

    @Bean
    public TestServiceInstance createTestServiceInstance (@Autowired CompanyBranchMap companyBranchMap, @Autowired HpsmMessageFactory hpsmMessageFactory) {
        HpsmEnvConfig.ServiceConfig config = new HpsmEnvConfig.ServiceConfig("test")
                .outbound(HpsmTestUtils.SENDER_ADDRESS, HpsmTestUtils.HPSM_MAIL_ADDRESS)
                .inbound("virtual://");

        return new TestServiceInstance (config, companyBranchMap, hpsmMessageFactory);
    }

    @Bean
    public HpsmTestUtils createTestUtils () {
        return new HpsmTestUtils ();
    }

}
