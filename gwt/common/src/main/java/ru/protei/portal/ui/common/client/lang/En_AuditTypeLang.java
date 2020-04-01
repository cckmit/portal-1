package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_AuthType;

/**
 * Тип аудита
 */
public class En_AuditTypeLang {
    public String getName( En_AuditType value ) {
        switch (value) {
            case ISSUE_MODIFY: return lang.auditTypeissueModify(); //Изменение обращения
            case ISSUE_CREATE: return lang.auditTypeIssueCreate(); //Создание обращения
            case ISSUE_REPORT: return lang.auditTypeIssueReport(); //Создание отчета по обращениям
            case ISSUE_EXPORT: return lang.auditTypeIssueExport(); //Экспорт обращений
            case REGION_MODIFY: return lang.auditTypeRegionModify(); //Изменение региона
            case REGION_REPORT: return lang.auditTypeRegionReport(); //Создание отчета по регионам
            case REGION_EXPORT: return lang.auditTypeRegionExport(); //Экспорт регионов
            case PROJECT_MODIFY: return lang.auditTypeProjectModify(); //Изменение проекта
            case PROJECT_CREATE: return lang.auditTypeProjectCreate(); //Создание проекта
            case COMPANY_MODIFY: return lang.auditTypeCompanyModify(); //Изменение компании
            case COMPANY_CREATE: return lang.auditTypeCompanyCreate(); //Создание компании
            case PRODUCT_MODIFY: return lang.auditTypeProductModify(); //Изменение продукта
            case PRODUCT_CREATE: return lang.auditTypeProductCreate(); //Создание продукта
            case CONTACT_MODIFY: return lang.auditTypeContactModify(); //Изменение контактного лица
            case CONTACT_CREATE: return lang.auditTypeContactCreate(); //Создание контактного лица
            case ACCOUNT_MODIFY: return lang.auditTypeAccountModify(); //Изменение учетной записи
            case ACCOUNT_CREATE: return lang.auditTypeAccountCreate(); //Создание учетной записи
            case ACCOUNT_REMOVE: return lang.auditTypeAccountRemove(); //Удаление учетной записи
            case EQUIPMENT_MODIFY: return lang.auditTypeEquipmentModify(); //Изменение оборудования
            case EQUIPMENT_CREATE: return lang.auditTypeEquipmentCreate(); //Создание оборудования
            case EQUIPMENT_REMOVE: return lang.auditTypeEquipmentRemove(); //Удаление оборудования
            case ROLE_MODIFY: return lang.auditTypeRoleModify(); //Изменение роли
            case ROLE_CREATE: return lang.auditTypeRoleCreate(); //Создание роли
            case ISSUE_COMMENT_CREATE: return lang.auditTypeIssueCommentCreate(); //Создание описания для обращения
            case ISSUE_COMMENT_MODIFY: return lang.auditTypeIssueCommentModify(); //Изменение описания для обращения
            case ISSUE_COMMENT_REMOVE: return lang.auditTypeIssueCommentRemove(); //Удаление описания для обращения
            case ATTACHMENT_REMOVE: return lang.auditTypeAttachmentRemove(); //Удаление вложения
            case EQUIPMENT_COPY: return lang.auditTypeEquipmentCopy(); //Копирование оборудования
            case OFFICIAL_MODIFY: return lang.auditTypeOfficialModify(); //Изменение должностного лица
            case OFFICIAL_CREATE: return lang.auditTypeOfficialCreate(); //Создание должностного лица
            case ROLE_REMOVE: return lang.auditTypeRoleRemove(); //Удаление роли
            case EMPLOYEE_MODIFY: return lang.auditTypeEmployeeModify(); //Изменение сотрудника
            case EMPLOYEE_CREATE: return lang.auditTypeEmployeeCreate(); //Создание сотрудника
            case DEPARTMENT_MODIFY: return lang.auditTypeDepartmentModify(); //Изменение отдела
            case DEPARTMENT_CREATE: return lang.auditTypeDepartmentCreate(); //Создание отдела
            case DEPARTMENT_REMOVE: return lang.auditTypeDepartmentRemove(); //Удаление отдела
            case WORKER_MODIFY: return lang.auditTypeWorkerModify(); //Изменение записи о сотруднике
            case WORKER_CREATE: return lang.auditTypeWorkerCreate(); //Создание записи о сотруднике
            case WORKER_REMOVE: return lang.auditTypeWorkerRemove(); //Удаление записи о сотруднике
            case POSITION_MODIFY: return lang.auditTypePositionModify(); //Изменение должности
            case POSITION_CREATE: return lang.auditTypePositionCreate(); //Создание должности
            case POSITION_REMOVE: return lang.auditTypePositionRemove(); //Удаление должности
            case PHOTO_UPLOAD: return lang.auditTypePhotoUpload(); //Загрузка фотографии сотрудника
            case DOCUMENT_MODIFY: return lang.auditTypeDocumentModify(); //Изменение документа
            case DOCUMENT_REMOVE: return lang.auditTypeDocumentRemove(); //Удаление документа
            case EMPLOYEE_REGISTRATION_CREATE: return lang.auditTypeEmployeeRegistrationCreate(); //Создание анкеты нового сотрудника
            case PROJECT_REMOVE: return lang.auditTypeProjectRemove(); //Удаление проекта
            case EMPLOYEE_REGISTRATION_MODIFY: return lang.auditTypeEmployeeRegistrationModify(); //Изменение анкеты нового сотрудника
            case CONTRACT_MODIFY: return lang.auditTypeContractModify(); //Изменение договора
            case CONTRACT_CREATE: return lang.auditTypeContractCreate(); //Создание договора
            case CONTACT_FIRE: return lang.auditTypeContactFire(); //Увольнение контактного лица
            case CONTACT_DELETE: return lang.auditTypeContactDelete(); //Удаление контактного лица
            case LINK_CREATE: return lang.auditTypeLinkCreate(); //Создание ссылки
            case LINK_REMOVE: return lang.auditTypeLinkRemove(); //Удаление ссылки
            case DOCUMENT_CREATE: return lang.auditTypeDocumentCreate(); //Создание документа
            case DOCUMENT_TYPE_CREATE: return lang.auditTypeDocumentTypeCreate(); //Создание типа документа
            case DOCUMENT_TYPE_REMOVE: return lang.auditTypeDocumentTypeRemove(); //Удаление типа документа
            case PLATFORM_CREATE: return lang.auditTypePlatformCreate(); //Создание площадки
            case PLATFORM_MODIFY: return lang.auditTypePlatformModify(); //Изменение площадки
            case PLATFORM_REMOVE: return lang.auditTypePlatformRemove(); //Удаление площадки
            case SERVER_CREATE: return lang.auditTypeServerCreate(); //Создание сервера
            case SERVER_MODIFY: return lang.auditTypeServerModify(); //Изменение сервера
            case SERVER_REMOVE: return lang.auditTypeServerRemove(); //Удаление сервера
            case APPLICATION_CREATE: return lang.auditTypeApplicationCreate(); //Создание приложения
            case APPLICATION_MODIFY: return lang.auditTypeApplicationModify(); //Изменение приложения
            case APPLICATION_REMOVE: return lang.auditTypeApplicationRemove(); //Удаление приложения

            default:
                return lang.unknownField();
        }
        
        
    }

    @Inject
    Lang lang;
}
