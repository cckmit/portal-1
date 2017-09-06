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
        }
        return null;
    }

    @Inject
    Lang lang;
}
