package ru.protei.portal.core.model.dict;

public enum En_CaseLink {

    CRM(false),
    YT(true);

    En_CaseLink(boolean forcePrivacy) {
        this.forcePrivacy = forcePrivacy;
    }

    private final boolean forcePrivacy;

    public boolean isForcePrivacy() {
        return forcePrivacy;
    }
}
