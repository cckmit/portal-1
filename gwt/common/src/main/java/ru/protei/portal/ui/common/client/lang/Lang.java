package ru.protei.portal.ui.common.client.lang;

import com.google.gwt.i18n.client.Messages;
import ru.protei.portal.core.model.dict.lang.ContactItemLang;

/**
 * Интерфейс со строковыми константами
 */
public interface Lang extends Messages, ContactItemLang {

    String search();

    String sortBy();

    String in();

    String unknownField();

    String name();

    String created();

    String updated();

    String description();

    String comment();

    String comments();

    String phone();

    String email();

    String personalPhone();

    String separatorText( int p0, int p1 );

    String pagerLabel( int p0, int p1, long p2 );

    String view();

    String list();

    String table();

    String calendar();

    String type();

    String createBy( String p0, String p1 );

    String selectValue();

    String dropFilesHere();

    String attachment();

    String addAttachmentBtn();

    String uploadFileError();

    String uploadFileSizeExceed();

    String uploadFileSuccess();

    String removeFileError();

    String attachmentsNotLoaded();

    String remove();

    String attachmentRemoveConfirmMessage();

    String attachmentAuthor();

    String buttonLogin();

    String buttonLogout();

    String buttonCreate();

    String buttonModify();

    String buttonChangePassword();

    String buttonApply();

    String buttonSave();

    String buttonSaveAndContinue();

    String buttonCancel();

    String buttonReset();

    String buttonImport();

    String buttonAdd();

    String buttonYes();

    String buttonNo();

    String buttonCopy();

    String buttonRemove();

    String buttonLocale();

    String buttonReport();

    String buttonDownload();

    String buttonClear();

    String buttonNotDefined();

    String buttonProfile();

    String buttonSearch();

    String buttonClose();

    String buttonCreateProject();

    String buttonCreateProduct();

    String logoutHotKey();

    String buttonToArchive();

    String buttonFromArchive();

    String buttonBack();

    String buttonForward();

    String buttonState();

    String timeDayLiteral();

    String timeHourLiteral();

    String timeMinuteLiteral();

    String timeEstimated();

    String timeElapsed();

    String timeElapsedType();

    String from();

    String to();

    String asc();

    String desc();

    String download();

    String selected();

    String searchByComments();

    String searchByCommentsUnavailable( int p0 );

    String id();

    String filters();

    String sendEmail();

    String sendEmailWarning();

    String locale();

    String amountShort();

    String openTableView();

    String searchPrivate();

    String membersCount();

    String version();

    String accessory();

    String belongsTo();

    String consistOf();

    String selectManager();

    String selectPlatform();

    String sendOnCtrlEnter();

    String projectRequired();

    String decimalNumbersRequired();

    String commentPreview();

    String commentPreviewIsDisplay();

    String colorHex();

    String crmPrefix();

    String fileDropzoneLabel();

    String openFullScreen();

    String passwordIsDisplay();

    String searchNoMatchesFound();

    String searchTerminalState();

    String emptySelectorList();

    String company();

    String companies();

    String companyCategory();

    String companyGroup();

    String companyParentLabel();

    String companyChildrenLabel();

    String companyNew();

    String companyName();

    String companyActualAddress();

    String companyLegalAddress();

    String companyWebSite();

    String companyProbationPeriodAddresses();

    String errWorkerWithThisPositionAlreadyExist();

    String errWorkerWithThisDepartmentAlreadyExist();

    String errPositionAlreadyExistInThisCompany();

    String errEmployeeAlreadyExist();

    String errEmployeeEmailAlreadyExist();

    String errEmployeeNotFiredFromTheseCompanies();

    String errEmployeeMigrationFailed();

    String errLoginAlreadyExist();

    String errDepartmentAlreadyExistInThisCompany();

    String companyInfoHeader();

    String companyAdditionalInfoHeader();

    String companyCommonHeader();

    String companyEdit();

    String noCompanyGroup();

    String selectCompanyGroup();

    String companyMainInfoTitle();

    String companyContactInfoTitle();

    String companyContactsHeader();

    String companyParentCompany();

    String companyIsAPartOfCompany( String p0 );

    String companyIsAHeadOfCompany( String p0 );

    String companySubscriptionGroupRemoveConfirmMessage();

    String companySubscriptionGroupRemoveButton();

    String companySubscriptionGroupCollapseButton();

    String companySubscriptionGroupExpandButton();

    String companySubscriptionGroupAddButton();

    String companySubscriptionGroupAnyValuePlatform();

    String companySubscriptionGroupAnyValueProduct();

    String companySubscriptionGroupQuantity();

    String errCompanyNameExists();

    String errCompanyNameContainsIllegalChars();

    String errCompanyFieldsFill();

    String contacts();

    String contactShowFired();

    String contactFullName();

    String contactPosition();

    String contactDepartment();

    String contactLastName();

    String contactFirstName();

    String contactSecondName();

    String newContact();

    String firstName();

    String lastName();

    String secondName();

    String displayName();

    String displayShortName();

    String gender();

    String genderMale();

    String genderFemale();

    String genderUndefined();

    String personalData();

    String contactInfo();

    String advPersonalData();

    String workPhone();

    String workEmail();

    String personalEmail();

    String primaryFax();

    String secondaryFax();

    String workAddress();

    String homeAddress();

    String department();

    String editContactHeader( String p0 );

    String selectContactCompany();

    String contactPhone();

    String mobilePhone();

    String contactMobilePhone();

    String contactWorkPhone();

    String contactActualAddress();

    String contactLegalAddress();

    String contactIcq();

    String contactJabber();

    String contactSkype();

    String contactWebSite();

    String contactSocialNet();

    String contactEmail();

    String contactFax();

    String contactFired();

    String contactDeleted();

    String contactSaved();

    String contactFiredShort();

    String contactDeletedShort();

    String contactFire();

    String contactDelete();

    String contactFireConfirmMessage();

    String contactRemoveConfirmMessage();

    String withoutContact();

    String contactMainInfoTitle();

    String contactContactInfoTitle();

    String contactAccountTitle();

    String contactAccountHint();

    String goToContacts();

    String promptFieldLengthExceed(String fieldName, int fieldLength);

    String promptFieldNeedContainAtSign();

    String contactGeneratePassword();

    String contactHasAccount();

    String product();

    String products();

    String complexesAndProducts();

    String productNew();

    String productShowDeprecated();

    String productName();

    String productDescription();

    String componentDescription();

    String complexDescription();

    String productWithout();

    String productType();

    String selectProduct();

    String componentName();

    String components();

    String selectComponent();

    String complexName();

    String productSubscription();

    String productWikiLink();

    String productCDRDescription();

    String productHistoryVersion();

    String productConfiguration();

    String productChangeStateConfirmMessage();

    String productSelectDirection();

    String productAdditionalInfoHeader();

    String productAliases();

    String productSearchNameOrAlias();

    String issues();

    String issueNumber();

    String issueProduct();

    String issueContacts();

    String issueInfo();

    String issueCreationDate();

    String issueManager();

    String issueManagerCompany();

    String issueHeader( String p0 );

    String issueCurrentStateHeader();

    String issueContactHeader();

    String issueNameHeader();

    String issuePlatformHeader();

    String issueInfoHeader();

    String issueState();

    String issueCopyNumber();

    String issueCopyNumberAndName();

    String issueCriticality();

    String issueName();

    String issueImportance();

    String issueImportanceCheckHistory();

    String issueImportanceCheckHistoryTitle();

    String issueCompany();

    String issueInitiator();

    String issueInitiatorInfo();

    String issueManagerInfo();

    String issueEdit();

    String newIssue();

    String issuePrivate();

    String selectIssueState();

    String selectIssueImportance();

    String selectIssueCompany();

    String selectIssueInitiator();

    String selectIssueProduct();

    String selectIssueManager();

    String selectIssuePeriod();

    String selectDate();

    String issueAttachments();

    String issueFilter();

    String issueFilterName();

    String issueFilterRemoveConfirmMessage();

    String issueFilterRemoveSuccessed();

