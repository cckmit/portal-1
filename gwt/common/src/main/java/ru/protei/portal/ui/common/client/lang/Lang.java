package ru.protei.portal.ui.common.client.lang;

import com.google.gwt.i18n.client.Messages;
import ru.protei.portal.core.model.dict.lang.ContactItemLang;

/**
 * Интерфейс со строковыми константами
 */
public interface Lang extends Messages, ContactItemLang {
    String buttonLogout();

    String buttonLogin();

    String companies();

    String name();

    String created();

    String updated();

    String sortBy();

    String products();

    String productNew();

    String productShowDeprecated();

    String buttonSave();

    String buttonCancel();

    String buttonCreate();

    String errNoMatchesFound();

    String errGetList();

    String msgHello();

    String unknownField();

    String companyGroup();

    String search();

    String description();

    String productToArchive();

    String productName();

    String msgObjectSaved();

    String productFromArchive();

    String errEmptyName();

    String error();

    String companyName();

    String companyActualAddress();

    String companyLegalAddress();

    String companyWebSite();

    String phone();

    String comment();

    String contacts();

    String companyNew();

    String errAsteriskRequired();

    String errLoginOrPwd();

    String msgOK();

    String errConnectionError();

    String errInvalidSessionID();

    String errGetDataError();

    String errNotFound();

    String errNotCreated();

    String errNotUpdated();

    String errNotSaved();

    String errUndefinedObject();

    String errAlreadyExist();

    String errValidationError();

    String errIncorrectParams();

    String errInternalError();

    String errDatabaseError ();

    String errDatabaseTempError ();

    String errUnknownResult();

    String companyInfoHeader();

    String companyCommonHeader();

    String company();

    String contactShowFired();

    String contactFullName();

    String contactPosition();

    String fullScreen();

    String contactLastName();

    String contactFirstName();

    String contactSecondName();

    String contactDepartment();

    String address();
    
    String productDescription();

    String edit();

    String companyAdditionalInfoHeader();

    String newContact ();

    String editContactHeader (String contactName);

    String firstName ();

    String lastName ();

    String secondName ();

    String displayName ();

    String displayShortName ();

    String birthday ();

    String gender ();

    String genderMale ();
    String genderFemale ();
    String genderUndefined ();

    String personalData ();
    String contactInfo ();
    String advPersonalData ();
    String workPhone ();
    String workEmail ();
    String personalEmail ();
    String primaryFax ();
    String secondaryFax ();

    String contactFax();
    String contactMobilePhone();
    String contactPersonalPhone();
    String contactEmail();
    String contactActualAddress();
    String contactLegalAddress();
    String contactIcq();
    String contactJabber();
    String contactSkype();
    String contactWebSite();
    String contactSocialNet();

    String workAddress ();
    String homeAddress ();
    String department ();

    String errorCompanyRequired ();
    String errorFirstNameRequired();
    String errorLastNameRequired ();

    String companyCategory();

    String appNavHeader();

    String companyEdit();

    String noCompanyGroup();
    String companyGroupLabel ();

    String issues();
    String issueNumber();
    String issueProduct();
    String issueContacts();
    String issueInfo();
    String issueCreationDate();
    String issueManager();
    String issueName();
    String issueState();
    String issueImportance();
    String issueCompany();
    String issueInitiator();
    String issuePrivate();
    String issueHeader( String issueNumber );
    String issueCurrentStateHeader();
    String issueContactHeader();
    String issueInfoHeader();
    String issueCriticality();
    String newIssue();
    String issueEdit();
    String selectIssueState();
    String selectIssueImportance();
    String selectIssueCompany();
    String selectIssueInitiator();
    String selectIssueProduct();
    String selectIssueManager();
    String issueAttachments();

    String createdCaseState();
    String openedCaseState();
    String closedCaseState();
    String pausedCaseState();
    String verifiedCaseState();
    String reopenedCaseState();
    String ignoredCaseState();
    String assignedCaseState();
    String estimatedCaseState();
    String discussCaseState();
    String plannedCaseState();
    String activeCaseState();
    String doneCaseState();
    String testCaseState();
    String testLocalCaseState();
    String testCustCaseState();
    String designCaseState();
    String solvedNoapCaseState();
    String solvedFixCaseState();
    String solvedDupCaseState();
    String workaroundCaseState ();
    String requestInfoCaseState ();

