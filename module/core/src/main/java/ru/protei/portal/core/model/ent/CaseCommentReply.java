package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

/**
 * Created by michael on 24.05.16.
 */
@JdbcEntity(table = "case_comment_reply")
public class CaseCommentReply {

    @JdbcId(name = "id",idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "case_id")
    private Long caseId;

    @JdbcColumn(name = "comment_id")
    private Long commentId;

    @JdbcColumn(name = "reply_id")
    private Long replyId;


    public CaseCommentReply() {
    }
}
