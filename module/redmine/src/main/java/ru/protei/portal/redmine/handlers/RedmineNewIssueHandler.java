package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CaseAttachmentDAO;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.redmine.api.RedmineStatus;

import java.util.Date;

public class RedmineNewIssueHandler implements RedmineEventHandler {
    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    CaseCommentDAO caseCommentDAO;

    @Autowired
    CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    PersonDAO personDAO;

    private final static Logger logger = LoggerFactory.getLogger(RedmineNewIssueHandler.class);

    @Override
    public void handle(Issue issue) {
        CaseObject caseObject = new CaseObject();
        caseObject.setState(RedmineStatus.parse(issue.getStatusName()).getCaseState());
        caseObject.setCreatorId(Long.valueOf(issue.getAuthorId()));
        caseObject.setName(issue.getSubject());

        //Author id
        issue.getAuthorId();

        issue.getSubject();

        //Responsible person
        issue.getAssigneeId();

        //Status
        issue.getStatusName();

        //Date of creation
        issue.getCreatedOn();

        //Comments
        issue.getJournals();

        Person contactPerson = getAssignedPerson(issue);
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

            logger.debug("create eventAssemblyConfig-case id={}, ext={}, data={}", appData.getId(), appData.getExtAppCaseId(), appData.getExtAppData());

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

    private Person getAssignedPerson (Issue issue) {

        Person person = personDAO.get(Long.valueOf(issue.getAssigneeId()));

        if (person != null) {
            logger.debug("contact found: {} (id={})", person.getDisplayName(), person.getId());
        }
        else {
            logger.debug("unable to find contact person : id={}, create new one", issue.getAssigneeId());
            person = new Person();
            person.setCreated(new Date());
            person.setCreator("eventAssemblyConfig");
            if (HelperFunc.isEmpty(issue.getAssigneeName())) {
                person.setFirstName("?");
                person.setLastName("?");
            }
            else {
                String[] np = issue.getAssigneeName().split("\\s+");
                person.setLastName(np[0]);
                person.setFirstName(np.length > 1 ? np[1] : "?");
                person.setSecondName(np.length > 2 ? np[2] : "");
            }

            person.setDisplayName(issue.getAssigneeName());

            person.setGender(En_Gender.UNDEFINED);
            person.setDeleted(false);
            person.setFired(false);

            personDAO.persist(person);
        }

        return person;
    }
}