    String criticalImportance();
    String importantImportance();
    String basicImportance();
    String cosmeticImportance();

    String comments ();

    String product ();

    String buttonReset();

    String selectContactCompany ();

    String selectDate();

    String email();
    String personalPhone();

    String separatorText( int page, int pageCount );
    String dataPageNumber (int page);

    String pagerLabel( int currentPage, int totalPages, int pageSize );

    String commentSendMessage();

    String commentAddMessagePlaceholder();

    String errEditIssueCommentNotAllowed();

    String errRemoveIssueComment();

    String errEditIssueComment();

    String selectCompanyGroup();
    
    String activeRecords();
    String newRecords();
    String inactiveRecords();

    String dashboard();

    String issueCommentChangeStatusTo();

    String errNotRemoved ();

    String errEditIssueCommentEmpty();

    String classifier();

    String equipmentName();

    String equipmentOrganizationCodePAMR();

    String equipmentOrganizationCodePDRA();

    String equipmentDecimalNumber();

    String equipmentComment();

    String equipmentNameBySpecification();

    String equipmentNameBySldWrks();

    String equipmentOrganizationProtei();

    String equipmentOrganizationProteiST();

    String equipmentOrganization();

    String equipmentSearchNameOrProject();

    String equipmentProduct();

    String regions();

    String regionStateUnknown();

    String regionStateRival();

    String regionStateTalk();

    String regionStateProjecting();

    String regionStateDevelopment();

    String regionStateDeployment();

    String regionStateSupport();

    String regionStateSupportFinished();

    String regionState();

    String regionDistrict();

    String productDirection();

    String projectDirection();

    String projectInfo();

    String projects();

    String projectManagers();

    String projectHeader( String s );

    String projectCreationDate();

    String projectHeadManager();

    String projectDeployManagers();

    String projectState();

    String projectRegion();

    String equipmentAttachment();

    String equipmentDescription();

    String equipmentTypeAssemblyUnit();

    String equipmentTypeComplex();

    String equipmentTypeDetail();

    String equipmentTypeProduct();

    String equipmentType();

    String equipmentStageDraftProject();

    String equipmentStageRKD_Product();

    String equipmentStageRKD_Prototype();

    String equipmentStageTechnicalProject();

    String equipmentStage();

    String equipmentPrimaryUse();

    String equipmentPrimaryUseNotDefinied();

    String equipmentDecimalNumberEmpty();

    String equipmentDecimalNumberBusy();

    String buttonImport();

    String equipmentClassifierCode();

    String equipmentRegisterNumber();

    String equipmentCommonHeader();

    String equipmentReserve();

    String buttonAdd();

    String equipmentRemove();

    String equipmentDecimalNumberNotDefinied();

    String equipmentRegisterNumberModification();

    String equipmentErrorGetNextAvailableNumber();

    String equipmentErrorCheckNumber();

    String equipmentNextAvailableNumber();

    String equipmentOrChange();

    String equipmentModification();

    String equipmentManager();

    String equipmentProject();

    String selectorAny();

    String buttonYes();

    String buttonNo();

    String buttonCopy();

    String buttonRemove();

    String equipmentRemoveConfirmMessage();

    String equipmentRemoveSuccessed();

    String equipmentNewName();

    String equipmentCopyNotFilledNewName();

    String equpmentCopySuccess();

    String equipmentCopyHeader();

    String equipmentCopyMessage();

    String copyPostfix();

    String attachment();

    String addAttachmentBtn();

    String uploadFileError();

    String uploadFileSuccess();

    String removeFileError();

    String attachmentsNotLoaded();

    String contactLogin();

    String contactPassword();

    String contactPasswordNotDefinied();

    String errEditContactLogin();

    String contactAccount();

    String contactLoginInfo();

    String contactConfirmPassword();

    String contactPasswordsNotMatch();

    String companySubscription();

    String accounts();
}
