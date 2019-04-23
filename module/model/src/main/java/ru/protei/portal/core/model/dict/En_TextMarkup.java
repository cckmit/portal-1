package ru.protei.portal.core.model.dict;

public enum En_TextMarkup implements ru.protei.winter.core.utils.enums.HasId {

    MARKDOWN(0),
    JIRA_WIKI_MARKUP(1),
    ;

    En_TextMarkup(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    private final int id;
}
