package ru.protei.portal.redmine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.redmine.handlers.ForwardChannelEventHandler;

import static ru.protei.portal.core.model.util.CrmConstants.Time.MINUTE;
import static ru.protei.portal.core.model.util.CrmConstants.Time.SEC;

@Component
public final class RedmineIssuesCheckRunner {

    public RedmineIssuesCheckRunner() {
        logger.debug("Redmine issues checker created");
    }

    //5 minutes in MS
    @Scheduled(fixedRate = 10 * SEC)
    public void queryIssues() {
        if (!portalConfig.data().integrationConfig().isRedmineEnabled()) {
            logger.debug("Redmine integration is disabled in config, therefore nothing happens");
            return;
        }

        forwardChannel.checkIssues();
    }

    private static final Logger logger = LoggerFactory.getLogger(RedmineIssuesCheckRunner.class);

    @Autowired
    private ForwardChannelEventHandler forwardChannel;

    @Autowired
    private PortalConfig portalConfig;
}