    String issueFilterNotDefined();

    String initiatorSelectACompany();

    String managerSelectACompany();

    String issueCommentAuthor();

    String jiraIssueType();

    String jiraSeverity();

    String jiraTimeOfReaction();

    String jiraTimeOfDecision();

    String goToProducts();

    String backToIssues();

    String editNameAndDescription();

    String issueCreate();

    String issueCreator();

    String issueJiraInfo();

    String jiraInfoPageHeader();

    String jiraInfoStatusMap();

    String jiraInfoWorkFlow();

    String jiraInfoJiraStatus();

    String jiraInfoCrmStatus();

    String jiraInfoStatusDefinition();

    String jiraInfoStatusComment();

    String jiraInfoBackButton();

    String linkedWith();

    String caseLinkCrm();

    String caseLinkCrmShort();

    String caseLinkYouTrack();

    String caseLinkYouTrackShort();

    String errYoutrackSynchronizationFailed();

    String caseLinkSuccessfulCreated();

    String caseLinkSuccessfulRemoved();

    String caseLinkSomeNotAdded();

    String errCaseLinkAlreadyAdded();

    String errCaseLinkNotFound();

    String linkAdd();

    String tag();

    String tags();

    String tagAdd();

    String tagCreate();

    String tagEdit();

    String tagName();

    String tagColor();

    String tagCompany();

    String tagNotSpecified();

    String tagAuthor();

    String tagInfo();

    String errFieldsRequired();

    String errUnknownResult();

    String error();

    String errNoMatchesFound();

    String errGetList();

    String errGetItem();

    String errSaveIssueFilter();

    String errNotFound();

    String errNotCreated();

    String errNotUpdated();

    String errNotRemoved();

    String errNotSaved();

    String errNotAvailable();

    String errAsteriskRequired();

    String errEmptyName();

    String errAlreadyExist();

    String errAlreadyExistRelated();

    String errInventoryNumberAlreadyExist();

    String errDecimalNumberAlreadyExist();

    String errLoginOrPwd();

    String errServerUnavailable();

    String errInternalError();

    String errIncorrectParams();

    String errValidationError();

    String errUndefinedObject();

    String errGetDataError();

    String errInvalidSessionID();

    String errConnectionError();

    String errDatabaseError();

    String errDatabaseTempError();

    String errorCompanyRequired();

    String errorFirstNameRequired();

    String errorLastNameRequired();

    String errEditIssueCommentNotAllowed();

    String errRemoveIssueComment();

    String errEditIssueComment();

    String errEditIssueCommentByTime();

    String errRemoveIssueCommentByTime();

    String errNRPEIpNotConfigured();

    String errNRPEIpNonAvailable();

    String errNRPEError();

    String errNRPENoFreeIps();

    String errEditTimeElapsedType();

    String errEditContactLogin();

    String errEditProfile();

    String errPermissionDenied();

    String errAccessDenied();

    String errSessionNotFound();

    String errSaveIssueNeedSelectManager();

    String errSaveIssueNeedUnselectManager();

    String errSaveIssueNeedSelectPlatform();

    String errProductNotSelected();

    String errFilterNameRequired();

    String errTooMuchCompanies();

    String errTooMuchManagers();

    String errTooMuchInitiators();

    String errTooMuchProducts();

    String errIssueCommentProhibitedPrivate();

    String errInvalidCaseUpdateCaseIsClosed();

    String errFilterParameterRequired();

    String errCopyToClipboard();

    String errInvalidCurrentPassword();

    String errNotAllowedChangeIssueNameOrDescription();

    String errorFieldHasInvalidValue( String p0 );

    String errUnableLinkIssueToItself();

    String errUpdateOrDeleteLinkedObjectError();

    String errSVN();

    String msgHello();

    String msgObjectSaved();

    String reservedIpPartiallyCreated(int p0, int p1 );

    String msgStatusChanged();

    String msgOK();

    String fullScreen();

    String birthday();

    String edit();

    String address();

    String appNavHeader();

    String copiedToClipboardSuccessfully();

    String privacyTypePublic();

    String privacyTypePrivateCustomers();

    String privacyTypePrivate();

    String dataPageNumber( String p0 );

    String dashboard();

    String dashboardAddTable();

    String dashboardEmpty();

    String dashboardTableOverflow( int p0 );

    String dashboardTableName();

    String dashboardTableFilter();

    String dashboardTableCreate();

    String dashboardTableEdit();

    String dashboardTableConfirmRemove();

    String dashboardTableRemoved();

    String dashboardTableFilterCreation();

    String dashboardTableFilterCreationNewIssues();

    String dashboardTableFilterCreationActiveIssues();

    String dashboardActionOpen();

    String dashboardActionReload();

    String dashboardActionEdit();

    String dashboardActionRemove();

    String dashboardActionCollapse();

    String dashboardActionExpand();

    String commentSendMessage();

    String markupPlaceholder();

    String issueCommentChangeStatusTo();

    String issueCommentChangeImportanceTo();

    String issueCommentChangeManagerTo();

    String commentEmpty();

    String textMarkdownSupport();

    String textJiraWikiMarkupSupport();

    String issueCommentHelp();

    String commentSetPrivate();

    String regions();

    String regionDistrict();

    String projectState();

    String projectStateUnknown();

    String projectStateMarketing();

    String projectStatePresale();

    String projectStateProjecting();

    String projectStateDevelopment();

    String projectStateDeployment();

    String projectStateTesting();

    String projectStateSupport();

    String projectStateFinished();

    String projectStateCanceled();

    String projectStatePaused();

    String regionNotSpecified();

    String projects();

    String projectNumber();

    String projectDirection();

    String projectDirections();

    String projectInfo();

    String projectManagers();

    String projectHeader( String p0 );

    String projectCreationDate();

    String projectHeadManager();

    String projectDeployManagers();

    String projectRegion();

    String productDirection();

    String projectCustomerCompany();

    String projectDescription();

    String projectProduct();

    String projectProducts();

    String projectCustomerType();

    String projectCustomer();

    String projectTeam();

    String projectSelectRole();

    String projectOnlyMine();

    String projectRemoveConfirmMessage( String p0 );

    String projectRemoveSucceeded();

    String projectCreateBy();

    String projectName();

    String projectSearch();

    String projectCreate();

    String projectSiteFolders();

    String productDirectionNotSpecified();

    String projectSlaReactionTime();

    String projectSlaTemporaryTime();

    String projectSlaFullTime();

    String projectSla();

    String projectSlaDefaultValues();

    String projectSlaSetValuesByManager();

    String projectImportance();

    String projectSlaNotValid();

    String projectTeamRole();

    String projectCommentCreationDate();

    String inputProjectName();

    String selectCustomerType();

    String firstHundredRecords();

    String newProject();

    String projectEdit();

    String goToProjects();

    String errSaveProjectNeedSelectDirection();

    String errSaveProjectNeedSelectCustomerType();

    String errSaveProjectNeedSelectCompany();

    String errSaveProjectCannotChangeCompany();

    String errSaveProjectPauseDate();

    String classifier();

    String equipmentName();

    String equipmentOrganizationCodePAMR();

    String equipmentOrganizationCodePDRA();

    String equipmentDecimalNumber();

    String equipmentComment();

    String equipmentNameBySldWrks();

    String equipmentNameBySpecification();

    String equipmentOrganizationProtei();

    String equipmentOrganizationProteiST();

    String equipmentOrganization();

    String equipmentSearchNameOrProject();

    String equipmentProduct();

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

    String equipmentClassifierCode();

    String equipmentRegisterNumber();

    String equipmentCommonHeader();

    String equipmentReserve();

    String equipmentRemove();

    String equipmentDecimalNumberNotDefinied();

    String equipmentDecimalNumberNotCorrect();

    String equipmentDecimalNumbeOccupied();

    String equipmentRegisterNumberModification();

    String equipmentErrorGetNextAvailableNumber();

    String equipmentErrorCheckNumber();

    String equipmentNextAvailableNumber();

    String equipmentOrChange();

    String equipmentModification();

