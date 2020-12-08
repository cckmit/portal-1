package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorItemRenderer;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class SubcontractorCompanyModel implements Activity, AsyncSelectorModel<EntityOption>, SelectorItemRenderer<EntityOption> {

    @Event
    public void onInit(AuthEvents.Success event) {
        cache.clearCache();
    }

    @Event
    public void onCompanyListChanged(CompanyEvents.ChangeModel event) {
        cache.clearCache();
    }

    public SubcontractorCompanyModel() {
        cache.setLoadHandler(makeLoadHandler());
    }

    @Override
    public EntityOption get(int elementIndex, LoadingHandler loadingHandler) {
        return cache.get(elementIndex, loadingHandler);
    }

    @Override
    public String getElementName(EntityOption value) {
        return value == null ? "" : value.getDisplayText();
    }

    public void setCompanyId(Long companyId) {
        cache.clearCache();
        this.companyId = companyId;
    }

    public void setActive(boolean active) {
        cache.clearCache();
        this.isActive = active;
    }

    private SelectorDataCacheLoadHandler<EntityOption> makeLoadHandler() {
        return (offset, limit, handler) ->
            companyService.getSubcontractorOptionList(companyId, isActive, new FluentCallback<List<EntityOption>>()
                    .withError(throwable -> {
                        fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                        handler.onFailure(throwable);
                    })
                    .withSuccess(options -> {
                        transliteration(options);
                        handler.onSuccess(options);
                    }));
    }

    private void transliteration(List<EntityOption> options) {
        options.forEach(option -> option.setDisplayText(TransliterationUtils.transliterate(option.getDisplayText(), LocaleInfo.getCurrentLocale().getLocaleName())));
    }

    @Inject
    CompanyControllerAsync companyService;

    @Inject
    Lang lang;

    private Long companyId;
    private boolean isActive;
    private SelectorDataCache<EntityOption> cache = new SelectorDataCache<>();
}