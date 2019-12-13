package ru.protei.portal.ui.equipment.server;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.service.DocumentService;
import ru.protei.portal.core.service.EquipmentService;
import ru.protei.portal.ui.common.client.service.EquipmentController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Реализация сервиса по работе с оборудованием
 */
@Service( "EquipmentController" )
public class EquipmentControllerImpl implements EquipmentController {

    @Override
    public SearchResult<Equipment> getEquipments(EquipmentQuery query ) throws RequestFailedException {

        log.info( "get equipments: name={} | types={} | organizationCodes={} | classifierCode={} | regNum={}",
                query.getSearchString(), query.getTypes(), query.getOrganizationCodes(), query.getClassifierCode(),
                query.getRegisterNumber() );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(equipmentService.getEquipments(token, query));
    }

    @Override
    public List< EquipmentShortView > equipmentOptionList( EquipmentQuery query ) throws RequestFailedException {
        log.info( "get equipments: name={} | types={} | organizationCodes={} | classifierCode={} | regNum={}",
                query.getSearchString(), query.getTypes(), query.getOrganizationCodes(), query.getClassifierCode(),
                query.getRegisterNumber() );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result<List<EquipmentShortView >> response = equipmentService.shortViewList( descriptor.makeAuthToken(), query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public Equipment getEquipment(long id) throws RequestFailedException {
        log.info("get equipment, id: {}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result<Equipment> response = equipmentService.getEquipment( descriptor.makeAuthToken(), id );
        log.info("get equipment, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

        if (response.isOk()) {
            log.info("get equipment, applied data: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Equipment saveEquipment(Equipment eq) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        if (eq == null) {
            log.warn("null equipment in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        if ( eq.getId() == null ) {
            UserSessionDescriptor session = sessionService.getUserSessionDescriptor( httpRequest );
            eq.setAuthorId( session.getPerson() == null ? 0 : session.getPerson().getId() );
        }
        log.info("store equipment, id: {} ", HelperFunc.nvl(eq.getId(), "new"));

        Result<Equipment> response = equipmentService.saveEquipment( descriptor.makeAuthToken(), eq );
        log.info("store equipment, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.info("store equipment, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Long copyEquipment( Long equipmentId, String newName ) throws RequestFailedException {
        log.info( "copy equipment: id: {}, newName = {}", equipmentId, newName );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        UserSessionDescriptor session = sessionService.getUserSessionDescriptor( httpRequest );
        Long authorId = session.getPerson() == null ? 0 : session.getPerson().getId();

        Result<Long> response = equipmentService.copyEquipment( session.makeAuthToken(), equipmentId, newName, authorId );
        log.info( "copy equipment: result: {}", response.isOk() ? "ok" : response.getStatus() );

        if (response.isOk()) {
            log.info("copy equipment, applied id: {}", response.getData());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public boolean removeEquipment( Long equipmentId ) throws RequestFailedException {
        log.info( "remove equipment: id={}", equipmentId );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result<Boolean> response = equipmentService.removeEquipment( descriptor.makeAuthToken(), equipmentId, descriptor.getPerson() );
        log.info( "remove equipment: result: {}", response.isOk() ? "ok" : response.getStatus() );

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public List<DecimalNumber> getDecimalNumbersOfEquipment(long equipmentId) throws RequestFailedException {

        log.info("get decimal numbers of equipment, id: {}", equipmentId);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result<List<DecimalNumber>> response = equipmentService.getDecimalNumbersOfEquipment(descriptor.makeAuthToken(), equipmentId);

        log.info("get decimal numbers of equipment, id: {} -> {} ", equipmentId, response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public boolean checkIfExistDecimalNumber( DecimalNumber number ) throws RequestFailedException {
        if (number == null) {
            log.warn("null number in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }
        log.info( "check exist decimal number: organizationCode={}, classifierCode={}, regNum={}, modification={}",
                number.getOrganizationCode(), number.getClassifierCode(), number.getRegisterNumber(), number.getModification() );

        Result<Boolean> response = equipmentService.checkIfExistDecimalNumber( number );
        if (response.isOk()) {
            log.info("check exist decimal number, result: {}", response.getData());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public DecimalNumber findDecimalNumber(DecimalNumber decimalNumber) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.info("find decimal number: decimal number={}", decimalNumber);
        Result<DecimalNumber> response = equipmentService.findDecimalNumber(descriptor.makeAuthToken(), decimalNumber);
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Integer getNextAvailableRegisterNumber( DecimalNumberQuery filter ) throws RequestFailedException {
        if (filter.getExcludeNumbers() == null) {
            log.warn("null numbers in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        Result<Integer> response = equipmentService.getNextAvailableDecimalNumber( getDescriptorAndCheckSession().makeAuthToken(), filter );
        if (response.isOk()) {
            log.info("get next available decimal number, result: {}", response.getData());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Integer getNextAvailableRegisterNumberModification( DecimalNumberQuery filter ) throws RequestFailedException  {
        if (filter.getExcludeNumbers() == null) {
            log.warn("null mods in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        Result<Integer> response = equipmentService.getNextAvailableDecimalNumberModification( getDescriptorAndCheckSession().makeAuthToken(), filter );
        if (response.isOk()) {
            log.info("get next available decimal number, result: {}", response.getData());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public SearchResult<Document> getDocuments(Long equipmentId) throws RequestFailedException {

        log.info("getDocuments: equipmentId={}", equipmentId);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(documentService.getDocuments(token, equipmentId));
    }

    @Override
    public Document getDocument(Long id) throws RequestFailedException {

        log.info("getDocument: id={}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result<Document> response = documentService.getDocument(descriptor.makeAuthToken(), id);
        log.info("getDocument: id={} -> {} ", id, response.isError() ? "error" : response.getData());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Document saveDocument(Document document) throws RequestFailedException {

        if (document == null) {
            log.warn("saveDocument | null document in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        String id4log = document.getId() == null ? "new" : String.valueOf(document.getId());

        log.info("saveDocument: id={}", id4log);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        Result<Document> response;

        FileItem pdfFile = sessionService.getFilePdf(httpRequest);
        FileItem docFile = sessionService.getFileDoc(httpRequest);
        sessionService.setFilePdf(httpRequest, null);
        sessionService.setFileDoc(httpRequest, null);

        if (document.getId() == null) {
            response = documentService.createDocument(descriptor.makeAuthToken(), document, docFile, pdfFile, descriptor.getPerson());
        } else {
            response = documentService.updateDocument(descriptor.makeAuthToken(), document, docFile, pdfFile, descriptor.getPerson());
        }

        log.info("saveDocument: id={} | result: {}", id4log, response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpRequest );
        log.info( "userSessionDescriptor={}", descriptor );
        if ( descriptor == null ) {
            throw new RequestFailedException( En_ResultStatus.SESSION_NOT_FOUND );
        }

        return descriptor;
    }

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    DocumentService documentService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    private static final Logger log = LoggerFactory.getLogger(EquipmentControllerImpl.class);

}