    String equipmentManager();

    String equipmentProject();

    String selectorAny();

    String equipmentRemoveConfirmMessage();

    String equipmentRemoveSuccessed();

    String equipmentNewName();

    String equipmentCopyNotFilledNewName();

    String equipmentCopySuccess();

    String equipmentCopyHeader();

    String equipmentCopyMessage();

    String copyPostfix();

    String companySubscription();

    String equipmentDocumentAlreadyExists();

    String accounts();

    String accountType();

    String accountLDAP();

    String accountLocal();

    String accountLogin();

    String accountPerson();

    String accountAuthType();

    String accountNew();

    String accountLoginInfo();

    String accountPassword();

    String accountCurrentPassword();

    String accountNewPassword();

    String accountConfirmPassword();

    String accountPasswordChange();

    String accountPasswordsNotMatch();

    String accountPasswordNotDefinied();

    String accountRoleNotDefinied();

    String accountRoles();

    String accountCompany();

    String accountFilterCompany();

    String accountRemoveConfirmMessage();

    String selectAccountCompany();

    String selectAccountPerson();

    String accountRemoveSuccessed();

    String account();

    String accountLastName();

    String accountFirstName();

    String accountSecondName();

    String accountFor();

    String profileSubscriptionCaseFilter();

    String accountRolesNotFound();

    String roleName();

    String roleDescription();

    String roles();

    String role( String p0 );

    String roleMainTitle();

    String rolePrivilegesHint();

    String privilegeCategoryCommon();

    String privilegeCategoryIssue();

    String privilegeCategoryIssueAssignment();

    String privilegeCategoryRegion();

    String privilegeCategoryProject();

    String privilegeCategoryCompany();

    String privilegeCategoryProduct();

    String privilegeCategoryContact();

    String privilegeCategoryAccount();

    String privilegeCategoryEquipment();

    String privilegeCategoryRole();

    String privilegeCategoryOfficial();

    String privilegeDashboard();

    String privilegeSiteFolder();

    String privilegeCategoryEmployee();

    String privilegeCategoryEmployeeRegistration();

    String privilegeContract();

    String privilegeLogin();

    String privilegeView();

    String privilegeEdit();

    String privilegeCreate();

    String privilegeReport();

    String privilegeExport();

    String privilegeRemove();

    String privilegeDocument();

    String rolePrivileges();

    String rolePrivilegesNote();

    String privilegeIpReservation();

    String matrixSolutions();

    String officialTableProduct();

    String officialTableInfo();

    String officialTableNumberEmployees();

    String selectOfficialRegion();

    String officialRegion();

    String officialGeneralInfo();

    String officialFaces();

    String officialPreviewSearch();

    String officialOrganization();

    String officialRelations();

    String officialAmplua();

    String selectOfficialCompany();

    String errOfficialRemove();

    String errOfficialMemberRemove();

    String officialComment();

    String selectOfficialAmplua();

    String personHeadManager();

    String personDeployManager();

    String officialInRelationsWith();

    String selectOfficialProduct();

    String personCreateNew();

    String personHardwareCurator();

    String personSoftwareCurator();

    String personIntroNewTechSolutions();

    String personLiableForAutoTesting();

    String personTechSupportCurator();

    String personProductAssembler();

    String personDeliveryPreparation();

    String personEngineerDocDev();

    String personTechDocDev();

    String personSoftwareDocDev();

    String personLiableForCertification();

    String personOkrEscort();

    String personQualityControlSmk();

    String personCustomerIntegration();

    String personPresaleManager();

    String personBusinessAnalyticsArchitecture();

    String personProjectDocumentation();

    String personProductManager();

    String personDevelopment();

    String personPresaleHeadManager();

    String personDeployHeadManager();

    String equipmentGetNextModification();

    String formNextMod();

    String buttonAddPamr();

    String buttonAddPdra();

    String equipmentManagerNotDefined();

    String equipmentNumberAlreadyInList();

    String companySubscriptionUpdatedSuccessful();

    String passwordUpdatedSuccessful();

    String scopeSystem();

    String scopeCompany();

    String scopeUser();

    String roleScope();

    String messageServerConnectionLost();

    String roleRemoveSuccessed();

    String roleAdd();

    String noChanges();

    String roleRemoveConfirmMessage();

    String issueCompanySubscriptionNeedSelectCompany();

    String issueCompanySubscriptionNotDefined();

    String issueCompanySubscriptionBasedOnPrivacyNotDefined();

    String caseSubscription();

    String roleDefaultForContact();

    String document();

    String annotation();

    String designation();

    String documentCommonHeader();

    String documentProjectHeader();

    String documentCreateHeader();

    String documentName();

    String documentType();

    String documentTypeShort();

    String documentSearchNameOrProject();

    String documentSearchNameOrDesignation();

    String documentKeywords();

    String documentContent();

    String documentContractor();

    String documentRegistrar();

    String documentVersion();

    String documentManagers();

    String documentOrganizationCode();

    String documentEquipment();

    String documentWorkGroupHeader();

    String addKeyword();

    String keywordInputPlaceholder();

    String inventoryNumber();

    String customer();

    String documentTypeIsEmpty();

    String documentCategoryIsEmpty();

    String documentProjectIsEmpty();

    String inventoryNumberIsEmpty();

    String decimalNumberIsInvalid();

    String documentApproveFieldsIsEmpty();

    String documentPDFFileIsNotSet();

    String negativeInventoryNumber();

    String customerNotSet();

    String decimalNumberNotSet();

    String documentNameIsNotSet();

    String documentTypeNotDefined();

    String uploadDocuments();

    String reUploadDocuments();

    String errGetObject();

    String documentIdColumnHeader();

    String documentDescription();

    String documentProject();

    String documentProjectCustomerType();

    String documentProjectProductDirection();

    String documentProjectRegion();

    String documentManager();

    String documentDecimalNumber();

    String documentIdentification();

    String documentUploadPlaceholder();

    String uploadingDocumentNotSet();

    String errSaveDocumentFile();

    String errDocumentNotSaved();

    String documentApproveType();

    String documentApproved();

    String documentiSApproved();

    String documentApprovedBy();

    String documentApprovalDate();

    String documentNotApproved();

    String documentEdit();

    String documentCreate();

    String documentSaving();

    String documentSaved();

    String documentSearchProject();

    String documentChangeStateConfirmMessage();

    String documentShowDeprecated();

    String documentConfirmRemove();

    String documentRemoved();

    String documentSectionProject();

    String documentSectionEquipment();

    String documentSectionInfo();

    String documentFileDoc();

    String documentFilePdf();

    String documentFileApprovalSheet();

    String documentMembers();

    String documentMemberUploadWorkDocumentation();

    String documentMemberWorkDocumentationComment();

    String documentMemberWorkDocumentationUpload();

    String goToDocuments();

    String equipmentDocuments();

    String equipmentDocumentCreate();

    String documentCategory();

    String tpDocumentCategory();

    String kdDocumentCategory();

    String pdDocumentCategory();

    String edDocumentCategory();

    String tdDocumentCategory();

    String abroadDocumentCategory();

    String documentTypes();

    String documentTypeName();

    String documentTypeNameValidationError();

    String documentTypeShortName();

    String documentTypeGost();

    String documentTypeRemoveSuccessed();

    String documentTypeRemoveConfirmMessage();

    String documentTypeUnableToRemoveUsedDocumentType();

    String documentNameNew();

    String privilegeDocumentType();

    String documentTypeCode();

    String decimalNumberNotFound();

    String decimalNumberFound();

    String documentExecutionType();

    String documentExecutionTypeElectronic();

    String documentExecutionTypePaper();

    String documentExecutionTypeTypographic();

    String issueReports();

    String issueReportsNumber();

    String issueReportsInfo();

    String issueReportsFilter();

    String issueReportsNotDeleted();

    String issueReportsDeleted();

    String issueReportsCreated();

    String issueReportsProcess();

    String issueReportsReady();

    String issueReportsError();

    String issueReportsCancelled();

    String issueReportsTitle();

    String issueReportsNew();

