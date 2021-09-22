package ru.protei.portal.core.model.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import ru.protei.portal.core.model.dao.CardBatchDAO;
import ru.protei.portal.core.model.ent.CardBatch;

public class CardBatchDAO_Impl extends PortalBaseJdbcDAO<CardBatch> implements CardBatchDAO {

    @Override
    public String getLastNumber(Long typeId) {
        try {
            return getMaxValue("number", String.class, "type_id = ?", typeId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
