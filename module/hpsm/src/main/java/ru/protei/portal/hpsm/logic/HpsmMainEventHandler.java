package ru.protei.portal.hpsm.logic;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.utils.HpsmUtils;

import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * Created by michael on 25.04.17.
 */
public class HpsmMainEventHandler implements HpsmHandler {

    private static Logger logger = LoggerFactory.getLogger(HpsmMainEventHandler.class);

    @Autowired
    @Qualifier("hpsmSerializer")
    private XStream xstream;

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private DevUnitDAO devUnitDAO;

    @Autowired
    private CaseCommentDAO commentDAO;


    @Override
    public boolean handle(MimeMessage msg, ServiceInstance instance) {

        HpsmMessageHeader subject = null;

        try {
            subject = HpsmMessageHeader.parse(msg.getSubject());

            if (subject == null)
                return false;

            HpsmEvent request = buildRequest(subject, instance, msg);

            if (request == null) {
                logger.debug("unable to create request");
                instance.sendReject(HpsmUtils.getEmailFromAddress(msg), subject, "wrong request data");
                return true;
            }

            HpsmEventHandler handler = createHandler(request);
            logger.debug("created handler : {}", handler);

            handler.handle(request, instance);
        } catch (Throwable e) {
            logger.debug("error on event message handle", e);
        }


        return subject != null;
    }


    private HpsmEvent buildRequest (HpsmMessageHeader subject, ServiceInstance instance, MimeMessage msg) throws Exception {

        logger.debug("Got inbound event-message {}", subject.toString());

        HpsmEvent hpsmEvent = HpsmUtils.parseEvent(msg, xstream);

        if (hpsmEvent.getHpsmMessage() != null) {
            logger.debug("event message parsed");
        }
        else {
            logger.debug("unable to parse event data");
            return null;
        }

        Company company = instance.getCompanyByBranch(hpsmEvent.getHpsmMessage().getCompanyBranch());

        if (company == null) {
            logger.debug("unable to map company by branch name : {}", hpsmEvent.getHpsmMessage().getCompanyBranch());
            return null;
        }

        return hpsmEvent.assign(company);
    }


    private HpsmEventHandler createHandler (HpsmEvent request) {
        if (request.getSubject().isNewCaseRequest())
            return new CreateNewCaseHandler();

        return new RejectHandler();
    }


    class CreateNewCaseHandler implements HpsmEventHandler {
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
            obj.setExtAppType("hpsm");

            obj.setCaseType(En_CaseType.CRM_SUPPORT);
            obj.setProduct(product);
            obj.setInitiator(contactPerson);
            obj.setInitiatorCompany(request.getCompany());

            if (HelperFunc.isNotEmpty(request.getHpsmMessage().getContactPersonEmail()))
                obj.setEmails(request.getHpsmMessage().getContactPersonEmail());

            obj.setImpLevel(request.getHpsmMessage().severity().getCaseImpLevel().getId());
            obj.setName(HelperFunc.nvlt(request.getHpsmMessage().getShortDescription(),request.getSubject().getHpsmId()));
            obj.setInfo(request.getHpsmMessage().getDescription());
            obj.setLocal(0);
            obj.setStateId(En_CaseState.CREATED.getId());
            obj.setProduct(product);
            obj.setCreatorInfo("hpsm");
            obj.setExtAppCaseId(request.getHpsmMessage().getHpsmId());

            Long caseObjId = caseObjectDAO.insertCase(obj);
            if (caseObjId != null && caseObjId > 0L) {


                StringBuilder commentText = new StringBuilder();
                if (HelperFunc.isNotEmpty(request.getHpsmMessage().getMessage())) {
                    commentText.append(request.getHpsmMessage().getMessage());
                }

                CaseComment comment = new CaseComment();
                comment.setCreated(new Date());
                comment.setAuthor(contactPerson);
                comment.setCaseId(caseObjId);
                comment.setCaseStateId(obj.getStateId());
                comment.setClientIp("hpsm");
                comment.setText(appendCommentInfo (commentText, request.getHpsmMessage()).toString());

                commentDAO.persist(comment);

                HpsmMessageHeader replySubj = new HpsmMessageHeader(request.getSubject().getHpsmId(), obj.getExtId(), HpsmStatus.REGISTERED);
                HpsmMessage replyEvent = request.getHpsmMessage().createCopy();

                replyEvent.status(HpsmStatus.REGISTERED);
                replyEvent.setOurRegistrationTime(obj.getCreated());
                replyEvent.setOurId(obj.getExtId());

                obj.setExtAppData(xstream.toXML(replyEvent));

                caseObjectDAO.merge(obj);

                instance.sendReply(request, replySubj, replyEvent);
            }
            else {
                instance.sendReject(request, "system error");
            }
        }
    }



    class RejectHandler implements HpsmEventHandler {

        private String messageText;

        public RejectHandler () {

        }

        public RejectHandler(String message) {
            this.messageText = message;
        }

        @Override
        public void handle(HpsmEvent request, ServiceInstance instance) throws Exception {
            instance.sendReject(request, messageText);
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
}
