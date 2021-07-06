package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;

/**
 * Названия статусов модулей
 */
public class ModuleStateLang {

    public String getStateName(CaseState state) {
        if (state == null || state.getState() == null) {
            return lang.errUnknownResult();
        }

        switch (state.getState().toLowerCase()) {
            case "preliminary": return lang.moduleStatePreliminary();
            case "reservation": return lang.moduleStateReservation();
            case "assembly": return lang.moduleStateAssembly();
            case "setup": return lang.moduleStateSetup();
            case "testing": return lang.moduleStateTesting();
            case "packaging": return lang.moduleStatePackaging();
            case "sent": return lang.moduleStateSent();
            case "repair": return lang.moduleStateRepair();
            case "write-off": return lang.moduleStateWriteOff();
            case "paused": return lang.moduleStatePaused();
            default: return state.getState();
        }
    }

    @Inject
    Lang lang;
}
