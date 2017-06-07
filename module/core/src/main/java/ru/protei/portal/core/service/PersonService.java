package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;

/**
 * Сервис управления person
 */
public interface PersonService {
    CoreResponse< List< PersonShortView > > shortViewList();
}
