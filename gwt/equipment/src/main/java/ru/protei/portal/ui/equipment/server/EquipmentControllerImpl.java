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
import ru.protei.portal.core.service.session.SessionService;
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

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        Result<List<EquipmentShortView >> response = equipmentService.shortViewList( token, query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public Equipment getEquipment(long id) throws RequestFailedException {
        log.info("get equipment, id: {}", id);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        Result<Equipment> response = equipmentService.getEquipment( token, id );
        log.info("get equipment, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

        if (response.isOk()) {
            log.info("get equipment, applied data: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Equipment saveEquipment(Equipment eq) throws RequestFailedException {

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        if (eq == null) {
            log.warn("null equipment in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        if ( eq.getId() == null ) {
            eq.setAuthorId( token.getPersonId() );
        }
        log.info("store equipment, id: {} ", HelperFunc.nvl(eq.getId(), "new"));

        Result<Equipment> response = equipmentService.saveEquipment( token, eq );
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

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        Long authorId = token.getPersonId();

        Result<Long> response = equipmentService.copyEquipment( token, equipmentId, newName, authorId );
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

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        Result<Boolean> response = equipmentService.removeEquipment( token, equipmentId, token.getPersonDisplayShortName() );

        log.info( "remove equipment: result: {}", response.isOk() ? "ok" : response.getStatus() );

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public List<DecimalNumber> getDecimalNumbersOfEquipment(long equipmentId) throws RequestFailedException {

        log.info("get decimal numbers of equipment, id: {}", equipmentId);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        Result<List<DecimalNumber>> response = equipmentService.getDecimalNumbersOfEquipment(token, equipmentId);

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
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        log.info("find decimal number: decimal number={}", decimalNumber);
        Result<DecimalNumber> response = equipmentService.findDecimalNumber(token, decimalNumber);
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

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        Result<Integer> response = equipmentService.getNextAvailableDecimalNumber( token, filter );
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

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        Result<Integer> response = equipmentService.getNextAvailableDecimalNumberModification( token, filter );
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

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        Result<Document> response = documentService.getDocument(token, id);
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

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        Result<Document> response;

        FileItem pdfFile = sessionService.getFilePdf(httpRequest);
        FileItem docFile = sessionService.getFileDoc(httpRequest);
        FileItem approvalSheetFile = sessionService.getFileApprovalSheet(httpRequest);
        sessionService.setFilePdf(httpRequest, null);
        sessionService.setFileDoc(httpRequest, null);
        sessionService.setFileApprovalSheet(httpRequest, null);


        if (document.getId() == null) {
            response = documentService.createDocument(token, document, docFile, pdfFile, approvalSheetFile, token.getPersonDisplayShortName());
        } else {
            response = documentService.updateDocument(token, document, docFile, pdfFile, approvalSheetFile, token.getPersonDisplayShortName());
        }

        log.info("saveDocument: id={} | result: {}", id4log, response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
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
