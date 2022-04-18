package ru.protei.portal.app.portal.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.protei.portal.config.ExternalLinksHtml;
import ru.protei.portal.config.IssueCommentHelpTextConverter;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.ui.common.client.service.AppService;
import ru.protei.portal.ui.common.shared.model.ClientConfigData;

import static java.util.Arrays.asList;

/**
 * Сервис приложения
 */
@Service( "AppService" )
@PropertySource("classpath:version.properties")
public class AppServiceImpl extends RemoteServiceServlet implements AppService {

    @Override
    public ClientConfigData getClientConfig() {
        log.info("getClientConfig");

        ClientConfigData data = new ClientConfigData();

        data.appVersion = properties.getProperty("version", "");
        data.cardbatchCompanyPartnerId = portalConfig.data().getCommonConfig().getCardbatchCompanyPartnerId();
        data.contractCuratorsDepartmentsIds = asList(portalConfig.data().getCommonConfig().getContractCuratorsDepartmentsIds());
        data.employeeBirthdayHideIds = portalConfig.data().getEmployeeConfig().getEmployeeBirthdayHideIds();
        data.autoCloseDefaultDeadline = portalConfig.data().getDeadlineConfig().getDefaultDeadline();

        log.info( "getClientConfig, data = {}", data );
        return data;
    }

    @Override
    public String getExternalLinksHtml() {
        log.info("getExternalLinksHtml");
        return externalLinksHtml.getHtml();
    }

    @Override
    public String getIssueCommentHelpText(String localeName) {
        log.info("getIssueCommentHelpText for locale " + localeName);
        return issueCommentHelpTextConverter.getText(localeName);
    }

    @Autowired
    Environment properties;

    @Autowired
    PortalConfig portalConfig;

    @Autowired
    ExternalLinksHtml externalLinksHtml;

    @Autowired
    IssueCommentHelpTextConverter issueCommentHelpTextConverter;

    private static final Logger log = LoggerFactory.getLogger(AppServiceImpl.class.getName());
}