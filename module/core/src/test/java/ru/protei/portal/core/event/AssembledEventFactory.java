package ru.protei.portal.core.event;

import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class AssembledEventFactory {

    public static AssembledCaseEvent makeAssembledEvent( CaseAttachmentEvent caseAttachmentEvent ) {
        AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent( caseAttachmentEvent );
        assembledCaseEvent.attachAttachmentEvent( caseAttachmentEvent );
        return assembledCaseEvent;
    }

    public static AssembledCaseEvent makeAssembledEvent( CaseCommentEvent event ) {
        AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent( event );
        assembledCaseEvent.attachCommentEvent( event );
        return assembledCaseEvent;
    }

    public static Attachment makeAttachment() {
        long id = attachmetnIdgenerator.incrementAndGet();
        return makeAttachment( id, "Test-" + id );
    }

    public static Attachment makeAttachment( String name ) {
        long id = attachmetnIdgenerator.incrementAndGet();
        return makeAttachment( id, name );
    }

    public static Attachment makeAttachment( Long id, String name ) {
        Attachment attachment = new Attachment();
        attachment.setId( id );
        attachment.setFileName( name );
        return attachment;
    }

    public static CaseComment makeComment() {
        long id = commentIdgenerator.incrementAndGet();
        return makeComment( id, "Test-" + id );
    }

    public static CaseComment makeComment(String text, Date creationDate) {
        long id = commentIdgenerator.incrementAndGet();
        CaseComment caseComment = makeComment( id, text );
        caseComment.setCreated( creationDate );
        return caseComment;
    }

    public static CaseComment makeComment( Long id, String text ) {
        CaseComment comment = new CaseComment();
        comment.setId( id );
        comment.setText( text );
        comment.setCreated( new Date() );
        return comment;
    }

    static AtomicLong attachmetnIdgenerator = new AtomicLong( 0L );
    static AtomicLong commentIdgenerator = new AtomicLong( 0L );
}
