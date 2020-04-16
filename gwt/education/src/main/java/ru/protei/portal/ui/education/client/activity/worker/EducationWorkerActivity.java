package ru.protei.portal.ui.education.client.activity.worker;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.EducationEntryType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.EducationWallet;
import ru.protei.portal.ui.common.client.events.EducationEvents;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
import ru.protei.portal.ui.common.client.service.EducationControllerAsync;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.education.client.activity.wallet.AbstractEducationWalletActivity;
import ru.protei.portal.ui.education.client.activity.wallet.AbstractEducationWalletView;
import ru.protei.portal.ui.education.client.util.EducationUtils;

import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public abstract class EducationWorkerActivity implements Activity, AbstractEducationWorkerActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(EducationEvents.ShowWorker event) {
        if (!EducationUtils.isWorker()) {
            fireEvent(new ForbiddenEvents.Show(event.parent));
            return;
        }
        showView(event.parent);
        showTable();
        loadWallets();
    }

    private void showView(HasWidgets parent) {
        parent.clear();
        parent.add(view.asWidget());
    }

    private void showTable() {
        fireEvent(new EducationEvents.ShowWorkerTable(view.tableContainer()));
    }

    private void loadWallets() {
        view.walletContainer().clear();
        view.walletLoadingViewVisibility().setVisible(true);
        view.walletFailedViewVisibility().setVisible(false);
        educationController.getAllWallets(new FluentCallback<List<EducationWallet>>()
                .withError(throwable -> {
                    view.walletContainer().clear();
                    view.walletLoadingViewVisibility().setVisible(false);
                    view.walletFailedViewVisibility().setVisible(true);
                    if (throwable instanceof RequestFailedException) {
                        view.walletFailedViewText(resultStatusLang.getMessage(((RequestFailedException) throwable).status));
                    } else {
                        view.walletFailedViewText(resultStatusLang.getMessage(En_ResultStatus.INTERNAL_ERROR));
                    }
                })
                .withSuccess(wallets -> {
                    view.walletContainer().clear();
                    view.walletLoadingViewVisibility().setVisible(false);
                    view.walletFailedViewVisibility().setVisible(false);
                    stream(wallets)
                            .map(this::makeWalletView)
                            .map(IsWidget::asWidget)
                            .forEach(v -> view.walletContainer().add(v));
                }));
    }

    private AbstractEducationWalletView makeWalletView(EducationWallet wallet) {
        AbstractEducationWalletView view = walletViewProvider.get();
        view.setActivity(new AbstractEducationWalletActivity() {});
        view.setDepartmentName(wallet.getDepartmentName());
        view.setCoins(wallet.getCoins());
        view.setCountConference(stream(wallet.getEducationEntryList())
                .filter(entry -> entry.getType() == EducationEntryType.CONFERENCE)
                .count());
        view.setCountCourse(stream(wallet.getEducationEntryList())
                .filter(entry -> entry.getType() == EducationEntryType.COURSE)
                .count());
        view.setCountLiterature(stream(wallet.getEducationEntryList())
                .filter(entry -> entry.getType() == EducationEntryType.LITERATURE)
                .count());
        return view;
    }

    @Inject
    En_ResultStatusLang resultStatusLang;
    @Inject
    EducationControllerAsync educationController;
    @Inject
    AbstractEducationWorkerView view;
    @Inject
    Provider<AbstractEducationWalletView> walletViewProvider;
}
