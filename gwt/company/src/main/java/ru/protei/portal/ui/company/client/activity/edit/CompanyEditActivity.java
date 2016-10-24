package ru.protei.portal.ui.company.client.activity.edit;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.NameStatus;
import ru.protei.portal.ui.company.client.service.CompanyServiceAsync;

import java.util.List;


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
            this.fireEvent( new AppEvents.InitPanelName( lang.newCompany() ) );
        else{

        }

    }

    @Override
    public void onSaveClicked() {
        if(view.companyName().getText().trim().isEmpty() || view.actualAddress().getText().trim().isEmpty() || view.legalAddress().getText().trim().isEmpty()){
            fireEvent( new NotifyEvents.Show(lang.asteriskRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        companyService.isCompanyNameExists(view.companyName().getText(), new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.companyNotSaved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean isExists) {
                if(isExists){
                    fireEvent(new NotifyEvents.Show(lang.companyNotSaved(), NotifyEvents.NotifyType.ERROR));
                    return;
                }

                Company company = new Company();
                company.setCname(view.companyName().getText());
                company.setInfo(view.comment().getText());
                company.setWebsite(view.webSite().getText());
                company.setAddressDejure(view.legalAddress().getText());
                company.setAddressFact(view.actualAddress().getText());

                companyService.saveCompany(company, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.companyNotSaved(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(Void aVoid) {
                        fireEvent(new CompanyEvents.Show());
                        fireEvent(new NotifyEvents.Show(lang.companySaved(), NotifyEvents.NotifyType.SUCCESS));
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
            view.setCompanyNameStatus(NameStatus.UNDEFINED);
            return;
        }

        companyService.isCompanyNameExists(
                view.companyName().getText(),
                new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.errorGetList(), NotifyEvents.NotifyType.ERROR));
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


    private void resetFields(){
        view.setCompanyNameStatus(NameStatus.UNDEFINED);
        view.companyName().setText("");
        view.actualAddress().setText("");
        view.legalAddress().setText("");
        view.webSite().setText("");
        view.comment().setText("");
    }

    //    native void consoleLog( String message) /*-{
//        console.log( "me:" + message );
//    }-*/;

    @Inject
    AbstractCompanyEditView view;
    @Inject
    Lang lang;

    @Inject
    CompanyServiceAsync companyService;


    private AppEvents.InitDetails initDetails;
}
