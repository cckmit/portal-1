package ru.protei.portal.core.client.uits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.util.CrmConstants;

public class UitsDealStageMapping {
    public static Long toCaseState(String uitsStageId) {
        if (uitsStageId == null)
            return null;
        switch (uitsStageId) {
            //Новый (не рассмотрен)
            case "NEW":
                return CrmConstants.State.CREATED;
            //В работе
            case "EXECUTING":
                return CrmConstants.State.ACTIVE;
            //Подготовка отчета
            case "1":
                //Ожидание обратной связи
            case "2":
                return CrmConstants.State.DONE;
            //Отклонено (отказ)
            case "LOSE":
                return CrmConstants.State.CANCELED;
            //Решено (Услуги оказаны)
            case "WON":
                return CrmConstants.State.VERIFIED;
            default:
                return null;
//                return CrmConstants.State.IGNORED;
//                return CrmConstants.State.CLOSED;
        }
    }
    private static final Logger log = LoggerFactory.getLogger(UitsDealStageMapping.class);
}
