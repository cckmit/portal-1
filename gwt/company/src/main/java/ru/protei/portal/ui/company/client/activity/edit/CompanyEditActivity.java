package ru.protei.portal.ui.company.client.activity.edit;

import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.ValueComment;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ValueCommentEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.NameStatus;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;

import java.util.ArrayList;
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
        view.resetFields();
        initDetails.parent.add(view.asWidget());

        if(event.getCompanyId() == null) {
            this.fireEvent(new AppEvents.InitPanelName(lang.companyNew()));
            fireEvent(new ValueCommentEvents.ShowList(view.phonesContainer(), companyPhones = new ArrayList<>()));
            fireEvent(new ValueCommentEvents.ShowList(view.emailsContainer(), companyEmails = new ArrayList<>()));
        }else {

        }
    }

    @Override
    public void onSaveClicked() {
        if(!validateFieldsAndGetResult()) {
            return;
        }

        // оставить только saveCompany
        companyService.isCompanyNameExists(view.companyName().getText(), null, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errNotSaved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean isExists) {
                if(isExists){
                    fireEvent(new NotifyEvents.Show(lang.errNotSaved(), NotifyEvents.NotifyType.ERROR));
                    return;
                }

                Company company = new Company();
                company.setCname(view.companyName().getText());
                company.setCategory( view.companyCategory().getValue() );
                company.setInfo(view.comment().getText());
                company.setWebsite(view.webSite().getText());
                company.setAddressDejure(view.legalAddress().getText());
                company.setAddressFact(view.actualAddress().getText());

                //установить в saveCompany группу
                companyService.saveCompany(company, view.companyGroup().getValue(), new RequestCallback<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.errNotSaved(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        fireEvent(new CompanyEvents.Show());
                        fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                        fireEvent(new CompanyEvents.ChangeModel());
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

    private boolean validateFieldAndGetResult(HasValidable validator, HasText field){
        boolean result = !field.getText().trim().isEmpty();
        validator.setValid(result);
        return result;
    }

    private boolean validateFieldsAndGetResult(){
        boolean isCorrect;

        isCorrect = validateFieldAndGetResult(view.companyNameValidator(), view.companyName());

        isCorrect = validateFieldAndGetResult(view.actualAddressValidator(), view.actualAddress()) && isCorrect;

        isCorrect = validateFieldAndGetResult(view.legalAddressValidator(), view.legalAddress()) && isCorrect;

        return isCorrect;
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
    private List<ValueComment> companyPhones;
    private List<ValueComment> companyEmails;
}