    String issueReportsView();

    String reportRequested();

    String reportCanceled(Long id);

    String issueReportNew();

    String reportTypeCaseObjects();

    String reportTypeCaseTimeElapsed();

    String issueReportsType();

    String caseStates();

    String privilegeCaseStates();

    String caseStatesPreviewHeader();

    String caseStateUsagesInCompaniesNone();

    String caseStateUsagesInCompaniesAll();

    String caseStateUsagesInCompaniesSelected();

    String caseStatesPreviewCompanies();

    String caseStatesColumnUsageInCompanies();

    String caseStatesColumnInfo();

    String caseStatesColumnName();

    String caseStateDescription();

    String workTime();

    String customerTypeMinistryOfDefence();

    String customerTypeStageBudget();

    String customerTypeCommercialRf();

    String customerTypeCommercialNearAbroad();

    String customerTypeCommercialFarAbroad();

    String customerTypeCommercialProtei();

    String siteFolder();

    String siteFolderName();

    String siteFolderNameOrIp();

    String siteFolderCompany();

    String siteFolderParams();

    String siteFolderParamsLocal();

    String siteFolderProject();

    String siteFolderComment();

    String siteFolderIP();

    String siteFolderPath();

    String siteFolderPaths();

    String siteFolderPlatform();

    String siteFolderPlatformRequestError();

    String siteFolderPlatforms();

    String siteFolderPlatformNew();

    String siteFolderPlatformEdit();

    String siteFolderPlatformCreate();

    String siteFolderPlatformConfirmRemove();

    String siteFolderPlatformRemoved();

    String siteFolderPlatformNotRemoved();

    String siteFolderPlatformNotSaved();

    String siteFolderPlatformSaved();

    String siteFolderPlatformName();

    String siteFolderPlatformServersExport();

    String siteFolderServer();

    String siteFolderServers();

    String siteFolderServerNew();

    String siteFolderServerEdit();

    String siteFolderServerCreate();

    String siteFolderServerConfirmRemove();

    String siteFolderServerRemoved();

    String siteFolderServerNotRemoved();

    String siteFolderServerNotSaved();

    String siteFolderServerName();

    String siteFolderServerGroupName();

    String siteFolderServerGroupCreate();

    String siteFolderServerGroupEdit();

    String siteFolderServerGroupNameIsMissing();

    String siteFolderServerGroupChooseGroup();

    String siteFolderServerGroupAddGroup();

    String siteFolderServerGroup();

    String siteFolderServerGroupWithoutGroup();

    String siteFolderServerGroupSaved();

    String siteFolderServerGroupRemoved();

    String siteFolderApp();

    String siteFolderApps();

    String siteFolderAppNew();

    String siteFolderAppEdit();

    String siteFolderAppCreate();

    String siteFolderAppConfirmRemove();

    String siteFolderAppRemoved();

    String siteFolderAppNotRemoved();

    String siteFolderAppNotSaved();

    String siteFolderAppName();

    String siteFolderCopyPreviewLink();

    String issueLinkIncorrectCrmNumberFormat();

    String issueLinkIncorrectUitsNumberFormat();

    String issueLinkIncorrectUitsCaseNotFound(long p0);

    String issueLinkIncorrectCrmCaseNotFound( long p0 );

    String issueLinkIncorrectYouTrackCaseNotFound( String p0 );

    String siteFolderCompanyContacts();

    String siteFolderClone();

    String siteFolderManager();

    String serverAccessParamsColumn();

    String siteFolderAttachments();

    String errSavePlatformConnectedIssuesExist();

    String technicalSupportValidity();

    String workCompletionDate();

    String purchaseDate();

    String technicalSupportValidityNotFound( String p0 );

    String technicalSupportValidityNotDefined();

    String documentCreated( String p0 );

    String devUnitDirection();

    String devUnitProduct();

    String devUnitComponent();

    String devUnitComplex();

    String equipmentProjectNotDefined();

    String errSaveIssueNeedSelectCompany();

    String errSaveIssueFieldsInvalid();

    String aliasInputPlaceholder();

    String employees();

    String employeeAdditionalInfo();

    String employeeEmployeeFullName();

    String employeeWorkPhone();

    String employeeWorkPhonePlaceHolder();

    String employeeMobilePhone();

    String employeeMobilePhonePlaceHolder();

    String employeeEmail();

    String employeeDepartment();

    String employeeID();

    String employeeIPAddress();

    String employeeCompany();

    String employeeMainPosition();

    String employeeFired();

    String employeeShowFired();

    String employeeFireConfirmMessage();

    String employeeFire();

    String employeeSaved();

    String employeeMainInfoTitle();

    String employeeContactInfoTitle();

    String employeeDepartmentHead();

    String employeePosition();

    String employeeContactInfo();

    String employeeTopBrassBtn();

    String employeeTopBrassLabel();

    String employeeAvatarLabelEnabled();

    String employeeAvatarLabelDisabled();

    String employeeAvatarUploadSuccessful();

    String employeeAvatarUploadingFailed();

    String employeePositionAddButton();

    String errEmployeePositionEmpty();

    String employeePositions();

    String employeeContractAgreement();

    String errEmployeePositionAlreadeyAdded();

    String backToEmployees();

    String employeeChangeAccount();

    String employeeRegistrations();

    String employeeRegistrationEmployeeFullNameColumnHeader();

    String employeeRegistrationHeadOfDepartmentColumnHeader();

    String employeeRegistrationEmploymentDateColumnHeader();

    String employeeRegistrationCommonHeader();

    String employeeRegistrationEmployeeFullName();

    String employeeRegistrationHeadOfDepartment();

    String employeeRegistrationEmploymentDate();

    String employeeRegistrationEmploymentType();

    String employeeRegistrationWithRegistration();

    String employeeRegistrationPost();

    String employeeRegistrationProbationPeriod();

    String employeeRegistrationWithoutProbationPeriod();

    String employeeRegistrationProbationPeriodPlaceholder();

    String employeeRegistrationWorkplace();

    String employeeRegistrationComment();

    String employeeRegistrationEquipmentList();

    String employeeRegistrationResourceList();

    String employeeRegistrationPhoneOfficeTypeList();

    String employeeRegistrationSearchFullNameOrPost();

    String employeeRegistrationState();

    String employeeRegistrationCreated();

    String employeeRegistrationEquipmentListPreview();

    String employeeRegistrationEmployeeWithRegistrationTrue();

    String employeeRegistrationEmployeeWithRegistrationFalse();

    String employeeRegistrationLastYoutrackSynchronization();

    String employeeRegistrationValidationEmployeeFullName();

    String employeeRegistrationValidationPosition();

    String employeeRegistrationValidationEmploymentDate();

    String employeeRegistrationValidationHeadOfDepartment();

    String employeeRegistrationValidationCurators();

    String employeeRegistrationValidationHeadOfDepartmentAsCurator();

    String employeeRegistrationValidationProbationPeriod();

    String employeeRegistrationResourceOtherComment();

    String employeeRegistrationNumberColumn();

    String employeeRegistrationCurators();

    String employeeRegistrationAdditionalSoftLengthExceed( int p0 );

    String employeeRegistrationResourceCommentLengthExceed( int p0 );

    String employeeRegistrationOperatingSystemExceed( int p0 );

    String employeeRegistrationPositionExceed( int p0 );

    String employeeRegistrationWorkplaceExceed( int p0 );

    String employeeRegistrationMainInfoTitle();

    String employeeRegistrationWorkplaceTitle();

    String employeeRegistrationResourceTitle();

    String employeeRegistrationResourceHint();

    String employeeRegistrationWorkplaceHint();

    String employeeRegistrationCuratorsHint();

    String goToEmployeeRegistration();

    String employmentTypeFullTime();

    String employmentTypePartTime();

    String employmentTypeRemote();

    String employmentTypeContract();

    String employeeEquipmentTable();

    String employeeEquipmentChair();

    String employeeEquipmentComputer();

    String employeeEquipmentMonitor();

    String employeeEquipmentPhone();

    String internalResourceYoutrack();

