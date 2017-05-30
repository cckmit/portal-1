package ru.protei.portal.tools.notifications;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.protei.portal.tools.migrate.MigrateSetup;
import ru.protei.portal.tools.migrate.parts.*;
import ru.protei.portal.tools.migrate.tools.MigrateAction;

/**
 * фабрика для бинов нужных для рассылки уведомлений
 */
@Configuration
public class NotificationConfiguration {

   @Bean
   public NotificationProcessor getNotificationProcessor() {
      return new NotificationProcessor();
   }
}
