package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;


public class En_PersonRoleTypeLang {

    public String getName(En_DevUnitPersonRoleType type) {

        switch (type) {
            case HEAD_MANAGER:
                return lang.personHeadManager();
            case DEPLOY_MANAGER:
                return lang.personDeployManager();
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
                return lang.personHardwareCurator();
            case SOFTWARE_CURATOR:
                return lang.personSoftwareCurator();
            case INTRO_NEW_TECH_SOLUTIONS:
                return lang.personIntroNewTechSolutions();
            case LIABLE_FOR_AUTO_TESTING:
                return lang.personLiableForAutoTesting();
            case TECH_SUPPORT_CURATOR:
                return lang.personTechSupportCurator();
            case PRODUCT_ASSEMBLER:
                return lang.personProductAssembler();
            case SUPPLY_PREPARATION:
                return lang.personSupplyPreparation();
            case ENGINEER_DOC_DEV:
                return lang.personEngineerDocDev();
            case TECH_DOC_DEV:
                return lang.personTechDocDev();
            case SOFTWARE_DOC_DEV:
                return lang.personSoftwareDocDev();
            case LIABLE_FOR_CERTIFICATION:
                return lang.personLiableForCertification();
            case OKR_ESCORT:
                return lang.personOkrEscort();
            case QUALITY_CONTROL_SMK:
                return lang.personQualityControlSmk();
            case CUSTOMER_INTEGRATION:
                return lang.personCustomerIntegration();
            case PRESALE_MANAGER:
                return lang.personPresaleManager();
            case BUSINESS_ANALYTICS_ARCHITECTURE:
                return lang.personBusinessAnalyticsArchitecture();
            case PROJECT_DOCUMENTATION:
                return lang.personProjectDocumentation();
            case DEVELOPMENT:
                return lang.personDevelopment();
            case PRESALE_HEAD_MANAGER:
                return lang.personPresaleHeadManager();
            case DEPLOY_HEAD_MANAGER:
                return lang.personDeployHeadManager();
        }
        return null;
    }

    @Inject
    Lang lang;
}
