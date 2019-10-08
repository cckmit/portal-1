package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.struct.ReportContent;

import java.util.List;

public interface ReportStorageService {

    /**
     * Сохранить файл в хранилище
     *
     * @param reportContent идентификатор отчета и его содержание
     */
    Result saveContent( ReportContent reportContent);

    /**
     * Получить файл из хранилища
     *
     * @param reportId идентификатор отчета
     * @return идентификатор отчета и его содержание
     */
    Result<ReportContent> getContent( Long reportId);

    /**
     * Удалить файл из хранилища
     *
     * @param reportId идентификатор отчета
     */
    Result removeContent( Long reportId);

    /**
     * Удалить файлы из хранилища
     *
     * @param reportIds идентификаторы отчетов
     */
    Result removeContent( List<Long> reportIds);

    /**
     * Получить имя файла по идентификатору отчета
     *
     * @param reportId идентификатор отчета
     * @return имя файла
     */
    Result<String> getFileName( String reportId);
}
