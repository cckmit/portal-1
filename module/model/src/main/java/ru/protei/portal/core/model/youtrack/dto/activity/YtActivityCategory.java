package ru.protei.portal.core.model.youtrack.dto.activity;

import ru.protei.portal.core.model.helper.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-ActivityCategory.html */
public enum YtActivityCategory {
    AttachmentRenameCategory,
    AttachmentVisibilityCategory,
    AttachmentsCategory,
    CommentAttachmentsCategory,
    CommentTextCategory,
    CommentUsesMarkdownCategory,
    CommentVisibilityCategory,
    CommentsCategory,
    CustomFieldCategory,
    DescriptionCategory,
    IssueCreatedCategory,
    IssueResolvedCategory,
    IssueUsesMarkdownCategory,
    IssueVisibilityCategory,
    LinksCategory,
    ProjectCategory,
    SprintCategory,
    SummaryCategory,
    TagsCategory,
    TotalVotesCategory,
    VcsChangeCategory,
    VcsChangeStateCategory,
    VotersCategory,
    WorkItemAuthorCategory,
    WorkItemCategory,
    WorkItemDateCategory,
    WorkItemDescriptionCategory,
    WorkItemDurationCategory,
    WorkItemTypeCategory,
    WorkItemUsesMarkdownCategory,
    ;

    public String getCategoryId() {
        return name();
    }

    public static List<String> getAllCategoryIds() {
        return CollectionUtils.stream(Arrays.asList(YtActivityCategory.values()))
                .map(YtActivityCategory::getCategoryId)
                .collect(Collectors.toList());
    }
}
