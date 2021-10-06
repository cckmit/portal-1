package ru.protei.portal.ui.delivery.client.activity.card.meta;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.view.card.meta.CardMetaView;

import java.util.Date;


public abstract class CardCommonMeta implements Activity, AbstractCardCommonMeta {

    public void setMetaView(CardMetaView view) {
        this.view = view;
    }

    @Override
    public void onTestDateChanged() {
        view.setTestDateValid(isTestDateFieldValid());
    }

    public String getValidationError() {
        if (view.state().getValue() == null) {
            return lang.cardValidationErrorState();
        }
        if (view.type().getValue() == null) {
            return lang.cardValidationErrorType();
        }
        if (view.cardBatch().getValue() == null) {
            return lang.cardValidationErrorCardBatch();
        }
        if (view.article().getValue() == null || !view.articleIsValid()) {
            return lang.cardValidationErrorArticle();
        }
        if (view.manager().getValue() == null) {
            return lang.cardValidationErrorManager();
        }
        if (view.testDate().getValue() == null || !isTestDateFieldValid()) {
            return lang.cardValidationErrorTestDate();
        }
        return null;
    }

    public boolean isTestDateFieldValid() {
        Date date = view.testDate().getValue();
        if (date == null) {
            return false;
        }

        return date.getTime() > System.currentTimeMillis();
    }

    @Inject
    protected Lang lang;
    @Inject
    protected CardMetaView view;
}
