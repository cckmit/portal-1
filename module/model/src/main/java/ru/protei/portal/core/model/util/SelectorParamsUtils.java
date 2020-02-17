package ru.protei.portal.core.model.util;

import ru.protei.portal.core.model.ent.SelectorsParamsRequest;
import ru.protei.portal.core.model.query.CaseQuery;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

public class SelectorParamsUtils {
    public static SelectorsParamsRequest makeRequest(CaseQuery caseQuery) {
        SelectorsParamsRequest request = new SelectorsParamsRequest();

        request.setCompanyIds(emptyIfNull(caseQuery.getCompanyIds()).stream().filter(Objects::nonNull).collect(Collectors.toList()));

        Set<Long> personsIds = new HashSet<>();
        personsIds.addAll(emptyIfNull(caseQuery.getManagerIds()));
        personsIds.addAll(emptyIfNull(caseQuery.getInitiatorIds()));
        personsIds.addAll(emptyIfNull(caseQuery.getCommentAuthorIds()));
        request.setPersonIds(personsIds.stream().filter(Objects::nonNull).collect(Collectors.toList()));

        request.setProductIds(emptyIfNull(caseQuery.getProductIds()).stream().filter(Objects::nonNull).collect(Collectors.toList()));

        return request;
    }
}
