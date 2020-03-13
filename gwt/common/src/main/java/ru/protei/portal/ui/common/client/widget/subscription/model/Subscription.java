package ru.protei.portal.ui.common.client.widget.subscription.model;

import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.ent.DevUnitSubscription;

/**
 * Клиентская обертка над подписками
 */
public class Subscription {
    private Long id;
    private Long entityId;
    private String email;
    private String langCode;

    public static Subscription fromProductSubscription( DevUnitSubscription cs ) {
        Subscription subscription = new Subscription();
        subscription.id = cs.getId();
        subscription.entityId = cs.getDevUnitId();
        subscription.email = cs.getEmail();
        subscription.langCode = cs.getLangCode();
        return subscription;
    }

    public DevUnitSubscription toProductSubscription() {
        DevUnitSubscription cs = new DevUnitSubscription();
        cs.setId( id );
        cs.setDevUnitId( entityId );
        cs.setEmail( email );
        cs.setLangCode( langCode );
        return cs;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String emal) {
        this.email = emal;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }
}
