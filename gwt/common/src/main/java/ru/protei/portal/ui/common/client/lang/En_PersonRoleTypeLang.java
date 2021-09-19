package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;


public class En_PersonRoleTypeLang {

    public String getName(En_DevUnitPersonRoleType type) {

        switch (type) {
            case HEAD_MANAGER:
                return lang.personHeadManager();
            case DEPLOY_MANAGER:
                return lang.personDeployManager() + " (" + lang.personDeployManagerShort() + ")";
            case DECISION_CENTER:
                return lang.personDecisionCenter();
            case CHIEF_DECISION_MAKER:
                return lang.personChiefDecisionMaker();
            case KEEPER:
                return lang.personDecisionKeeper();
            case TECH_SPECIALIST:
                return lang.personTechSpecialist();
            case INFLUENCE_MAKER:
                return lang.personInfluenceMaker();
            case CHIEF_INFLUENCE_MAKER:
                return lang.personChielInfluenceMaker();
            case ECONOMIST:
                return lang.personEconomist();
            case WELL_WISHER:
                return lang.personWellWisher();
            case RECEPTIVITY_CENTER:
                return lang.personReceptivityCenter();
            case HARDWARE_CURATOR:
                return lang.personHardwareCurator() + " (" + lang.personHardwareCuratorShort() + ")";
            case SOFTWARE_CURATOR:
                return lang.personSoftwareCurator() + " (" + lang.personSoftwareCuratorShort() + ")";
            case INTRO_NEW_TECH_SOLUTIONS:
                return lang.personIntroNewTechSolutions() + " (" + lang.personIntroNewTechSolutionsShort() + ")";
            case LIABLE_FOR_AUTO_TESTING:
                return lang.personLiableForAutoTesting() + " (" + lang.personLiableForAutoTestingShort() + ")";
            case TECH_SUPPORT_CURATOR:
                return lang.personTechSupportCurator() + " (" + lang.personTechSupportCuratorShort() + ")";
            case PRODUCT_ASSEMBLER:
                return lang.personProductAssembler() + " (" + lang.personProductAssemblerShort() + ")";
            case SUPPLY_PREPARATION:
                return lang.personDeliveryPreparation() + " (" + lang.personDeliveryPreparationShort() + ")";
            case ENGINEER_DOC_DEV:
                return lang.personEngineerDocDev() + " (" + lang.personEngineerDocDevShort() + ")";
            case TECH_DOC_DEV:
                return lang.personTechDocDev() + " (" + lang.personTechDocDevShort() + ")";
            case SOFTWARE_DOC_DEV:
                return lang.personSoftwareDocDev() + " (" + lang.personSoftwareDocDevShort() + ")";
            case LIABLE_FOR_CERTIFICATION:
                return lang.personLiableForCertification() + " (" + lang.personLiableForCertificationShort() + ")";
            case OKR_ESCORT:
                return lang.personOkrEscort() + " (" + lang.personOkrEscortShort() + ")";
            case QUALITY_CONTROL_SMK:
                return lang.personQualityControlSmk() + " (" + lang.personQualityControlSmkShort() + ")";
            case CUSTOMER_INTEGRATION:
                return lang.personCustomerIntegration() + " (" + lang.personCustomerIntegrationShort() + ")";
            case PRESALE_MANAGER:
                return lang.personPresaleManager() + " (" + lang.personPresaleManagerShort() + ")";
            case BUSINESS_ANALYTICS_ARCHITECTURE:
                return lang.personBusinessAnalyticsArchitecture() + " (" + lang.personBusinessAnalyticsArchitectureShort() + ")";
            case PROJECT_DOCUMENTATION:
                return lang.personProjectDocumentation() + " (" + lang.personProjectDocumentationShort() + ")";
            case PRODUCT_MANAGER:
                return lang.personProductManager() + " (" + lang.personProductManagerShort() + ")";
            case DEVELOPMENT:
                return lang.personDevelopment() + " (" + lang.personDevelopmentShort() + ")";
            case PRESALE_HEAD_MANAGER:
                return lang.personPresaleHeadManager() + " (" + lang.personPresaleHeadManagerShort() + ")";
            case DEPLOY_HEAD_MANAGER:
                return lang.personDeployHeadManager() + " (" + lang.personDeployHeadManagerShort() + ")";
            case DELIVERY_PACKING:
                return lang.personDeliveryPacking() + " (" + lang.personDeliveryPackingShort() + ")";
            case ENTRANCE_CONTROL:
                return lang.personEntranceControl() + " (" + lang.personEntranceControlShort() + ")";
            case EQUIPMENT_SETUP:
                return lang.personEquipmentSetup() + " (" + lang.personEquipmentSetupShort() + ")";
            case OPERATIONAL_DOCUMENTATION:
                return lang.personOperationalDocumentation() + " (" + lang.personOperationalDocumentationShort() + ")";
            case QAD_DOCUMENTATION:
                return lang.personQadDocumentation() + " (" + lang.personQadDocumentationShort() + ")";
            case SPECIAL_CHECK_SPECIAL_RESEARCH:
                return lang.personSpecialCheckSpecialResearch() + " (" + lang.personSpecialCheckSpecialResearchShort() + ")";

        }
        return null;
    }

