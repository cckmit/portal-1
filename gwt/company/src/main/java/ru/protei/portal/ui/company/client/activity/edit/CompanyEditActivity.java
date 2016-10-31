package ru.protei.portal.ui.company.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.NameStatus;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.company.client.service.CompanyServiceAsync;


/**
 * Активность создания и редактирования компании
 */
public abstract class CompanyEditActivity implements AbstractCompanyEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( CompanyEvents.Edit event ) {

        initDetails.parent.clear();
        resetFields();
        initDetails.parent.add(view.asWidget());

        if(event.getCompanyId() == null)
            this.fireEvent( new AppEvents.InitPanelName( lang.companyNew() ) );
        else {

        }
    }

    @Override
    public void onSaveClicked() {
        if(!checkRequiredFields())
            return;

        companyService.isCompanyNameExists(view.companyName().getText(), null, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                //fireEvent(new NotifyEvents.Show(lang.errNotSaved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean isExists) {
                if(isExists){
                    fireEvent(new NotifyEvents.Show(lang.errNotSaved(), NotifyEvents.NotifyType.ERROR));
                    return;
                }

                Company company = new Company();
                company.setCname(view.companyName().getText());
                company.setInfo(view.comment().getText());
                company.setWebsite(view.webSite().getText());
                company.setAddressDejure(view.legalAddress().getText());
                company.setAddressFact(view.actualAddress().getText());

                //установить в saveCompany группу
                companyService.saveCompany(company, null, new RequestCallback<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {
                        //fireEvent(new NotifyEvents.Show(lang.errNotSaved(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        fireEvent(new CompanyEvents.Show());
                        fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    }
                });
            }
        });
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onChangeCompanyName() {
        if(view.companyName().getText().trim().isEmpty()){
            view.setCompanyNameStatus(NameStatus.NONE);
            return;
        }

        companyService.isCompanyNameExists(
                view.companyName().getText(),
                null,
                new RequestCallback<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(Boolean isExists) {
                        if (isExists) {
                            view.setCompanyNameStatus(NameStatus.ERROR);
                        } else {
                            view.setCompanyNameStatus(NameStatus.SUCCESS);
                        }
                    }
                }
        );

    }

    private boolean checkRequiredFields(){
        boolean isCorrect = true;

        view.markAsCorrect(view.companyName(), !view.companyName().getText().trim().isEmpty() || (isCorrect = false));

        view.markAsCorrect(view.actualAddress(), !view.actualAddress().getText().trim().isEmpty() || (isCorrect = false));

        view.markAsCorrect(view.legalAddress(), !view.legalAddress().getText().trim().isEmpty() || (isCorrect = false));

        return isCorrect;
    }

    private void resetFields(){
        view.setCompanyNameStatus(NameStatus.NONE);
        view.companyName().setText("");
        view.actualAddress().setText("");
        view.legalAddress().setText("");
        view.webSite().setText("");
        view.comment().setText("");
        view.companyGroup().setValue(null);

        view.markAsCorrect(view.companyName(), true);
        view.markAsCorrect(view.actualAddress(), true);
        view.markAsCorrect(view.legalAddress(), true);

    }


    @Inject
    AbstractCompanyEditView view;
    @Inject
    Lang lang;

    @Inject
    CompanyServiceAsync companyService;


    private AppEvents.InitDetails initDetails;
}
