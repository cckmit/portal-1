package ru.protei.portal.ui.account.client.widget.casefilter.item;

public interface PersonCaseFilterCallbacks {
    void add(Long caseFilterId);
    void remove(Long caseFilterId);
    void change(Long oldCaseFilterId, Long newCaseFilterId);
}
