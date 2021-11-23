package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.RFIDLabel;

import java.util.List;

public interface RFIDLabelService {
    Result<RFIDLabel> get(AuthToken token, Long id);

    Result<List<RFIDLabel>> getAll(AuthToken token);

    Result<RFIDLabel> update(AuthToken token, RFIDLabel value);
}
