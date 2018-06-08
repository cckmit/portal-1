package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.struct.ReportContent;

import java.util.List;

public interface ReportStorageService {

    /**
     * Сохранить файл в хранилище
     *
     * @param reportContent идентификатор отчета и его содержание
     */
    CoreResponse saveContent(ReportContent reportContent);

    /**
     * Получить файл из хранилища
     *
     * @param reportId идентификатор отчета
     * @return идентификатор отчета и его содержание
     */
    CoreResponse<ReportContent> getContent(Long reportId);

    /**
     * Удалить файл из хранилища
     *
     * @param reportId идентификатор отчета
     */
    CoreResponse removeContent(Long reportId);

    /**
     * Удалить файлы из хранилища
     *
     * @param reportIds идентификаторы отчетов
     */
    CoreResponse removeContent(List<Long> reportIds);

    /**
     * Получить имя файла по идентификатору отчета
     *
     * @param reportId идентификатор отчета
     * @return имя файла
     */
    CoreResponse<String> getFileName(String reportId);
}
