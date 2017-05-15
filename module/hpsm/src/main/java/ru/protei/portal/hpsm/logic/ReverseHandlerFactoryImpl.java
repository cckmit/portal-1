package ru.protei.portal.hpsm.logic;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.hpsm.api.HpsmSeverity;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.utils.HpsmUtils;

/**
 * Created by michael on 15.05.17.
 */
public class ReverseHandlerFactoryImpl implements ReverseHandlerFactory {

    @Autowired
    @Qualifier("hpsmSerializer")
    private XStream xstream;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    private static Logger logger = LoggerFactory.getLogger(ReverseHandlerFactoryImpl.class);

    public ReverseHandlerFactoryImpl() {
    }

    @Override
    public ReverseEventHandler createHandler(HpsmMessage currentState, CaseObjectEvent event) {

        if (event.isCreateEvent()) {
            return new NoActionHandler ();
        }

        if (event.isCaseStateChanged()) {
            //
        }

        if (event.isManagerChanged() || event.isCaseImportanceChanged()) {

        }

        // by default
        return new NoActionHandler ();
    }

    public class MakeMessageAction implements  ReverseEventHandler {
        @Override
        public void handle(CaseObjectEvent event, HpsmMessage message, ServiceInstance instance) {

            CaseObject object = event.getNewState();

            message.severity(HpsmSeverity.find(object.importanceLevel()));
            message.setProductName(object.getProduct() != null ? object.getProduct().getName() : "");

            if (object.getManager() != null) {
                message.setOurManager(object.getManager().getDisplayName());
                PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade(object.getManager().getContactInfo());
                message.setOurManagerEmail(contactInfoFacade.getEmail());
            }

            object.setExtAppData(xstream.toXML(message));
            caseObjectDAO.saveExtAppData(object);


        }
    }


    public class NoActionHandler implements ReverseEventHandler {
        @Override
        public void handle(CaseObjectEvent event, HpsmMessage message, ServiceInstance instance) {
            logger.debug("no action for case {} / instance {}", event.getCaseObject().getExtId(), instance.id());
        }
    }
}
