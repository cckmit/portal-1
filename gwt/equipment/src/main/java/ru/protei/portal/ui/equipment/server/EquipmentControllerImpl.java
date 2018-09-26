package ru.protei.portal.ui.equipment.server;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.service.DocumentService;
import ru.protei.portal.core.service.EquipmentService;
import ru.protei.portal.ui.common.client.service.EquipmentController;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Реализация сервиса по работе с оборудованием
 */
@Service( "EquipmentController" )
public class EquipmentControllerImpl implements EquipmentController {

    @Override
    public List<Equipment> getEquipments( EquipmentQuery query ) throws RequestFailedException {

        log.debug( "get equipments: name={} | types={} | organizationCodes={} | classifierCode={} | regNum={}",
                query.getSearchString(), query.getTypes(), query.getOrganizationCodes(), query.getClassifierCode(),
                query.getRegisterNumber() );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<List<Equipment>> response = equipmentService.equipmentList( descriptor.makeAuthToken(), query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public List< EquipmentShortView > equipmentOptionList( EquipmentQuery query ) throws RequestFailedException {
        log.debug( "get equipments: name={} | types={} | organizationCodes={} | classifierCode={} | regNum={}",
                query.getSearchString(), query.getTypes(), query.getOrganizationCodes(), query.getClassifierCode(),
                query.getRegisterNumber() );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<List<EquipmentShortView >> response = equipmentService.shortViewList( descriptor.makeAuthToken(), query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public Equipment getEquipment(long id) throws RequestFailedException {
        log.debug("get equipment, id: {}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Equipment> response = equipmentService.getEquipment( descriptor.makeAuthToken(), id );
        log.debug("get equipment, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

        if (response.isOk()) {
            log.debug("get equipment, applied data: {}", response.getData().getId());
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
        log.debug("store equipment, id: {} ", HelperFunc.nvl(eq.getId(), "new"));

        CoreResponse<Equipment> response = equipmentService.saveEquipment( descriptor.makeAuthToken(), eq );
        log.debug("store equipment, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.debug("store equipment, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Long copyEquipment( Long equipmentId, String newName ) throws RequestFailedException {
        log.debug( "copy equipment: id: {}, newName = {}", equipmentId, newName );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        UserSessionDescriptor session = sessionService.getUserSessionDescriptor( httpRequest );
        Long authorId = session.getPerson() == null ? 0 : session.getPerson().getId();

        CoreResponse<Long> response = equipmentService.copyEquipment( session.makeAuthToken(), equipmentId, newName, authorId );
        log.debug( "copy equipment: result: {}", response.isOk() ? "ok" : response.getStatus() );

        if (response.isOk()) {
            log.debug("copy equipment, applied id: {}", response.getData());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public boolean removeEquipment( Long equipmentId ) throws RequestFailedException {
        log.debug( "remove equipment: id={}", equipmentId );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Boolean> response = equipmentService.removeEquipment( descriptor.makeAuthToken(), equipmentId );
        log.debug( "remove equipment: result: {}", response.isOk() ? "ok" : response.getStatus() );

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Long getEquipmentCount( EquipmentQuery query ) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug( "get equipment count(): query={}", query );
        return equipmentService.count( descriptor.makeAuthToken(), query ).getData();
    }

    @Override
    public List<DecimalNumber> getDecimalNumbersOfEquipment(long equipmentId) throws RequestFailedException {

        log.debug("get decimal numbers of equipment, id: {}", equipmentId);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<List<DecimalNumber>> response = equipmentService.getDecimalNumbersOfEquipment(descriptor.makeAuthToken(), equipmentId);

        log.debug("get decimal numbers of equipment, id: {} -> {} ", equipmentId, response.isOk() ? "ok" : response.getStatus());

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
        log.debug( "check exist decimal number: organizationCode={}, classifierCode={}, regNum={}, modification={}",
                number.getOrganizationCode(), number.getClassifierCode(), number.getRegisterNumber(), number.getModification() );

        CoreResponse<Boolean> response = equipmentService.checkIfExistDecimalNumber( number );
        if (response.isOk()) {
            log.debug("check exist decimal number, result: {}", response.getData());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public DecimalNumber findDecimalNumber(DecimalNumber decimalNumber) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug("find decimal number: decimal number={}", decimalNumber);
        CoreResponse<DecimalNumber> response = equipmentService.findDecimalNumber(descriptor.makeAuthToken(), decimalNumber);
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

        CoreResponse<Integer> response = equipmentService.getNextAvailableDecimalNumber( getDescriptorAndCheckSession().makeAuthToken(), filter );
        if (response.isOk()) {
            log.debug("get next available decimal number, result: {}", response.getData());
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

        CoreResponse<Integer> response = equipmentService.getNextAvailableDecimalNumberModification( getDescriptorAndCheckSession().makeAuthToken(), filter );
        if (response.isOk()) {
            log.debug("get next available decimal number, result: {}", response.getData());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public List<Document> getDocuments(Long equipmentId) throws RequestFailedException {

        log.debug("getDocuments: equipmentId={}", equipmentId);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<List<Document>> response = documentService.documentList(descriptor.makeAuthToken(), equipmentId);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Document getDocument(Long id) throws RequestFailedException {

        log.debug("getDocument: id={}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Document> response = documentService.getDocument(descriptor.makeAuthToken(), id);
        log.debug("getDocument: id={} -> {} ", id, response.isError() ? "error" : response.getData());

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

        String id = HelperFunc.nvlt(String.valueOf(document.getId()), "new");

        log.debug("saveDocument: id={}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Document> response;
        if (document.getId() == null) {
            FileItem fileItem = sessionService.getFileItem(httpRequest);
            if (fileItem == null) {
                log.error("saveDocument: id={} | file item in session was null", id);
                throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
            }
            sessionService.setFileItem(httpRequest, null);
            response = documentService.createDocument(descriptor.makeAuthToken(), document, fileItem);
        } else {
            FileItem fileItem = sessionService.getFileItem(httpRequest);
            if (fileItem == null) {
                response = documentService.updateDocument(descriptor.makeAuthToken(), document);
            } else {
                sessionService.setFileItem(httpRequest, null);
                response = documentService.updateDocumentAndContent(descriptor.makeAuthToken(), document, fileItem);
            }
        }

        log.debug("saveDocument: id={} | result: {}", id, response.isOk() ? "ok" : response.getStatus());

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