    public String getShortName(En_DevUnitPersonRoleType type) {

        switch (type) {
            case HEAD_MANAGER:
                return lang.personHeadManager();
            case DEPLOY_MANAGER:
                return lang.personDeployManagerShort();
            case HARDWARE_CURATOR:
                return lang.personHardwareCuratorShort();
            case SOFTWARE_CURATOR:
                return lang.personSoftwareCuratorShort();
            case INTRO_NEW_TECH_SOLUTIONS:
                return lang.personIntroNewTechSolutionsShort();
            case LIABLE_FOR_AUTO_TESTING:
                return lang.personLiableForAutoTestingShort();
            case TECH_SUPPORT_CURATOR:
                return lang.personTechSupportCuratorShort();
            case PRODUCT_ASSEMBLER:
                return lang.personProductAssemblerShort();
            case SUPPLY_PREPARATION:
                return lang.personDeliveryPreparationShort();
            case ENGINEER_DOC_DEV:
                return lang.personEngineerDocDevShort();
            case TECH_DOC_DEV:
                return lang.personTechDocDevShort();
            case SOFTWARE_DOC_DEV:
                return lang.personSoftwareDocDevShort();
            case LIABLE_FOR_CERTIFICATION:
                return lang.personLiableForCertificationShort();
            case OKR_ESCORT:
                return lang.personOkrEscortShort();
            case QUALITY_CONTROL_SMK:
                return lang.personQualityControlSmkShort();
            case CUSTOMER_INTEGRATION:
                return lang.personCustomerIntegrationShort();
            case PRESALE_MANAGER:
                return lang.personPresaleManagerShort();
            case BUSINESS_ANALYTICS_ARCHITECTURE:
                return lang.personBusinessAnalyticsArchitectureShort();
            case PROJECT_DOCUMENTATION:
                return lang.personProjectDocumentationShort();
            case PRODUCT_MANAGER:
                return lang.personProductManagerShort();
            case DEVELOPMENT:
                return lang.personDevelopmentShort();
            case PRESALE_HEAD_MANAGER:
                return lang.personPresaleHeadManagerShort();
            case DEPLOY_HEAD_MANAGER:
                return lang.personDeployHeadManagerShort();
            case DELIVERY_PACKING:
                return lang.personDeliveryPackingShort();
            case ENTRANCE_CONTROL:
                return lang.personEntranceControlShort();
            case EQUIPMENT_SETUP:
                return lang.personEquipmentSetupShort();
            case OPERATIONAL_DOCUMENTATION:
                return lang.personOperationalDocumentationShort();
            case QAD_DOCUMENTATION:
                return lang.personQadDocumentationShort();
            case SPECIAL_CHECK_SPECIAL_RESEARCH:
                return lang.personSpecialCheckSpecialResearchShort();
        }
        return null;
    }

    @Inject
    Lang lang;
}