    String internalResourceCvs();

    String internalResourceStoreDelivery();

    String internalResourceSvn();

    String internalResourceMercurial();

    String internalResourceGit();

    String internalResourceCrm();

    String internalResourceEmail();

    String phoneOfficeTypeLongDistance();

    String phoneOfficeTypeInternational();

    String phoneOfficeTypeOffice();

    String employeeRegistrationOS();

    String employeeRegistrationOSPlaceholder();

    String employeeRegistrationAdditionalSoft();

    String employeeRegistrationAdditionalSoftPlaceholder();

    String employeeRegistrationInternalResourceOtherCommentPlaceholder();

    String goToIssues();

    String goToPlatforms();

    String employeeWithoutManager();

    String markdownPreview();

    String contractTypeAfterSalesServiceContract();

    String contractTypeExportOfServiceContract();

    String contractTypeGovernmentContract();

    String contractTypeLeaseContract();

    String contractTypeLicenseContract();

    String contractTypeLicenseFrameworkContract();

    String contractTypeMunicipalContract();

    String contractTypeOrder();

    String contractTypePurchaseContract();

    String contractTypeSubcontract();

    String contractTypeSupplyAndWorkContract();

    String contractTypeSupplyAndWorkFrameworkContract();

    String contractTypeSupplyContract();

    String contractTypeSupplyFrameworkContract();

    String contractTypeWorkContract();

    String contractTypeRequest();

    String goToContracts();

    String contractStateAgreement();

    String contractStateHaveOriginal();

    String contractStateCopiesSendToCustomer();

    String contractStateWaitOriginal();

    String contractWaitingCopiesFromCustomer();

    String contractCancelled();

    String contractDescription();

    String contractContractor();

    String contractContractorDropped();

    String contractOrganizationDropped();

    String contractContractorDescription();

    String selectContractContractor();

    String contractContractorOrganizationHint();

    String searchContractorTitle();

    String createContractorTitle();

    String contractContractorName();

    String contractContractorFullName();

    String contractContractorInn();

    String contractContractorKpp();

    String contractContractorCountry();

    String contractContractorValidationError();

    String contractContractorSaveError();

    String contractContractorFindError();

    String contractContractorNotFound();

    String contractContractorFindNotChosenError();

    String contractContractorSelectorPlaceholder();

    String contractContractorCountryPlaceholder();

    String contractProject();

    String contractProjectNotDefined();

    String contractProjectManager();

    String contractCost();

    String contractDirection();

    String contractCurator();

    String contractState();

    String contractType();

    String contractKind();

    String contractNumber();

    String contractCommonHeader();

    String contracts();

    String contractNum( String p0 );

    String contract();

    String contractWorkGroup();

    String contractCostNotDefined();

    String contractSearchPlaceholder();

    String contractPeriodHeader();

    String contractDeliveryAndPaymentsPeriodHeader();

    String contractSpecificationHeader();

    String contractListOfExpenditureHeader();

    String contractSpecificationClausePlaceholder();

    String contractSpecificationTextPlaceholder();

    String contractSpecificationQuantityPlaceholder();

    String contractPaymentCommentPlaceholder();

    String contractDateSigning();

    String contractDateValid();

    String contractPrePayment();

    String contractPostPayment();

    String contractSupply();

    String contractSelectDirection();

    String contractSelectType();

    String contractDateNotDefined();

    String contractValidationEmptyNumber();

    String contractValidationEmptyDescription();

    String contractValidationEmptyType();

    String contractValidationEmptyProject();

    String contractValidationEmptyState();

    String contractValidationEmptyDateSigning();

    String contractValidationEmptyDateValid();

    String contractValidationInvalidDateValid();

    String contractValidationInvalidCost();

    String contractValidationContractSpecification();

    String contractValidationContractSpecificationClauseDuplication();

    String contractOrganization();

    String contractParent();

    String contractChild();

    String contractParentWithout();

    String contractDatesNotify();

    String contractProjectHint();

    String period();

    String reportMissingProduct();

    String reportMissingPeriod();

    String reportNotValidPeriod();

    String reportCaseObjectIsAnySelectedParamNotPresentError();

    String reportCaseObjectAdditionalLimitError();

    String reportCaseObjectPlanInfo();

    String reportPeriodMoreMaxError();

    String reportMissingState();

    String reportTypeCaseResolutionTime();

    String reportTypeCaseTimeElapsedWorkAutor();

    String reportTypeCaseTimeElapsedPeriod();

    String reportTypeProject();

    String reportTypeContract();

    String reportNightWork();

    String reportYtWork();

    String reportYtWorkDates();

    String reportScheduledType();

    String reportScheduledTypeNone();

    String reportScheduledTypeDaily();

    String reportScheduledTypeWeekly();

    String numberUncompletedCases();

    String proteiTitleShort();

    String authDescription();

    String proteiTitleFull();

    String authTitle();

    String rememberMe();

    String timeElapsedTypeNone();

    String timeElapsedTypeWatch();

    String timeElapsedTypeNightWork();

    String timeElapsedTypeSoftInstall();

    String timeElapsedTypeSoftUpdate();

    String timeElapsedTypeSoftConfig();

    String timeElapsedTypeTesting();

    String timeElapsedTypeConsultation();

    String timeElapsedTypeMeeting();

    String timeElapsedTypeDiscussionOfImprovements();

    String timeElapsedTypeLogAnalysis();

    String timeElapsedTypeSolveProblems();

    String issueCommentElapsedTimeTypeLabel();

    String issueCommentElapsedTimeTypeChange();

    String privateComment();

    String departmentAddButton();

    String departmentSelectCompanyLabel();

    String departmentName();

    String departmentCompany();

    String departmentEdit();

    String departmentCreate();

    String positionAddButton();

    String positionSelectCompanyLabel();

    String positionName();

    String positionCompany();

    String positionEdit();

    String positionCreate();

    String issueAssignment();

    String issueAssignmentEntryEdit();

    String issueAssignmentEntryRemove();

    String issueAssignmentTableFilter();

    String issueAssignmentTableFilterDefault();

    String issueAssignmentTableOverflow( int p0 );

    String issueAssignmentIssueAssignTo();

    String issueAssignmentIssueReassignTo();

    String issueAssignmentToggleTable();

    String issueAssignmentReload();

    String issueAssignmentMenuEdit();

    String issueAssignmentMenuActions();

    String issueAssignmentIssueOpen();

    String issueAssignmentToggleDeskRow();

    String issueAssignmentDeskAddColumn();

    String issueAssignmentDeskAddRow();

    String education();

    String educationConference();

    String educationCourse();

    String educationLiterature();

    String educationConferences();

    String educationCourses();

    String educationActualEntries();

    String educationEntryAttendance();

    String educationRequestEntry();

    String educationEntryAttendanceRequested();

    String educationEntryTitle();

    String educationEntryType();

    String educationEntryCoins();

    String educationEntryLink();

    String educationEntryLocation();

    String educationEntryDates();

    String educationEntryDescription();

    String educationEntryImage();

    String educationEntryParticipants();

    String educationEntryActionApprove();

    String educationEntryActionDecline();


    String educationShowOnlyNotApproved();

    String educationShowOutdated();

    String selectImage();


    String companyCategoryCustomer();

    String companyCategoryPartner();

    String companyCategorySubcontractor();

    String companyCategoryOfficial();

    String companyCategoryHome();

    String auditTypeIssueModify();

    String auditTypeIssueCreate();

    String auditTypeIssueReport();

    String auditTypeIssueExport();

    String auditTypeRegionModify();

    String auditTypeRegionReport();

    String auditTypeRegionExport();

    String auditTypeProjectModify();

    String auditTypeProjectCreate();

    String auditTypeCompanyModify();

    String auditTypeCompanyCreate();

    String auditTypeProductModify();

    String auditTypeProductCreate();

    String auditTypeContactModify();

    String auditTypeContactCreate();

    String auditTypeAccountModify();

    String auditTypeAccountCreate();

    String auditTypeAccountRemove();

    String auditTypeEquipmentModify();

