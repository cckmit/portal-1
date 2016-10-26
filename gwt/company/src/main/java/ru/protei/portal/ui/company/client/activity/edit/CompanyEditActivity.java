package ru.protei.portal.ui.company.client.activity.edit;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
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
import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.ValueCommentPair;
import ru.protei.portal.ui.common.shared.Debugging;
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
        else {

        }
    }

    @Override
    public void onSaveClicked() {
        Debugging.consoleLog(view.companyGroup().getValue().getName());


        if(view.companyName().getText().trim().isEmpty() || view.actualAddress().getText().trim().isEmpty() || view.legalAddress().getText().trim().isEmpty()){
            fireEvent(new NotifyEvents.Show(lang.asteriskRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        companyService.isCompanyNameExists(view.companyName().getText(), null, new AsyncCallback<Boolean>() {
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

                List<ValueCommentPair>
                        phoneList = view.phoneDataList().getDataList(),
                        emailList = view.emailDataList().getDataList();

                if(!phoneList.isEmpty()){
                    company.setPhone(buildJsonFromVCPairList(phoneList));
                }
                if(!emailList.isEmpty()){
                    company.setEmail(buildJsonFromVCPairList(emailList));
                }

                companyService.saveCompany(company, view.companyGroup().getValue(), new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.companyNotSaved(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
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
            view.setCompanyNameStatus(NameStatus.NONE);
            return;
        }

        companyService.isCompanyNameExists(
                view.companyName().getText(),
                null,
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

    private String buildJsonFromVCPairList(List<ValueCommentPair> list){
        JSONObject items = new JSONObject();
        int counter = 0;
        for(ValueCommentPair pair: list){
            items.put(""+ counter++, buildJsonFromVCPair(pair));
        }
        return items.toString();
    }

    private JSONObject buildJsonFromVCPair(ValueCommentPair pair){
        JSONObject item = new JSONObject();
        item.put("v", new JSONString(pair.value().getText()));
        if(!pair.comment().getText().trim().isEmpty())
            item.put("c", new JSONString(pair.comment().getText()));

        return item;
    }

    private void resetFields(){
        view.setCompanyNameStatus(NameStatus.NONE);
        view.companyName().setText("");
        view.actualAddress().setText("");
        view.legalAddress().setText("");
        view.webSite().setText("");
        view.comment().setText("");
        view.companyGroup().setValue(null);
        view.phoneDataList().setDataList(null);
        view.emailDataList().setDataList(null);
    }


    @Inject
    AbstractCompanyEditView view;
    @Inject
    Lang lang;

    @Inject
    CompanyServiceAsync companyService;


    private AppEvents.InitDetails initDetails;
}
