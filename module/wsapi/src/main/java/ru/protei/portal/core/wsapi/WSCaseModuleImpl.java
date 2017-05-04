package ru.protei.portal.core.wsapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;

import javax.jws.WebService;
import java.util.Date;
import java.util.List;

/**
 * Created by Mike on 01.05.2017.
 */
@WebService(endpointInterface = "ru.protei.portal.core.wsapi.WSCaseModule")
public class WSCaseModuleImpl implements WSCaseModule {


    private static Logger logger = LoggerFactory.getLogger(WSCaseModuleImpl.class);

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    DevUnitDAO devUnitDAO;

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    CaseCommentDAO commentDAO;

    @Autowired
    CompanyCategoryDAO companyCategoryDAO;

    @Override
    public CaseObject getCaseObject(long id) {

        CaseObject obj = caseObjectDAO.get(id);

        if (obj != null) {

            logger.debug("get case object, id={}, found={}, ext-id={}", id, obj != null, obj == null ? "?" : obj.getExtId());

            if (!WSAPIDefs.testCaseVisible(obj)) {
                logger.debug("case object with id={}/guid={} is hidden by app-type rule", obj.getId(), obj.getExtId());
                return null;
            }
        }

        return obj;
    }

    @Override
    public CaseObject getCaseObjectExtId(String extAppId) {
        CaseObject obj = caseObjectDAO.getByExternalAppId(extAppId);

        if (obj != null) {

            logger.debug("get case object by external app id={}, local-id={}", extAppId, obj == null ? "" : obj.getId());

            if (!WSAPIDefs.testCaseVisible(obj)) {
                logger.debug("case object with id={}/guid={} is hidden by app-type rule", obj.getId(), obj.getExtId());
                return null;
            }
        }

        return obj;
    }

    @Override
    public String ping(Date clientTime) {
        logger.debug("client time string: " + HelperFunc.nvl(clientTime, "null"));
        return String.format("your time: %s, server time: %s", HelperFunc.nvl(clientTime, "null"), new Date().toString());
    }

    private CaseObject createDefaultTicket (CaseObject ticket, SupportTicketRequest request) {

        ticket.setCreated(new Date());
        ticket.setModified(new Date());
        ticket.setCaseType(En_CaseType.CRM_SUPPORT);
        ticket.setStateId(En_CaseState.CREATED.getId());
        ticket.setCreatorInfo(WSAPIDefs.WS_API_CREATOR_INFO);
        ticket.setImpLevel(En_ImportanceLevel.BASIC.getId());

        ticket.setExtAppType(WSAPIDefs.WS_API_APP_TYPE);
        ticket.setExtAppCaseId(request.extId);
        ticket.setExtAppData(WSAPIDefs.WS_API_PROVIDER_CODE);

        if (request.contactEmail != null)
            ticket.setEmails(request.contactEmail);

        ticket.setName(request.subject);
        ticket.setInfo(request.description);

        ticket.setLocal(0);

        return ticket;
    }

    @Override
    public CaseObject createSupportTicket(SupportTicketRequest request) {
        if (!HelperFunc.testAllNotEmpty(request.extId, request.companyName, request.personName, request.subject, request.productName)) {
            return null;
        }

        CaseObject object = createDefaultTicket(new CaseObject(), request);

        Company company = ensureCompanyExists(request.companyName);

        Person initiator = ensurePersonExists(company, request.personName);

        object.setInitiatorCompany(company);
        object.setInitiator(initiator);
        object.setCreatorInfo(request.personName);
        object.setCreatorId(initiator.getId());
        object.setProduct(ensureProductExists(request.productName, initiator));

        caseObjectDAO.insertCase(object);

        return object;
    }

    @Override
    public CaseObject saveSupportTicket(SupportTicketRequest request) {
        CaseObject obj = caseObjectDAO.getByExternalAppId(request.getExtId());

        if (obj == null) {
            return createSupportTicket(request);
        }

        if (!WSAPIDefs.testCaseVisible(obj))
            return null;

        obj.setName(request.subject);
        obj.setInfo(request.description);

        caseObjectDAO.merge(obj);

        return obj;
    }

