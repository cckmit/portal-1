package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.CaseFilter;

import java.util.List;

public interface CaseFilterDAO extends PortalBaseDAO<CaseFilter> {
    List< CaseFilter > getListByLoginIdAndFilterType( Long loginId, En_CaseFilterType filterType );

    List<CaseFilter> getListByFilterTypes(List<En_CaseFilterType> filterTypes);

    CaseFilter checkExistsByParams(String name, Long loginId, En_CaseFilterType type );
    List<CaseFilter> getByPersonId(Long personId);

    List<CaseFilter> getByPersonIdAndTypes(Long personId, List<En_CaseFilterType> filterTypes);
}
