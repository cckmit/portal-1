package ru.protei.tm.ui.common.client.lang;

import com.google.gwt.i18n.client.Messages;

/**
 * Интерфейс со строковыми константами
 */
public interface Lang extends Messages {

    String version();

    String yes();

    String no();

    String undefined();

    String undefinedMarked();

    String statusSuccessfully();

    String statusObjectNotFound();

    String statusObjectUnavailable();

    String statusInsufficientRights();

    String statusErrorAuthorisation();

    String statusInternalCoreError();

    String statusSuchObjectAlreadyExists();

    String statusErrorConsistence();

    String statusIncorrectParameters();

    String statusObjectBusy();

    String statusServerUnavailable();

    String statusEndDateNotifyListInThePast();

    String statusMissingDataOfService();

    String statusMissingWaitingTime();

    String statusObjectCantBeChangedRemoved();

    String statusHttpError();

    String statusBadRequest();

    String authLogin();

    String authPassword();

    String authHeader();

    String authEnter();

    String authRememberMe();

    String errorTmDataLoad();

    String surveyIdColumn();

    String surveyNameColumn();

    String surveyCreateDateColumn();

    String surveyTableHeader();

    String errorLoad();

    String errorNoSurveySelected();

    String buttonCreate();

    String buttonRemove();

    String headerSurveys();

    String headerProjects();

    String headerNumbers();

    String surveyProjectColumn();

    String appLogout();

    String searchNoMatchesFound();

    String buttonCopy();

    String surveyName();

    String buttonSave();

    String buttonArchive();

    String surveyEditHeader( String id );

    String copySurveyTitle();

    String errorCopySurvey();

    String surveyCopyDetailsHeader();

    String buttonCancel();

    String allRegions();

    String allVcc();

    String  statusColumnName();

    String surveyVirtualCc();

    String surveyRegion();

    String surveyFailures();

    String surveyFilterNameSearch();

    String surveyStatusNew();

    String surveyStatusRun();

    String surveyStatusPause();

    String surveyStatusStop();

    String surveyStatusComplete();

    String allSurveyses();

    String activeSurveyses();

    String surveyStatusFilter();

    String infosName();

    String surveyServices();

    String surveyStatusArchive();

    String surveyCopyDetailsOkButton();

    String surveyRegionColumn();

    String surveyVccColumn();
}
