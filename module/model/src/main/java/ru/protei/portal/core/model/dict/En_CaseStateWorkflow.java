package ru.protei.portal.core.model.dict;

import ru.protei.portal.core.model.ent.CaseStateWorkflowLink;
import ru.protei.winter.core.utils.enums.HasId;

/**
 * Рабочий процесс, которому подчиняется смена статусов {@link ru.protei.portal.core.model.ent.CaseState}.
 * Каждый рабочий процесс должен быть объявлен в бд в таблице case_state_workflow.
 * Тип NO_WORKFLOW не содержит правил смены статусов. Разрешены любые переходы.
 * Для всех остальных типов должны быть указаны переходы в таблице case_state_workflow_link {@link CaseStateWorkflowLink}.
 */
public enum En_CaseStateWorkflow implements HasId {
    NO_WORKFLOW(0),
    NX_JIRA(1),
    REDMINE(2),
    ;

    En_CaseStateWorkflow(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    private int id;
}
