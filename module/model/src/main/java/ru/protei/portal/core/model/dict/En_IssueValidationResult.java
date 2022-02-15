package ru.protei.portal.core.model.dict;

public enum En_IssueValidationResult {
    OK,
    /**
     * Case object cannot be null
     */
    NULL,
    /**
     * Name must be specified
     */
    NAME_EMPTY,
    /**
     * Type must be specified
     */
    TYPE_EMPTY,
    /**
     * Required creator id
     */
    CREATOR_EMPTY,
    /**
     * Importance level must be specified
     */
    IMPORTANCE_EMPTY,
    /**
     * Manager company must be specified
     */
    MANAGER_EMPTY,
    /**
     * Manager must belong to company
     */
    MANAGER_OTHER_COMPANY,
    /**
     * Manager must be specified with product
     */
    MANAGER_WITHOUT_PRODUCT,
    /**
     * Initiator company must be specified
     */
    INITIATOR_EMPTY,
    /**
     * State is not valid
     */
    STATUS_INVALID,
    /**
     * Importance level must belong to company
     */
    IMPORTANCE_OTHER_COMPANY,
    /**
     * Initiator must belong to company
     */
    INITIATOR_OTHER_COMPANY,
    /**
     * Platform must belong to initiator company
     */
    PLATFORM_OTHER_COMPANY,
    /**
     * Product is not valid
     */
    PRODUCT_INVALID,
    /**
     * Deadline has passed
     */
    DEADLINE_PASSED,
}
