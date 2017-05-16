package ru.protei.portal.test.hpsm;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.hpsm.api.MailSendChannel;
import ru.protei.portal.hpsm.utils.HpsmTestUtils;
import ru.protei.portal.test.hpsm.config.HpsmTestConfiguration;

/**
 * Created by Mike on 01.05.2017.
 */
public class InitTestCases {
    public static final String HPSM_TEST_CASE_ID1 = "hpsm-bt-001";

    public static void main (String argv[]) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(HpsmTestConfiguration.class);

        CaseObjectDAO caseObjectDAO = ctx.getBean(CaseObjectDAO.class);

        if (caseObjectDAO.getByExternalAppId(HPSM_TEST_CASE_ID1) != null)
            return;

        MailSendChannel sendChannel = ctx.getBean(MailSendChannel.class);

        HpsmTestUtils testUtils = ctx.getBean(HpsmTestUtils.class);

        try {
            sendChannel.send(testUtils.createNewRequest(HPSM_TEST_CASE_ID1));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
