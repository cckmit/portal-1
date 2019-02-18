package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;

import java.util.Date;
import java.util.List;

/**
 *
 */
public class CommentTimeElapsedQuery  extends BaseQuery {

    public CommentTimeElapsedQuery() {
    }


    public CommentTimeElapsedQuery( CaseQuery caseQuery ) {
        this.caseQuery = caseQuery;
    }

    public Boolean isTimeElapsedNotNull() {
        return timeElapsedNotNull;
    }

    public void setTimeElapsedNotNull(Boolean timeElapsedNotNull) {
        this.timeElapsedNotNull = timeElapsedNotNull;
    }

    public String getSearchString() {
        return caseQuery.getSearchString();
    }

    public void setSearchString( String searchString ) {
        caseQuery.setSearchString( searchString );
    }

    public boolean isParamsPresent() {
        return caseQuery.isParamsPresent();
    }

    public Long getId() {
        return caseQuery.getId();
    }

    public void setId( Long id ) {
        caseQuery.setId( id );
    }

    public List<Long> getCaseNumbers() {
        return caseQuery.getCaseNumbers();
    }

    public void setCaseNo( Long caseNo ) {
        caseQuery.setCaseNo( caseNo );
    }

    public void setCaseNumbers( List<Long> caseNumbers ) {
        caseQuery.setCaseNumbers( caseNumbers );
    }

    public List<Long> getCompanyIds() {
        return caseQuery.getCompanyIds();
    }

    public void setCompanyIds( List<Long> companyIds ) {
        caseQuery.setCompanyIds( companyIds );
    }

    public List<Long> getInitiatorIds() {
        return caseQuery.getInitiatorIds();
    }

    public void setInitiatorIds( List<Long> initiatorIds ) {
        caseQuery.setInitiatorIds( initiatorIds );
    }

    public List<Long> getProductIds() {
        return caseQuery.getProductIds();
    }

    public void setProductIds( List<Long> productIds ) {
        caseQuery.setProductIds( productIds );
    }

    public En_CaseType getType() {
        return caseQuery.getType();
    }

    public void setType( En_CaseType type ) {
        caseQuery.setType( type );
    }

    public List<Integer> getStateIds() {
        return caseQuery.getStateIds();
    }

    public void setStateIds( List<Integer> stateIds ) {
        caseQuery.setStateIds( stateIds );
    }

    public void setStates( List<En_CaseState> states ) {
        caseQuery.setStates( states );
    }

    public List<Integer> getImportanceIds() {
        return caseQuery.getImportanceIds();
    }

    public void setImportanceIds( List<Integer> importanceIds ) {
        caseQuery.setImportanceIds( importanceIds );
    }

    public Date getFrom() {
        return caseQuery.getFrom();
    }

    public void setFrom( Date from ) {
        caseQuery.setFrom( from );
    }

    public Date getTo() {
        return caseQuery.getTo();
    }

    public void setTo( Date to ) {
        caseQuery.setTo( to );
    }

    public List<Long> getManagerIds() {
        return caseQuery.getManagerIds();
    }

    public void setManagerIds( List<Long> managerIds ) {
        caseQuery.setManagerIds( managerIds );
    }

    public boolean isOrWithoutManager() {
        return caseQuery.isOrWithoutManager();
    }

    public void setOrWithoutManager( boolean withoutManager ) {
        caseQuery.setOrWithoutManager( withoutManager );
    }

    public boolean isAllowViewPrivate() {
        return caseQuery.isAllowViewPrivate();
    }

    public void setAllowViewPrivate( boolean isAllowViewPrivate ) {
        caseQuery.setAllowViewPrivate( isAllowViewPrivate );
    }

    public boolean isSearchStringAtComments() {
        return caseQuery.isSearchStringAtComments();
    }

    public void setSearchStringAtComments( boolean searchStringAtComments ) {
        caseQuery.setSearchStringAtComments( searchStringAtComments );
    }

    public String getSearchCasenoString() {
        return caseQuery.getSearchCasenoString();
    }

    public void setSearchCasenoString( String searchCasenoString ) {
        caseQuery.setSearchCasenoString( searchCasenoString );
    }

    public Boolean isViewPrivate() {
        return caseQuery.isViewPrivate();
    }

    public void setViewPrivate( Boolean viewOnlyPrivate ) {
        caseQuery.setViewPrivate( viewOnlyPrivate );
    }

    public List<Long> getMemberIds() {
        return caseQuery.getMemberIds();
    }

    public void setMemberIds( List<Long> memberIds ) {
        caseQuery.setMemberIds( memberIds );
    }

    public List<Long> getCommentAuthorIds() {
        return caseQuery.getCommentAuthorIds();
    }

    public void setCommentAuthorIds( List<Long> commentAuthorIds ) {
        caseQuery.setCommentAuthorIds( commentAuthorIds );
    }

    public CaseQuery getCaseQuery() {
        return caseQuery;
    }


    private Boolean timeElapsedNotNull;
    private CaseQuery caseQuery;
}
