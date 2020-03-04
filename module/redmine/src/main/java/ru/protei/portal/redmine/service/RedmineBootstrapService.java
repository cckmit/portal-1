package ru.protei.portal.redmine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/**
 * Сервис выполняющий первичную инициализацию, работу с исправлением данных
 */
public class RedmineBootstrapService {

    private static Logger logger = LoggerFactory.getLogger(RedmineBootstrapService.class);

    @PostConstruct
    public void init() {
        // do nothing
    }
}
