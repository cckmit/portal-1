package ru.protei.portal.hpsm.logic;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import ru.protei.portal.hpsm.api.MailHandler;
import ru.protei.portal.hpsm.api.MailMessageFactory;
import ru.protei.portal.hpsm.api.MailSendChannel;
import ru.protei.portal.hpsm.service.HpsmService;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;
import ru.protei.portal.hpsm.utils.EventMsgInputStreamSource;
import ru.protei.portal.hpsm.utils.HpsmUtils;

import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * Created by michael on 25.04.17.
 */
public class HpsmMainEventHandler implements MailHandler {

    private static Logger logger = LoggerFactory.getLogger(HpsmMainEventHandler.class);

    @Autowired
    @Qualifier("hpsmSerializer")
    private XStream xstream;

    @Autowired
    @Qualifier("hpsmSendChannel")
    private MailSendChannel sendChannel;

    @Autowired
    @Qualifier("hpsmMessageFactory")
    private MailMessageFactory messageFactory;

    @Autowired
    private HpsmEnvConfig setup;

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private DevUnitDAO devUnitDAO;

    @Autowired
    private CaseCommentDAO commentDAO;

    @Autowired
    private HpsmService hpsmService;


    public MailSendChannel getSendChannel() {
        return sendChannel;
    }

    public void setSendChannel(MailSendChannel sendChannel) {
        this.sendChannel = sendChannel;
    }

    @Override
    public boolean handle(MimeMessage msg) {

        HpsmMessageHeader subject = null;

        try {
            subject = HpsmMessageHeader.parse(msg.getSubject());

            if (subject == null)
                return false;


            HpsmEvent request = buildRequest(subject, msg);

            if (request == null) {
                logger.debug("unable to create request");
                sendChannel.send(createRejectMessage(HpsmUtils.getEmailFromAddress(msg), subject, "wrong request data"));
                return true;
            }

            HpsmEventHandler handler = createHandler(request);
            logger.debug("created handler : {}", handler);

            MimeMessage responseMsg = handler.handle(request);

            if (responseMsg != null) {
                logger.debug("handler return message with subject {}, send it back", responseMsg.getSubject());

                sendChannel.send(responseMsg);
            }
            else {
                logger.debug("Handler return empty message, it's nothing to send back");
            }

        } catch (Throwable e) {
            logger.debug("error on event message handle", e);
        }


        return subject != null;
    }


    private HpsmEvent buildRequest (HpsmMessageHeader subject, MimeMessage msg) throws Exception {

        logger.debug("Got inbound event-message {}", subject.toString());

        HpsmEvent hpsmEvent = HpsmUtils.parseEvent(msg, xstream);

        if (hpsmEvent.getHpsmMessage() != null) {
            logger.debug("event message parsed");
        }
        else {
            logger.debug("unable to parse event data");
            return null;
        }

        Company company = hpsmService.getCompanyByBranchName(hpsmEvent.getHpsmMessage().getCompanyBranch());

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
        public MimeMessage handle(HpsmEvent request) throws Exception {

            Person contactPerson = getAssignedPerson(request.getCompany().getId(), request.getHpsmMessage());
            if (contactPerson == null) {
                return createRejectMessage(request, "No contact person provided");
            }

            DevUnit product = getAssignedProduct(request.getHpsmMessage());
            if (product == null) {
                return createRejectMessage(request, "product not found");
            }

            CaseObject ex_test = caseObjectDAO.getByCondition("ext_app_id=?", request.getSubject().getHpsmId());
            if (ex_test != null) {
                return createRejectMessage(request, "already_registered");
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

                return createReplyMessage (request, replySubj, replyEvent);
            }
            else {
                return createRejectMessage(request, "system error");
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
        public MimeMessage handle(HpsmEvent request) throws Exception {
            return createRejectMessage(request, messageText);
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




    public MimeMessage createReplyMessage (HpsmEvent request, HpsmMessageHeader subject, HpsmMessage hpsmMessage) throws Exception {

        MimeMessage response = messageFactory.createMailMessage();
        MimeMessageHelper helper = new MimeMessageHelper(response, true);

        helper.setSubject(subject.toString());
        helper.setTo(request.getEmailSourceAddr());
        helper.setFrom(setup.getSenderAddress());
        helper.addAttachment(HpsmUtils.RTTS_HPSM_XML, new EventMsgInputStreamSource (xstream).attach(hpsmMessage), "application/xml");

        return response;
    }

    private MimeMessage createRejectMessage (HpsmEvent request, String messageText) throws Exception {
        return createRejectMessage(request.getEmailSourceAddr(), request.getSubject(), messageText);
    }

    private MimeMessage createRejectMessage (String replyTo, HpsmMessageHeader subject, String messageText) throws Exception {
        MimeMessage response = messageFactory.createMailMessage();

        HpsmMessageHeader respSubject = new HpsmMessageHeader(subject.getHpsmId(), subject.getOurId(), HpsmStatus.REJECTED);

        MimeMessageHelper helper = new MimeMessageHelper(response, false);

        helper.setSubject(respSubject.toString());
        helper.setTo(replyTo);
        helper.setFrom(setup.getSenderAddress());

        if (messageText != null)
            helper.setText(messageText);

        return response;
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
