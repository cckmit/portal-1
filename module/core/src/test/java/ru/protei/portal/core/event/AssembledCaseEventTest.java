package ru.protei.portal.core.event;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class AssembledCaseEventTest {


    @Test
    public void attachCommentEvent_old_attachments() {
        List<Attachment> old = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, null, null ) );
        assembled.setExistingAttachments( old );

        Assert.assertTrue( "Expected existing attachments", !isEmpty( assembled.getExistingAttachments() ) );
        Assert.assertTrue( isEmpty( assembled.getAddedAttachments() ) );
        Assert.assertTrue( isEmpty( assembled.getRemovedAttachments() ) );
    }

    @Test
    public void attachCommentEvent_add_attachments() {
        List<Attachment> add = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, add, null ) );

        Assert.assertTrue( isEmpty( assembled.getExistingAttachments() ) );
        Assert.assertTrue( "Expected added attachments", !isEmpty( assembled.getAddedAttachments() ) );
        Assert.assertTrue( isEmpty( assembled.getRemovedAttachments() ) );
    }

    @Test
    public void attachCommentEvent_del_attachments() {
        List<Attachment> del = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, null, del ) );

        Assert.assertTrue( isEmpty( assembled.getExistingAttachments() ) );
        Assert.assertTrue( isEmpty( assembled.getAddedAttachments() ) );
        Assert.assertTrue( "Expected removed attachments", !isEmpty( assembled.getRemovedAttachments() ) );
    }


    @Test
    public void attachCommentEvent_old_del_attachments() {
        List<Attachment> old = listOf( makeAttachment(), makeAttachment() );
        List<Attachment> del = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, null, del ) );
        assembled.setExistingAttachments( old );

        Assert.assertTrue( "Expected existing attachments", !isEmpty( assembled.getExistingAttachments() ) );
        Assert.assertTrue( isEmpty( assembled.getAddedAttachments() ) );
        Assert.assertTrue( "Expected removed attachments", !isEmpty( assembled.getRemovedAttachments() ) );
    }

    @Test
    public void attachCommentEvent_old_add_attachments() {
        List<Attachment> old = listOf( makeAttachment(), makeAttachment() );
        List<Attachment> add = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, add, null ) );
        assembled.setExistingAttachments( old );

        Assert.assertTrue( "Expected existing attachments", !isEmpty( assembled.getExistingAttachments() ) );
        Assert.assertTrue( "Expected added attachments", !isEmpty( assembled.getAddedAttachments() ) );
        Assert.assertTrue( isEmpty( assembled.getRemovedAttachments() ) );
    }

    @Test
    public void attachCommentEvent_old_add_del_attachments() {
        List<Attachment> old = listOf( makeAttachment(), makeAttachment() );
        List<Attachment> add = listOf( makeAttachment(), makeAttachment() );
        List<Attachment> del = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, add, del ) );
        assembled.setExistingAttachments( old );

        Assert.assertTrue( "Expected existing attachments", !isEmpty( assembled.getExistingAttachments() ) );
        Assert.assertTrue( "Expected added attachments", !isEmpty( assembled.getAddedAttachments() ) );
        Assert.assertTrue( "Expected removed attachments", !isEmpty( assembled.getRemovedAttachments() ) );
    }


    @Test
    public void attachCommentEvent_old_add_del_byAttaching() {
        List<Attachment> old = listOf( makeAttachment(), makeAttachment() );
        List<Attachment> add = listOf( makeAttachment(), makeAttachment() );
        List<Attachment> del = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, null, null ) );
        assembled.setExistingAttachments( old );
        assembled.attachAttachmentEvent( new CaseAttachmentEvent( this, null, null, null, add, null ) );
        assembled.attachAttachmentEvent( new CaseAttachmentEvent( this, null, null, null, null, del ) );

        Assert.assertTrue( "Expected existing attachments", !isEmpty( assembled.getExistingAttachments() ) );
        Assert.assertTrue( "Expected added attachments", !isEmpty( assembled.getAddedAttachments() ) );
        Assert.assertTrue( "Expected removed attachments", !isEmpty( assembled.getRemovedAttachments() ) );
    }

    @Test
    public void attachCommentEvent_MergeEvents() {
        Attachment oldDel = makeAttachment( "oldDel" );
        Attachment oldExists = makeAttachment( "oldExists" );
        List<Attachment> old = listOf( oldDel, oldExists );

        Attachment addDel = makeAttachment( "addDel" );
        Attachment addAdded = makeAttachment( "addAdded" );
        Attachment addAdded2 = makeAttachment( "addAdded2" );
        List<Attachment> add = listOf( addAdded, addDel );

        List<Attachment> oldAndAdded = new ArrayList<>( old );
        oldAndAdded.add( addAdded2 ); // Добаылен в параллельном потоке -> выглядит как уже существующий

        List<Attachment> del = listOf( oldDel, addDel );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, null, null ) );
        assembled.attachAttachmentEvent( new CaseAttachmentEvent( this, null, null, null, add, null ) );
        assembled.attachAttachmentEvent( new CaseAttachmentEvent( this, null, null, null, null, del ) );//TODO
        assembled.attachAttachmentEvent( new CaseAttachmentEvent( this, null, null, null, listOf( addAdded2 ), null ) );
        assembled.setExistingAttachments( oldAndAdded );

        Assert.assertEquals( "Expected only intacted attachments. If Add and then Remove = not old.",
                listOf( oldExists ), listOf( assembled.getExistingAttachments() ) );
        Assert.assertEquals( "Expected only added attachments. If Add and then Remove = not added.",
                listOf( addAdded, addAdded2 ), assembled.getAddedAttachments() );
        Assert.assertEquals( "Expected only removed attachments. If Add and then Remove = not removed.",
                listOf( oldDel ), assembled.getRemovedAttachments() );
    }


    @Test
    public void attachCommentEvent_CaseCommentEvent_Preserv_existing_attachments() {
        List<Attachment> old = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, null, null ) );
        assembled.setExistingAttachments( old );
        //  CaseCommentEvent не должен затирать существующие вложения
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, null, null, null ) );

        Assert.assertTrue( "Expected existing attachments. CaseCommentEvent erase existing attachments", !isEmpty( assembled.getExistingAttachments() ) );
        Assert.assertTrue( isEmpty( assembled.getAddedAttachments() ) );
        Assert.assertTrue( isEmpty( assembled.getRemovedAttachments() ) );
    }

    @Test
    public void attachCommentEvent_Merge_CaseCommentEvent() {
        List<Attachment> old = listOf( makeAttachment(), makeAttachment() );
        List<Attachment> add = listOf( makeAttachment(), makeAttachment() );
        List<Attachment> del = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, add, del ) );
        assembled.setExistingAttachments( old );
        //  CaseCommentEvent не должен затирать
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false,  null, null, null ) );

        Assert.assertTrue( "Expected existing attachments", !isEmpty( assembled.getExistingAttachments() ) );
        Assert.assertTrue( "Expected added attachments", !isEmpty( assembled.getAddedAttachments() ) );
        Assert.assertTrue( "Expected removed attachments", !isEmpty( assembled.getRemovedAttachments() ) );
    }


    private AssembledCaseEvent makeAssembledEvent( CaseAttachmentEvent caseAttachmentEvent ) {
        AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent( caseAttachmentEvent );
        assembledCaseEvent.attachAttachmentEvent( caseAttachmentEvent );
        return assembledCaseEvent;
    }


    private Attachment makeAttachment() {
        long id = attachmetnIdgenerator.incrementAndGet();
        return makeAttachment( id, "Test-" + id );
    }

    private Attachment makeAttachment( String name ) {
        long id = attachmetnIdgenerator.incrementAndGet();
        return makeAttachment( id, name );
    }

    private Attachment makeAttachment( Long id, String name ) {
        Attachment attachment = new Attachment();
        attachment.setId( id );
        attachment.setFileName( name );
        return attachment;
    }

    AtomicLong attachmetnIdgenerator = new AtomicLong( 0L );
}