    String auditTypeEquipmentCreate();

    String auditTypeEquipmentRemove();

    String auditTypeRoleModify();

    String auditTypeRoleCreate();

    String auditTypeIssueCommentCreate();

    String auditTypeIssueCommentModify();

    String auditTypeIssueCommentRemove();

    String auditTypeAttachmentRemove();

    String auditTypeEquipmentCopy();

    String auditTypeOfficialModify();

    String auditTypeOfficialCreate();

    String auditTypeRoleRemove();

    String auditTypeEmployeeModify();

    String auditTypeEmployeeCreate();

    String auditTypeDepartmentModify();

    String auditTypeDepartmentCreate();

    String auditTypeDepartmentRemove();

    String auditTypeWorkerModify();

    String auditTypeWorkerCreate();

    String auditTypeWorkerRemove();

    String auditTypePositionModify();

    String auditTypePositionCreate();

    String auditTypePositionRemove();

    String auditTypePhotoUpload();

    String auditTypeDocumentModify();

    String auditTypeDocumentRemove();

    String auditTypeEmployeeRegistrationCreate();

    String auditTypeProjectRemove();

    String auditTypeEmployeeRegistrationModify();

    String auditTypeContractModify();

    String auditTypeContractCreate();

    String auditTypeContactFire();

    String auditTypeContactDelete();

    String auditTypeLinkCreate();

    String auditTypeLinkRemove();

    String auditTypeDocumentCreate();

    String auditTypeDocumentTypeCreate();

    String auditTypeDocumentTypeRemove();

    String auditTypePlatformCreate();

    String auditTypePlatformModify();

    String auditTypePlatformRemove();

    String auditTypeServerCreate();

    String auditTypeServerModify();

    String auditTypeServerRemove();

    String auditTypeApplicationCreate();

    String auditTypeApplicationModify();

    String auditTypeApplicationRemove();

    String errPauseDateError();

    String issuePauseDateValidity();

    String ipReservation();

    String subnet();

    String reservedIp();

    String reservedIpOwner();

    String reservedIpAddress();

    String reservedIpIpAddress();

    String reservedIpMacAddress();

    String reservedIpReserveDate();

    String reservedIpReleaseDate();

    String reservedIpForever();

    String reservedIpStatus();

    String reservedIpNonActiveRange();

    String reservedIpStatusCheckInfo();

    String reservedIpUsePeriod();

    String reservedIpWrongSubnetAddress();

    String reservedIpWrongSubnetMask();

    String reservedIpWrongIpAddress();

    String reservedIpWrongMacAddress();

    String reservedIpSubnetAddress();

    String reservedIpSubnetMask();

    String selectReservedIpOwner();

    String reservedIpIpRelease();

    String reservedIpIpReleased();

    String reservedIpSubnetRemove();

    String reservedIpSubnetRemoved();

    String reservedIpSubnetRemoveConfirmMessage();

    String reservedIpSubnetRemoveWithIpsConfirmMessage();

    String reservedIpReleaseConfirmMessage();

    String reservedIpUnableToRemove();

    String reservedIpSubnetUnableToRemove();

    String subnets();

    String reservedIps();

    String reservedIpNumber();

    String reservedIpState();

    String reservedIpExactIp();

    String reservedIpAnyFreeIps();

    String reservedIpSelectedSubnets();

    String monthInterval();

    String fixedInterval();

    String unlimitedInterval();

    String errSaveReservedIpUseInterval();

    String errSaveReservedIpNeedSelectOwner();

    String errSaveReservedIpNeedSelectSubnet();

    String reservedIpWrongNumber(int minCount, int maxCount);

    String reservedIpCreateBy();

    String errSaveReservedIpSubnetDoesNotExist();

    String errSaveReservedIpSubnetNotAvailable();

    String january();

    String february();

    String march();

    String april();

    String may();

    String june();

    String july();

    String august();

    String september();

    String october();

    String november();

    String december();

    String monday();

    String tuesday();

    String wednesday();

    String thursday();

    String friday();

    String saturday();

    String sunday();

    String mondayShort();

    String tuesdayShort();

    String wednesdayShort();

    String thursdayShort();

    String fridayShort();

    String saturdayShort();

    String sundayShort();

    String roomReservation();

    String roomReservationReasonValue0();

    String roomReservationReasonValue1();

    String roomReservationReasonValue2();

    String roomReservationReasonValue3();

    String roomReservationReasonValue4();

    String roomReservationShowTodayButton();

    String roomReservationHourStartButton();

    String roomReservationCreation();

    String roomReservationEditing();

    String roomReservationPersonResponsible();

    String roomReservationRoom();

    String roomReservationDates();

    String roomReservationTime();

    String roomReservationDatesCreateAddWeek();

    String roomReservationDatesCreateAddDay();

    String roomReservationReason();

    String roomReservationCoffeeBreakCount();

    String roomReservationNotifiers();

    String roomReservationComment();

    String roomReservationSaved();

    String roomReservationUpdated();

    String roomReservationRemoved();

    String roomReservationRemoveConfirmMessage();

    String errRoomReservationFinished();

    String errRoomReservationRoomNotAccessible();

    String errRoomReservationHasIntersections();

    String buttonReload();

    String issueAssignmentDeskOverflow(long limit);

    String employeeRegistrationEditHeader();

    String reservedIpAvailableCount();

    String companyAutoOpenIssues();

    String productSelectCommonManager();

    String productCommonManager();

    String reloadPageAfterUpdateHeader();

    String reloadPageAfterUpdateMessage();

    String reloadPageAfterUpdateDoReloadPage();

    String plan();

    String plans();

    String planNameColumn();

    String planPeriodColumn();

    String planCreatorColumn();

    String planIssueQuantityColumn();

    String planSelectCreator();

    String planCreationDate();

    String planStartDate();

    String planFinishDate();

    String planIssuesList();

    String goToPlans();

    String planPeriod();

    String planHeader( String planName );

    String errGetConcreteList(String listName);

    String selectPlan();

    String planHeaderNew();

    String planName();

    String planMainInfoTitle();

    String planSaved();

    String planIssueConfirmRemove();

    String planConfirmRemove();

    String planIssueAdded();

    String planIssueRemoved();

    String planRemoved();

    String planIssueMoved();

    String planAddIssueToPlan();

    String planMoveIssueToAnotherPlan();

    String planEditButton();

    String planEditPopupHeader();

    String errPlanAlreadyExisted();

    String planIssueList();

    String planUnplannedTableFilterDefault();

    String planUnplannedTableFilter();

    String planUnplannedTableLimit( String p0 );

    String errIssueAlreadyExistInPlan();

    String planIssueNumber();

    String issueReportWithDescription();

    String issueReportWithDescriptionTitle();

    String personCaseFilterChange();

    String errPersonCaseFilterChangeError();

    String errPersonCaseFilterInUse();

    String employeeRegistrationDepartment();

    String caseHistory();

    String errPlanNotFound();

    String planSort();

    String commentNewDisabled();

    String commentNewDisabledReasonTerminal();

    String todayInterval();

    String yesterdayInterval();

    String thisWeekInterval();

    String lastWeekInterval();

    String nextWeekInterval();

    String thisMonthInterval();

    String lastMonthInterval();

    String nextMonthInterval();

    String lastPreviousAndThisMonthInterval();

    String thisYearInterval();

    String lastYearInterval();

    String thisWeekAndBeyondInterval();

    String relativeLastDayInterval();

    String relativeLastWeekInterval();

    String relativeLastMonthInterval();

    String relativeLastThreeMonthsInterval();

    String relativeLastHalfYearInterval();

    String relativeLastYearInterval();

    String reservedIpSubnetAllowReserve();

    String errRequest1CFailed();

    String absenceEmployee();

    String selectAbsenceEmployee();

    String absenceDateRange();

    String absenceReason();

    String absenceComment();

    String absenceReasonPersonAffair();

    String absenceReasonBusinessTrip();

    String absenceReasonLocalBusinessTrip();

    String absenceReasonStudy();

    String absenceReasonDisease();

