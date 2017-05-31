package ru.protei.portal.tools.notifications;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