    private DevUnit ensureProductExists (String prodName, Person creator) {
        DevUnit product = devUnitDAO.checkExistsByName(En_DevUnitType.PRODUCT, prodName);

        if (product == null) {
            logger.debug("create new product {}", prodName);

            product = new DevUnit();
            product.setCreated(new Date());
            product.setCreatorId(creator.getId());
            product.setLastUpdate(new Date());
            product.setName(prodName);
            product.setStateId(En_DevUnitState.ACTIVE.getId());
            product.setTypeId(En_DevUnitType.PRODUCT.getId());

            devUnitDAO.persist(product);
        }
        else {
            logger.debug("found product id={}, name={}", product.getId(), prodName);
        }

        return product;
    }

    private Person ensurePersonExists (Company company, String personName) {
        Person person = personDAO.findContactByName(company.getId(), personName);

        if (person == null) {
            logger.debug("create new person {}, company={}", personName, company.getCname());

            person = new Person();
            person.setCompany(company);
            person.setDisplayName(personName);
            person.setDisplayShortName(personName);

            String[] np = personName.split ("\\s+");
            person.setLastName(np[0]);
            person.setFirstName(np.length > 1 ? np[1] : "?");
            person.setSecondName(np.length > 2 ? np[2] : "");

            person.setCreated(new Date ());
            person.setCreator(WSAPIDefs.WS_API_CREATOR_INFO);
            person.setGender(En_Gender.UNDEFINED);

            personDAO.persist(person);
        }
        else {
            logger.debug("found contact person {}, id={}, company={}", person.getDisplayName(), person.getId(), company.getCname());
        }

        return person;
    }

    private Company ensureCompanyExists(String company) {
        Company compObj = companyDAO.getCompanyByName(company);

        if (compObj == null) {
            logger.debug("create new partner-company, name={}", company);

            compObj = new Company();

            CompanyCategory category = companyCategoryDAO.get(2L); // partner

            compObj.setCategory(category);

            compObj.setCname(company);
            compObj.setCreated(new Date());
            compObj.setInfo("");

            companyDAO.persist(compObj);
        }
        else {
            logger.debug("found company id={}, name={}", compObj.getId(), compObj.getCname());
        }
        return compObj;
    }

    @Override
    public CaseComment addComment(long caseId, String comment) {

        CaseComment caseComment = new CaseComment(comment);

        CaseObject object = stateAndComment(caseId, caseComment, null);

        return object != null ? caseComment : null;
    }

    @Override
    public CaseObject closeCase(long caseId, String comment) {
        return stateAndComment(caseId, new CaseComment(comment), En_CaseState.VERIFIED);
    }


    private CaseObject stateAndComment (long caseId, CaseComment caseComment, final En_CaseState state) {
        CaseObject object = caseObjectDAO.get(caseId);

        if (object == null) {
            logger.debug("case object with id={} not found", caseId);
            return null;
        }

        if (!WSAPIDefs.testCaseVisible(object)) {
            logger.debug("case object with id={} is hidden by app-type rule", caseId);
            return null;
        }

        if (caseComment == null) {
            caseComment = new CaseComment();
            caseComment.setText("");
        }

        caseComment.setClientIp(WSAPIDefs.WS_API_CREATOR_INFO);
        caseComment.setCreated(new Date());
        caseComment.setAuthor(object.getInitiator());
        caseComment.setCaseId(object.getId());
        caseComment.setCaseStateId(state == null ? object.getStateId() : state.getId());

        commentDAO.persist(caseComment);


        if (state != null) {
            object.setState(state);
            caseObjectDAO.merge(object);
        }

        return object;
    }


    @Override
    public List<CaseComment> getCaseComments(long caseId) {
        CaseObject object = caseObjectDAO.get(caseId);

        if (object == null) {
            logger.debug("case object with id={} not found", caseId);
            return null;
        }

        if (!WSAPIDefs.testCaseVisible(object)) {
            logger.debug("case object with id={} is hidden by app-type rule", caseId);
            return null;
        }

        return commentDAO.getCaseComments(caseId);
    }
}