    String absenceReasonSickLeave();

    String absenceReasonGuestPass();

    String absenceReasonNightWork();

    String absenceReasonLeaveWithoutPay();

    String absenceReasonDuty();

    String absenceReasonRemoteWork();

    String selectAbsenceReason();

    String absenceUpdated();

    String absenceValidationEmployee();

    String absenceValidationDateRange();

    String absenceValidationDateRanges();

    String absenceValidationDateRangesIntersection();

    String absenceValidationReason();

    String absenceCreation();

    String absenceEditing();

    String absenceButtonCreate();

    String absenceCreated(@PluralCount int count);

    String absences();

    String absenceFromTime();

    String absenceTillTime();

    String absenceRemoveConfirmMessage();

    String absenceRemovedSuccessfully();

    String absenceReasonLeave();

    String errAbsenceHasIntersections();

    String profileGeneral();

    String profileSubscriptions();

    String profileSubscriptionEmployees();

    String absenceCompletedSuccessfully();

    String absenceComplete();

    String employeeShowAbsent();

    String absenceReport();

    String absenceReportEmployees();

    String absenceReportReasons();

    String absenceReportDateRange();

    String absenceReportTitle();

    String absenceReportHint();

    String absenceButtonSummaryTable();

    String absenceReportDefaultNameTemplate(String date);

    String absenceReportRequestNotification();

    String absenceReportValidationDateRange();

    String buttonSend();

    String errNotCurrentAbsence();

    String contractKindReceipt();

    String contractKindExpenditure();

    String days();

    String withoutVat();

    String vat(long percent);

    String contractSecondExpenditureHeader();

    String contractSecondExpenditureHint();

    String contractSecondExpenditureToggle();

    String savedFilter();

    String filterName();

    String filterRemoveConfirmMessage();

    String filterRemoveSuccessed();

    String filterNotDefined();

    String errorNeedFeelTimeElapsed();

    String errSaveProjectHeadManager();

    String searchProjectTitle();

    String selectContractProject();

    String contractProjectFindNotChosenError();

    String errContractProjectRequired();

    String issueAddToFavorites();

    String issueRemoveFromFavorites();

    String issueFavorites();

    String issueSyncedWith();

    String issueDeadline();

    String issueWorkTrigger();

    String employeeBirthdays();

    String employeeBirthdayPrevMonth();

    String employeeBirthdayResetMonth();

    String employeeBirthdayNextMonth();

    String errContractorNotRemovedHasContracts();

    String contractorRemoveConfirmMessage();

    String contractorRemoved();

    String projectPauseDate(String date);

    String errDashboardChooseFilter();

    String errDashboardTableNameEmpty();

    String topBrass();

    String archive();

    String storeAndDelivery();

    String systemAdministratorLog();

    String floorPlans();

    String boardSearch();

    String delivery();

    String store();

    String supportAndMarketing();

    String notificationSystem();

    String testZones();

    String generalTable();

    String signingDate();

    String attachmentsHeader(String countOfAttachments);

    String attachmentAdd();

    String internalResourceVpn();

    String commentAddMessageMentionPlaceholder();

    String dutyLogCreation();

    String dutyLogEditing();

    String dutyLogDutyDate();

    String dutyLogEmployee();

    String dutyLogRange();

    String dutyLogDuty();

    String dutyLogCreated();

    String dutyLogUpdated();

    String dutyLogReport();

    String dutyLogReportDefaultNameTemplate(String date);

    String dutyLogReportRequestNotification();

    String dutyTypeBG();

    String dutyTypeIP();

    String dutyTypeBilling();

    String dutyTypeMobile();

    String dutyTypeDPI();

    String dutyTypeMKSP_VKS();

    String dutyTypeNGN();

    String dutyTypeSORM();

    String dutyLogType();

    String dutyLog();

    String dutyLogSortDateFrom();

    String dutyLogValidationDateRange();

    String dutyLogValidationEmployee();

    String issueReportWithTags();

    String issueReportWithTagsTitle();

    String issueReportWithLinkedIssues();

    String issueReportWithLinkedIssuesTitle();

    String issueReportAdditionalParams();

    String yes();

    String no();

    String percent();

    String attention();

    String contractDatesWarningCostOverflow();

    String valueNotSet();

    String errEmployeeNotFound();

    String reservedIpExistedIpInRange();

    String issueReportHumanReadable();

    String issueReportHumanReadableTitle();

    String errReportingServiceNotConfigured();

    String dutyTypeCOV();

    String reservedIpReservationStart();

    String reservedIpOnlineTestStart();

    String reservedIpOnlineStatusOnline();

    String reservedIpOnlineStatusOffline();

    String errUserNotFound();

    String errDeadlineError();

    String errRemoteAccessParametersLengthExceeded(int maxLength);

    String errAccessParametersLengthExceeded(int maxLength);

    String errTagNameEmpty();

    String errTagNameAlreadyExists();

    String errTagNameLengthExceeded(int maxLength);

    String errTagNameValidationError();

    String errTagColorEmpty();

    String errTagColorIncorrectFormat();

    String errTagTypeNotSpecified();

    String workTriggerNone();

    String workTriggerPSGO();

    String workTriggerNewRequirements();

    String workTriggerPreCommissioningContract();

    String workTriggerNewPreCommissioningRequirements();

    String workTriggerMarketing();

    String workTriggerOther();

    String overdueDeadlines();

    String fileNotFoundError();

    String projectSubcontractors();

    String subtaskCreate();

    String errNotAllowedCompanyWithAutoOpenIssue();

    String errSaveSubtaskFieldsInvalid();

    String errNotFoundParent();

    String errNotAllowedParentState();

    String parentFor();

    String subtask();

    String errInvalidCaseUpdateSubtaskNotClosed();

    String errNotAllowedIntegrationIssue();

    String help();

    String contractTypeAdditionalAgreement();

    String contractContractSignManager();

    String contractDateColumn();

    String contractCostColumn();

    String contractCommentColumn();

    String contractInAmount(String cost);

    String contractDateType();

    String contractDateComment();

    String contractDateCalendarDay();

    String contractDateDate();

    String contractDateEditHeader();

    String contractDatePercent();

    String contractDatesBaseSection();

    String contractDatesCostSection();

    String contractDatesAdvanceSection();

    String contractDateCostType();

    String contractCostTypeServices();

    String contractCostTypeSoftware();

    String contractCostTypeEquipment();

    String contractValidDay();

    String contractValidDate();

    String contractEDSSigned();

    String contractSignedOnSite();

    String contractTypeContract();

    String contractTypeContractForService();

    String contractTypeAgreement();

    String contractDeliveryNumber();

    String commentReply();

    String commentTimeElapsedTypeEdit();

    String commentEdit();

    String commentRemove();

    String commentRemoveConfirmMessage();

    String issueReportDeadlineWorkTrigger();

    String errMySqlDataTruncation();

    String commentCancelMessage();

    String platformSelectInitiator();

    String errTooMuchPlatforms();

    String issueFilterPlatform();

    String errInvalidFileFormat();

    String deliveryName();

    String deliveryDescription();

    String deliveryProject();

    String deliveryAttribute();

    String deliveryState();

    String deliveryType();

    String deliveryCustomerInfo();

    String deliveryCustomerType();

    String deliveryCustomerCompany();

    String deliveryCustomerInitiator();

    String deliveryManagerInfo();

    String deliveryContractCompany();

    String deliveryManager();

    String deliveryContract();

    String deliveryProducts();

    String deliveryDepartureDate();

    String deliverySubscribers();

    String deliveryKitsAddHeader();

    String deliveryKits();

    String deliveryKitSerialNumber();

    String deliveryKitSerialNumberTitle();

    String deliveryKitStatus();

    String deliveryKitName();

    String deliveryStatePreliminary();

    String deliveryStateReservation();

    String deliveryStateReserved();

    String deliveryStateAssembly();

    String deliveryStateTesting();

    String deliveryStateReady();

    String deliveryStateSent();

    String deliveryStateWorks();

    String deliveryAttributeDelivery();

    String deliveryAttributeTest();

