package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;

public class CardBatchStateLang {

    public String getStateName(CaseState state) {
        if (state == null || state.getState() == null) {
            return lang.errUnknownResult();
        }

        switch (state.getState().toLowerCase()) {
            case "in queue: build equipment": return lang.cardBatchStateBuildEquipmentInQueue();
            case "build equipment": return lang.cardBatchStateBuildEquipment();
            case "in queue: automatic mounting": return lang.cardBatchStateAutomaticMountingInQueue();
            case "automatic mounting": return lang.cardBatchStateAutomaticMounting();
            case "in queue: manual mounting": return lang.cardBatchStateManualMountingInQueue();
            case "manual mounting": return lang.cardBatchStateManualMounting();
            case "in queue: sticker labeling": return lang.cardBatchStateStickerLabelingInQueue();
            case "sticker labeling": return lang.cardBatchStateStickerLabeling();
            case "transferred for testing": return lang.cardBatchStateTransferredForTesting();
            default: return state.getState();
        }
    }

    @Inject
    Lang lang;
}
