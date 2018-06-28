package ru.protei.portal.hpsm.factories;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.EventPublisherService;
import ru.protei.portal.hpsm.api.HpsmSeverity;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.handlers.HpsmEventHandler;
import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.logic.ServiceInstance;
import ru.protei.portal.hpsm.struct.HpsmAttachment;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.utils.HpsmUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class HpsmEventHandlerFactoryImpl implements HpsmEventHandlerFactory{

    public HpsmEventHandlerFactoryImpl() { }

    @Override
    public HpsmEventHandler createHandler (HpsmEvent request, ServiceInstance instance) {
        if (request.getSubject().isNewCaseRequest()) {
            logger.debug("new case creation handler");
            return new CreateNewCaseHandler();
        }

        logger.debug("prepare case update handler");
        ExternalCaseAppData appData = externalCaseAppDAO.getByExternalAppId(request.getSubject().getHpsmId());

        CaseObject object = appData == null ? null : caseObjectDAO.get(appData.getId());

        if (object != null && HpsmUtils.testBind(object, instance)) {

//            if (object.getInitiatorCompanyId() == null || !object.getInitiatorCompanyId().equals(request.getCompany().getId()))
//                return new RejectHandler("Wrong company");
            logger.debug("return update handler");
            return new UpdateCaseHandler(object, caseObjectDAO.get(object.getId()));
        }
        else {
            logger.debug("case {} is not bound to service instance {}", request.getSubject().getHpsmId(), instance.id());
        }

        return new RejectHandler("Case object was not found");
    }

    public class CreateNewCaseHandler implements HpsmEventHandler {
        @Override
        public void handle(HpsmEvent request, ServiceInstance instance) throws Exception {

            Person contactPerson = getAssignedPerson(request.getCompany().getId(), request.getHpsmMessage());
            if (contactPerson == null) {
                instance.sendReject(request, "No contact person provided");
                return;
            }

            DevUnit product = getAssignedProduct(request.getHpsmMessage());
            if (product == null) {
                instance.sendReject(request, "product not found");
                return;
            }

            CaseObject ex_test = caseObjectDAO.getByCondition("ext_app_id=?", request.getSubject().getHpsmId());
            if (ex_test != null) {
                instance.sendReject(request, "already_registered");
                return;
            }


            CaseObject obj = new CaseObject();
            obj.setCreated(new Date());
            obj.setModified(new Date());

            HpsmUtils.bindCase(obj, instance);


            obj.setCaseType(En_CaseType.CRM_SUPPORT);
            obj.setProduct(product);
            obj.setInitiator(contactPerson);
            obj.setInitiatorCompany(request.getCompany());

            if (HelperFunc.isNotEmpty(request.getHpsmMessage().getContactPersonEmail()))
                obj.setEmails(request.getHpsmMessage().getContactPersonEmail());

            obj.setImpLevel(HelperFunc.nvlt(request.getHpsmMessage().severity(), HpsmSeverity.LEVEL3).getCaseImpLevel().getId());
            obj.setName(HelperFunc.nvlt(request.getHpsmMessage().getShortDescription(),request.getSubject().getHpsmId()));
            obj.setInfo(request.getHpsmMessage().getDescription());
            obj.setLocal(0);
            obj.setStateId(En_CaseState.CREATED.getId());
            obj.setProduct(product);

            Long caseObjId = caseObjectDAO.insertCase(obj);

            if (caseObjId != null && caseObjId > 0L) {

                HpsmMessageHeader replySubj = new HpsmMessageHeader(request.getSubject().getHpsmId(), obj.getExtId(), HpsmStatus.REGISTERED);
                HpsmMessage replyEvent = request.getHpsmMessage().createCopy();

                replyEvent.status(HpsmStatus.REGISTERED);
                replyEvent.setOurRegistrationTime(obj.getCreated());
                replyEvent.setOurId(obj.getExtId());

                ExternalCaseAppData appData = new ExternalCaseAppData(obj);
                appData.setExtAppCaseId(request.getHpsmMessage().getHpsmId());
                appData.setExtAppData(xstream.toXML(replyEvent));

                logger.debug("create hpsm-case id={}, ext={}, data={}", appData.getId(), appData.getExtAppCaseId(), appData.getExtAppData());

                externalCaseAppDAO.merge(appData);

                logger.debug("publish event on create case id={}, ext={}", obj.getId(), obj.getExtId());

                eventPublisherService.publishEvent(new CaseObjectEvent(ServiceModule.HPSM, caseService, obj, null, contactPerson));

                createComment(request, contactPerson, obj, caseObjId);

                instance.sendReply(request.getEmailSourceAddr(), replySubj, replyEvent);
            }
            else {
                instance.sendReject(request.getEmailSourceAddr(), request, "system error");
            }
        }
    }

    public class RejectHandler implements HpsmEventHandler {

        private String messageText;

        public RejectHandler(String message) {
            this.messageText = message;
        }

        @Override
        public void handle(HpsmEvent request, ServiceInstance instance) throws Exception {
            instance.sendReject(request, messageText);
        }
    }

    public class UpdateCaseHandler implements HpsmEventHandler {

        CaseObject object;
        final CaseObject oldState;
        private final HpsmStatusHandlerFactory statusHandlerFactory = new HpsmStatusHandlerFactoryImpl();

        public UpdateCaseHandler(CaseObject object, CaseObject oldState) {
            this.object = object;
            this.oldState = oldState;
        }

        @Override
        public void handle(HpsmEvent request, ServiceInstance instance) throws Exception {
            Person contactPerson = request.getCompany() != null ? getAssignedPerson(request.getCompany().getId(), request.getHpsmMessage()) : null;

            if (contactPerson == null) {
                contactPerson = this.object.getInitiator();
            }

            logger.debug("contact-person : {} (id={})", contactPerson.getDisplayName(), contactPerson.getId());

            ExternalCaseAppData appData = externalCaseAppDAO.get(object.getId());

            HpsmMessage currState = (HpsmMessage) xstream.fromXML(appData.getExtAppData());

            StringBuilder commentText = new StringBuilder();

            CaseComment comment = new CaseComment();
            comment.setCreated(new Date());
            comment.setAuthor(contactPerson);
            comment.setCaseId(object.getId());
            comment.setClientIp("hpsm");

            if (request.getSubject().getStatus() != null && currState.status() != request.getSubject().getStatus()) {
                commentText.append(currState.status()).append(" -> ").append(request.getSubject().getStatus()).append("\n");

                logger.debug("change case {} state from {} to {}", appData.getExtAppCaseId(), currState.status(), request.getSubject().getStatus());

                currState.status(request.getSubject().getStatus());
            }


            if (request.getSubject().getStatus() != null) {
                statusHandlerFactory.createHandler(currState, request.getSubject().getStatus())
                        .handle(object, comment);
            }

            currState.updateCustomerFields(request.getHpsmMessage());
            appData.setExtAppData(xstream.toXML(currState));

            logger.debug("case {} state after merge: {}", appData.getExtAppCaseId(), appData.getExtAppData());

            if (!contactPerson.getId().equals(object.getInitiatorId())) {
                logger.debug("change initiator from {} to {}", object.getInitiator().getDisplayName(), contactPerson.getDisplayName());
                object.setInitiator(contactPerson);
            }

            if (request.getHpsmMessage().severity() != null) {
                logger.debug("set severity to {}", request.getHpsmMessage().severity());
                int importanceId = request.getHpsmMessage().severity().getCaseImpLevel().getId();
                object.setImpLevel(importanceId);
                comment.setCaseImpLevel(importanceId);
            }

            caseObjectDAO.merge(object);
            externalCaseAppDAO.merge(appData);

            logger.debug("case and data stored in db for {}", appData.getExtAppCaseId());

            logger.debug("publish event on update case id={}, ext={}", object.getId(), object.getExtId());

            eventPublisherService.publishEvent(new CaseObjectEvent(ServiceModule.HPSM, caseService, object, oldState, contactPerson));


            if (HelperFunc.isNotEmpty(request.getHpsmMessage().getMessage())) {
                logger.debug("append comment text from message");
                commentText.append(request.getHpsmMessage().getMessage());
            }

            logger.debug("case {}, add comment: {}", appData.getExtAppCaseId(), commentText.toString());

            comment.setText(commentText.toString());

            processStoreComment(request, contactPerson, object, object.getId(), comment);

            logger.debug("comment added in db, id={}", comment.getId());
        }
    }

    private DevUnit getAssignedProduct (HpsmMessage msg) {

        return devUnitDAO.checkExistsByName(En_DevUnitType.PRODUCT, msg.getProductName());

    }

    private Person getAssignedPerson (Long companyId, HpsmMessage msg) {

        Person person = null;

        if (HelperFunc.isEmpty(msg.getContactPersonEmail()) && HelperFunc.isEmpty(msg.getContactPerson())) {
            logger.debug("no contact data provided for request {}", msg.getHpsmId());
            return null;
        }

        if (HelperFunc.isNotEmpty(msg.getContactPersonEmail())) {
            // try find by e-mail
            person = personDAO.findContactByEmail(companyId, msg.getContactPersonEmail());
        }

        if (person == null && HelperFunc.isNotEmpty(msg.getContactPerson())) {
            // try find by name
            person = personDAO.findContactByName(companyId, msg.getContactPerson());
        }


        if (person != null) {
            logger.debug("contact found: {} (id={}), request {}", person.getDisplayName(), person.getId(), msg.getHpsmId());
        }
        else {
            logger.debug("unable to find contact person : email={}, company={}, create new one", msg.getContactPersonEmail(), companyId);

            person = new Person();
            person.setCreated(new Date());
            person.setCreator("hpsm");
            person.setCompanyId(companyId);

            if (HelperFunc.isEmpty(msg.getContactPerson())) {
                person.setFirstName("?");
                person.setLastName("?");
            }
            else {
                String[] np = msg.getContactPerson().split ("\\s+");
                person.setLastName(np[0]);
                person.setFirstName(np.length > 1 ? np[1] : "?");
                person.setSecondName(np.length > 2 ? np[2] : "");
            }

            person.setDisplayName(msg.getContactPerson());

            PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade();

            if (msg.getContactPersonEmail() != null)
                contactInfoFacade.setEmail(msg.getContactPersonEmail());

            if (msg.getAddress() != null)
                contactInfoFacade.setFactAddress(msg.getAddress());

            if (msg.getWebSite() != null)
                contactInfoFacade.setWebSite(msg.getWebSite());

            person.setContactInfo(contactInfoFacade.editInfo());

            person.setGender(En_Gender.UNDEFINED);
            person.setDeleted(false);
            person.setFired(false);

            personDAO.persist(person);
        }

        return person;
    }

    private CaseComment processStoreComment(HpsmEvent request, Person contactPerson, CaseObject obj, Long caseObjId, CaseComment comment) {
        commentDAO.persist(comment);
        logger.debug("add comment to new case, case-id={}, comment={}", caseObjId, comment.getId());


        Collection<Attachment> addedAttachments = null;
        if (request.hasAttachments()) {
            logger.debug("process attachments for new case, id={}", caseObjId);

            List<CaseAttachment> caseAttachments = new ArrayList<>(request.getAttachments().size());
            addedAttachments = new ArrayList<>(request.getAttachments().size());

            for (HpsmAttachment in : request.getAttachments()) {
                Attachment a = new Attachment();
                a.setCreated(new Date());
                a.setCreatorId(contactPerson.getId());
                a.setDataSize((long)in.getSize());
                a.setFileName(in.getFileName());
                a.setMimeType(in.getContentType());
                a.setLabelText(in.getDescription());

                addedAttachments.add(a);

                try {
                    logger.debug("invoke file controller to store attachment {} (size={})", in.getFileName(), in.getSize());
                    Long caId = fileController.saveAttachment(a, in.getStreamSource(), caseObjId);
                    logger.debug("result from file controller = {} for {} (size={})", caId, in.getFileName(), in.getSize());

                    if (caId != null) {
                        caseAttachments.add(new CaseAttachment(caseObjId, a.getId(), comment.getId(), caId));
                    }
                }
                catch (Exception e) {
                    logger.debug("unable to process attachment {}", in.getFileName());
                    logger.debug("trace", e);
                }
            }

            comment.setCaseAttachments(caseAttachments);
        }

        eventPublisherService.publishEvent(new CaseCommentEvent(
                ServiceModule.HPSM,
                caseService,
                obj,
                null,
                null,
                comment,
                addedAttachments,
                contactPerson
        ));

        return comment;
    }

    private CaseComment createComment(HpsmEvent request, Person contactPerson, CaseObject obj, Long caseObjId) {
        StringBuilder commentText = new StringBuilder();
        if (HelperFunc.isNotEmpty(request.getHpsmMessage().getMessage())) {
            commentText.append(request.getHpsmMessage().getMessage());
        }

        CaseComment comment = new CaseComment();
        comment.setCreated(new Date());
        comment.setAuthor(contactPerson);
        comment.setCaseId(caseObjId);
        comment.setCaseStateId(obj.getStateId());
        comment.setCaseImpLevel(obj.getImpLevel());
        comment.setClientIp("hpsm");
        comment.setText(appendCommentInfo (commentText, request.getHpsmMessage()).toString());

        return processStoreComment(request, contactPerson, obj, caseObjId, comment);
    }

    private StringBuilder appendCommentInfo (StringBuilder commentText, HpsmMessage msg) {
        commentText.append("--").append("\n");

        if (HelperFunc.isNotEmpty(msg.getGeoRegion())) {
            commentText.append("Region: ").append(msg.getGeoRegion()).append("\n");
        }

        if (HelperFunc.isNotEmpty(msg.getCity())) {
            commentText.append("City: ").append(msg.getCity()).append("\n");
        }

        if (HelperFunc.isNotEmpty(msg.getAddress())) {
            commentText.append("Address: ").append(msg.getAddress()).append("\n");
        }

        if (HelperFunc.isNotEmpty(msg.getLogicalName())) {
            commentText.append("Product name: ").append(msg.getLogicalName()).append("\n");
        }

        if (HelperFunc.isNotEmpty(msg.getManufacturer())) {
            commentText.append("Manufacturer: ").append(msg.getManufacturer()).append("\n");
        }

        if (HelperFunc.isNotEmpty(msg.getVersion())) {
            commentText.append("Version: ").append(msg.getVersion()).append("\n");
        }

        return commentText;
    }

    @Autowired
    private FileController fileController;

    @Autowired
    @Qualifier("hpsmSerializer")
    private XStream xstream;

    @Autowired
    private CaseCommentDAO commentDAO;

    @Autowired
    private DevUnitDAO devUnitDAO;

    @Autowired
    private ExternalCaseAppDAO externalCaseAppDAO;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private EventPublisherService eventPublisherService;

    @Autowired
    private CaseService caseService;

    private static Logger logger = LoggerFactory.getLogger(HpsmEventHandlerFactoryImpl.class);

}