    String deliveryAttributePilotZone();

    String deliveryTypeUpgrade();

    String deliveryTypeUpgradeHW();

    String deliveryTypeUpgradeSW();

    String deliveryTypeReplacementHW();

    String deliveryTypeBugfix();

    String deliveryTypeNewVersion();

    String deliveryTypeNewVersionSW();

    String deliveryTypeNewDelivery();

    String deliveryTypeTrialOperation();

    String deliveryTypeDelivery();

    String deliveryTypeSupport();

    String deliveries();

    String deliveryColumnNumber();

    String deliveryColumnInfo();

    String deliveryColumnContacts();

    String deliveryColumnManager();

    String deliveryFilterNotDefined();

    String deliveryFilterName();

    String deliveryFilterRemoveConfirmMessage();

    String deliveryFilterRemoveSuccessed();

    String deliveryRemoveConfirmMessage(String deliveryName);

    String deliveryRemoveSucceeded();

    String backToDeliveries();

    String selectDeliveryCustomerInitiator();

    String selectDeliveryContract();

    String deliveryButtonRefreshSerialNumber();

    String errDeliverySerialNumberNotAvailable();

    String errModuleSerialNumberNotAvailable();

    String errDeliveryForbiddenChangeStatus();

    String errDeliveryForbiddenChangeProject();

    String deliveryValidationEmptyName();

    String deliveryValidationEmptyState();

    String deliveryValidationInvalidStateAtCreate();

    String deliveryValidationEmptyType();

    String deliveryValidationEmptyProject();

    String deliveryValidationEmptyAttribute();

    String deliveryValidationEmptyContractAtAttributeDelivery();

    String deliveryValidationInvalidKits();

    String deliveryValidationOnlyOneKitForCivilProject();

    String deliveryFilterProduct();

    String deliveryFilterCompany();

    String deliveryFilterManager();

    String caseLinkUits();

    String caseLinkUitsShort();

    String uitsPrefix();

    String deliveryKind();

    String deliveryHwManager();

    String deliveryQcManager();

    String deliveryDepartureInfo();

    String deliveryMilitary();

    String deliveryCivil();

    String deliveryNotDefined();

    String kitSearchHint();

    String moduleStatePreliminary();

    String moduleStateReservation();

    String moduleStateAssembly();

    String moduleStateSetup();

    String moduleStateTesting();

    String moduleStatePackaging();

    String moduleStateSent();

    String moduleStateRepair();

    String moduleStateWriteOff();

    String moduleStatePaused();

    String selectModulesToRemove();

    String moduleRemoveConfirmMessage();

    String modulesRemoved();

    String moduleName();

    String moduleDescription();

    String moduleState();

    String moduleBuildDate();

    String moduleDepartureDate();

    String moduleSerialNumber();

    String moduleValidationEmptyName();

    String moduleValidationEmptyState();

    String moduleValidationInvalidDepartureDate();

    String moduleValidationInvalidBuildDate();

    String moduleCreatedSuccessfully();

    String backToModules();

    String errKitSerialNumberNotMatchDeliveryNumber();

    String documentation();

    String deliveryKit();

    String warnOneKitAllowedForTheOperation();

    String addKits();

    String kitNotSelectedMessage();

    String moduleNotSelectedMessage();

    String buttonAddModule();

    String buttonCopyModule();

    String buttonStateModule();

    String buttonRemoveModule();

    String buttonReloadModule();

    String personDeployManagerShort();

    String personHardwareCuratorShort();

    String personSoftwareCuratorShort();

    String personIntroNewTechSolutionsShort();

    String personLiableForAutoTestingShort();

    String personTechSupportCuratorShort();

    String personProductAssemblerShort();

    String personDeliveryPreparationShort();

    String personEngineerDocDevShort();

    String personTechDocDevShort();

    String personSoftwareDocDevShort();

    String personLiableForCertificationShort();

    String personOkrEscortShort();

    String personQualityControlSmkShort();

    String personCustomerIntegrationShort();

    String personPresaleManagerShort();

    String personBusinessAnalyticsArchitectureShort();

    String personProjectDocumentationShort();

    String personProductManagerShort();

    String personDevelopmentShort();

    String personPresaleHeadManagerShort();

    String personDeployHeadManagerShort();

    String personDeliveryPacking();

    String personEntranceControl();

    String personEquipmentSetup();

    String personOperationalDocumentation();

    String personQadDocumentation();

    String personSpecialCheckSpecialResearch();

    String personDeliveryPackingShort();

    String personEntranceControlShort();

    String personEquipmentSetupShort();

    String personOperationalDocumentationShort();

    String personQadDocumentationShort();

    String personSpecialCheckSpecialResearchShort();

    String errAccountIsLocked();

    String reportYoutrackWorkTypeNiokr();

    String reportYoutrackWorkTypeNma();

    String reportYoutrackWorkDictionaryAdd();

    String reportYoutrackWorkDictionaryCreate();

    String reportYoutrackWorkDictionaryEdit();

    String reportYoutrackWorkDictionaryConfirmRemove(String name);

    String reportYoutrackWorkDictionaryName();
    
    String reportYoutrackWorkDictionaryType();
    
    String reportYoutrackWorkDictionaryProjects();

    String newStoreAndDelivery();

    String card();

    String cardBatch();

    String cardTestDate();

    String cardState();

    String cardStateTesting();

    String cardStateInStock();

    String cardStateInternalUse();

    String cardStateReservation();

    String cardStateSent();

    String cardStateRepair();

    String cardStateWriteOff();

    String cardColumnNumber();

    String cardColumnInfo();

    String cardColumnTestDate();

    String cardColumnManager();

    String cardSerialNumber();

    String cardType();

    String cardArticle();

    String cardNote();

    String cardComment();

    String cardManager();

    String cardSelectType();

    String cardSelectBatch();

    String cardSelectManager();

    String cardSerialNumberPlaceholder();

    String cardEditNoteAndComment();

    String cardValidationErrorSerialNumber();

    String cardValidationErrorState();

    String cardValidationErrorType();

    String cardValidationErrorCardBatch();

    String cardValidationErrorArticle();

    String cardValidationErrorManager();

    String cardValidationErrorTestDate();

    String cardBatchNumber();

    String cardBatchType();

    String cardBatchArticle();

    String cardBatchSearchPlaceholder();

    String cardBatchAmount();

    String cardBatchAmountOrdered();

    String cardBatchAmountManufactured();

    String cardBatchAmountFree();

    String cardBatchParams();

    String cardBatchArticlePlaceholder();

    String cardBatchNumberPlaceholder();

    String cardBatchPreviousInfo(String number, int amount, String state);

    String cardBatchState();

    String cardBatchPriority();

    String cardBatchDeadline();

    String cardBatchContractors();

    String cardBatchRemoved();

    String cardBatchSaved();

    String cardBatchCreated();

    String cardBatchStatePreliminary();

    String cardBatchStateActual();

    String cardBatchStateOrdered();

    String cardBatchStateReserved();

    String cardBatchStateMounting();

    String cardBatchStateMounted();

    String personAutomaticMounting();

    String personManualMounting();

    String personOutputControl();

    String personAutomaticMountingShort();

    String personManualMountingShort();

    String personOutputControlShort();

    String cardBatchTypeValidationError();

    String cardBatchNumberValidationError();

    String cardBatchArticleValidationError();

    String cardBatchAmountValidationError();

    String cardBatchDeadlineValidationError();

    String cardBatchContractorsValidationError();

    String cardBatchGetLastNumberError();

    String cardBatchNumberExceedLimitError();

    String cardBatchStateBuildEquipmentInQueue();

    String cardBatchStateBuildEquipment();

    String cardBatchStateAutomaticMountingInQueue();

    String cardBatchStateAutomaticMounting();

    String cardBatchStateManualMountingInQueue();

    String cardBatchStateManualMounting();

    String cardBatchStateStickerLabelingInQueue();

    String cardBatchStateStickerLabeling();

    String cardBatchStateTransferredForTesting();

    String newEmployeeBook();
}
