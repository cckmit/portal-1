package ru.protei.portal.core.event;

import org.junit.Test;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.protei.portal.core.event.AssembledEventFactory.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class AssembledCaseEventTest {


    @Test
    public void attachCommentEvent_old_attachments() {
        List<Attachment> old = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, null, null ) );
        assembled.setExistingAttachments( old );

        assertTrue( "Expected existing attachments", !isEmpty( assembled.getExistingAttachments() ) );
        assertTrue( isEmpty( assembled.getAddedAttachments() ) );
        assertTrue( isEmpty( assembled.getRemovedAttachments() ) );
    }

    @Test
    public void attachCommentEvent_add_attachments() {
        List<Attachment> add = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, add, null ) );

        assertTrue( isEmpty( assembled.getExistingAttachments() ) );
        assertTrue( "Expected added attachments", !isEmpty( assembled.getAddedAttachments() ) );
        assertTrue( isEmpty( assembled.getRemovedAttachments() ) );
    }

    @Test
    public void attachCommentEvent_del_attachments() {
        List<Attachment> del = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, null, del ) );

        assertTrue( isEmpty( assembled.getExistingAttachments() ) );
        assertTrue( isEmpty( assembled.getAddedAttachments() ) );
        assertTrue( "Expected removed attachments", !isEmpty( assembled.getRemovedAttachments() ) );
    }


    @Test
    public void attachCommentEvent_old_del_attachments() {
        List<Attachment> old = listOf( makeAttachment(), makeAttachment() );
        List<Attachment> del = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, null, del ) );
        assembled.setExistingAttachments( old );

        assertTrue( "Expected existing attachments", !isEmpty( assembled.getExistingAttachments() ) );
        assertTrue( isEmpty( assembled.getAddedAttachments() ) );
        assertTrue( "Expected removed attachments", !isEmpty( assembled.getRemovedAttachments() ) );
    }

    @Test
    public void attachCommentEvent_old_add_attachments() {
        List<Attachment> old = listOf( makeAttachment(), makeAttachment() );
        List<Attachment> add = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, add, null ) );
        assembled.setExistingAttachments( old );

        assertTrue( "Expected existing attachments", !isEmpty( assembled.getExistingAttachments() ) );
        assertTrue( "Expected added attachments", !isEmpty( assembled.getAddedAttachments() ) );
        assertTrue( isEmpty( assembled.getRemovedAttachments() ) );
    }

    @Test
    public void attachCommentEvent_old_add_del_attachments() {
        List<Attachment> old = listOf( makeAttachment(), makeAttachment() );
        List<Attachment> add = listOf( makeAttachment(), makeAttachment() );
        List<Attachment> del = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, add, del ) );
        assembled.setExistingAttachments( old );

        assertTrue( "Expected existing attachments", !isEmpty( assembled.getExistingAttachments() ) );
        assertTrue( "Expected added attachments", !isEmpty( assembled.getAddedAttachments() ) );
        assertTrue( "Expected removed attachments", !isEmpty( assembled.getRemovedAttachments() ) );
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

        assertTrue( "Expected existing attachments", !isEmpty( assembled.getExistingAttachments() ) );
        assertTrue( "Expected added attachments", !isEmpty( assembled.getAddedAttachments() ) );
        assertTrue( "Expected removed attachments", !isEmpty( assembled.getRemovedAttachments() ) );
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
        assembled.attachAttachmentEvent( new CaseAttachmentEvent( this, null, null, null, null, del ) );
        assembled.attachAttachmentEvent( new CaseAttachmentEvent( this, null, null, null, listOf( addAdded2 ), null ) );
        assembled.setExistingAttachments( oldAndAdded );

        assertEquals( "Expected only intacted attachments. If Add and then Remove = not old.",
                listOf( oldExists ), listOf( assembled.getExistingAttachments() ) );
        assertEquals( "Expected only added attachments. If Add and then Remove = not added.",
                listOf( addAdded, addAdded2 ), assembled.getAddedAttachments() );
        assertEquals( "Expected only removed attachments. If Add and then Remove = not removed.",
                listOf( oldDel ), assembled.getRemovedAttachments() );
    }


    @Test
    public void attachCommentEvent_CaseCommentEvent_Preserv_existing_attachments() {
        List<Attachment> old = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, null, null ) );
        assembled.setExistingAttachments( old );
        //  CaseCommentEvent не должен затирать существующие вложения
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, null, null, null ) );

        assertTrue( "Expected existing attachments. CaseCommentEvent erase existing attachments", !isEmpty( assembled.getExistingAttachments() ) );
        assertTrue( isEmpty( assembled.getAddedAttachments() ) );
        assertTrue( isEmpty( assembled.getRemovedAttachments() ) );
    }

    @Test
    public void attachCommentEvent_Merge_CaseCommentEvent() {
        List<Attachment> old = listOf( makeAttachment(), makeAttachment() );
        List<Attachment> add = listOf( makeAttachment(), makeAttachment() );
        List<Attachment> del = listOf( makeAttachment(), makeAttachment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseAttachmentEvent( this, null, null, null, add, del ) );
        assembled.setExistingAttachments( old );
        //  CaseCommentEvent не должен затирать
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, null, null, null ) );

        assertTrue( "Expected existing attachments", !isEmpty( assembled.getExistingAttachments() ) );
        assertTrue( "Expected added attachments", !isEmpty( assembled.getAddedAttachments() ) );
        assertTrue( "Expected removed attachments", !isEmpty( assembled.getRemovedAttachments() ) );
    }

    @Test
    public void caseCommentEvent_old_comments() {
        List<CaseComment> old = listOf( makeComment(), makeComment() );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseCommentEvent( this, null, null, null, false, null, null, null ) );
        assembled.setExistingCaseComments( old );

        assertTrue( "Expected existing comments", !isEmpty( assembled.getAllComments() ) );
        assertEquals( old, assembled.getAllComments() );
        assertTrue( isEmpty( assembled.getChangedCaseComments() ) );
        assertTrue( isEmpty( assembled.getRemovedCaseComments() ) );
        assertTrue( isEmpty( assembled.getAddedCaseComments() ) );
    }

    @Test
    public void caseCommentEvent_added_comments() {
        List<CaseComment> old = listOf( makeComment(), makeComment() );
        CaseComment add1 = makeComment();
        CaseComment add2 = makeComment();
        List<CaseComment> all = new ArrayList<>( old );
        all.add( add1 );
        all.add( add2 );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseCommentEvent( this, null, null, null, false, null, add1, null ) );
        assembled.setExistingCaseComments( old );
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, null, add2, null ) );


        assertTrue( "Expected existing comments", !isEmpty( assembled.getAllComments() ) );
        assertEquals( "Expected existing all", all, assembled.getAllComments() );
        assertEquals( "Expected existing added", listOf( add1, add2 ), assembled.getAddedCaseComments() );
        assertTrue( isEmpty( assembled.getChangedCaseComments() ) );
        assertTrue( isEmpty( assembled.getRemovedCaseComments() ) );
    }

    @Test
    public void caseCommentEvent_removed_comments() {
        List<CaseComment> old = listOf( makeComment(), makeComment() );
        CaseComment rem1 = makeComment();
        CaseComment rem2 = makeComment();
        List<CaseComment> all = new ArrayList<>( old );
        all.add( rem1 );
        all.add( rem2 );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseCommentEvent( this, null, null, null, false, null, null, rem1 ) );
        assembled.setExistingCaseComments( old );
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, null, null, rem2 ) );


        assertTrue( "Expected existing comments", !isEmpty( assembled.getAllComments() ) );
        assertEquals( "Expected existing old and removed", all, assembled.getAllComments() );
        assertTrue( isEmpty( assembled.getAddedCaseComments() ) );
        assertTrue( isEmpty( assembled.getChangedCaseComments() ) );
        assertEquals( "Expected existing removed", listOf( rem1, rem2 ), assembled.getRemovedCaseComments() );
    }


    @Test
    public void caseCommentEvent_changed_comments() {
        List<CaseComment> old = listOf( makeComment(), makeComment() );
        CaseComment chang1 = makeComment();
        CaseComment chang2 = makeComment();
        CaseComment chang1new = makeComment();
        chang1new.setId( chang1.getId() );
        chang1new.setText( "new chang1" );
        CaseComment chang2new = makeComment();
        chang2new.setId( chang2.getId() );
        chang2new.setText( "new chang2" );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseCommentEvent( this, null, null, null, false, chang1, chang1new, null ) );
        assembled.setExistingCaseComments( old );
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, chang2, chang2new, null ) );

        assertTrue( "Expected existing comments", !isEmpty( assembled.getAllComments() ) );
        assertEquals( "Expected existing only initial comments", old, assembled.getAllComments() );
        assertTrue( isEmpty( assembled.getAddedCaseComments() ) );
        assertEquals( "Expected existing changed", listOf( chang1, chang2 ), assembled.getChangedCaseComments() );
        assertTrue( isEmpty( assembled.getRemovedCaseComments() ) );
    }

    @Test
    public void caseCommentEvent_added_removed_changed_comments() {
        List<CaseComment> old = listOf( makeComment(), makeComment() );
        CaseComment chang1 = makeComment();
        CaseComment chang2 = makeComment();
        CaseComment rem1 = makeComment();
        CaseComment rem2 = makeComment();
        CaseComment add1 = makeComment();
        CaseComment add2 = makeComment();

        CaseComment chang1new = makeComment();
        chang1new.setId( chang1.getId() );
        chang1new.setText( "new chang1" );
        CaseComment chang2new = makeComment();
        chang2new.setId( chang2.getId() );
        chang2new.setText( "new chang2" );

        List<CaseComment> all = new ArrayList<>( old );
        all.add( rem1 );
        all.add( rem2 );
        all.add( add1 );
        all.add( add2 );

        AssembledCaseEvent assembled = makeAssembledEvent( new CaseCommentEvent( this, null, null, null, false, chang1, chang1new, rem1 ) );
        assembled.setExistingCaseComments( old );
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, chang2, chang2new, rem2 ) );
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, null, add1, null ) );
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, null, add2, null ) );

        assertTrue( "Expected existing comments", !isEmpty( assembled.getAllComments() ) );
        assertEquals( "Expected existing old and removed", all, assembled.getAllComments() );
        assertEquals( "Expected existing added", listOf( add1, add2 ), assembled.getAddedCaseComments() );
        assertEquals( "Expected existing changed", listOf( chang1, chang2 ), assembled.getChangedCaseComments() );
        assertEquals( "Expected existing removed", listOf( rem1, rem2 ), assembled.getRemovedCaseComments() );
    }


}
