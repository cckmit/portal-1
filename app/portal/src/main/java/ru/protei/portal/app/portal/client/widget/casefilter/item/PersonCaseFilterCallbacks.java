package ru.protei.portal.app.portal.client.widget.casefilter.item;

public interface PersonCaseFilterCallbacks {
    void add(Long caseFilterId);
    void remove(Long caseFilterId);
    void change(Long oldCaseFilterId, Long newCaseFilterId);
}
