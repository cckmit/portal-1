package ru.protei.portal.core.utils;

import com.atlassian.renderer.embedded.EmbeddedResourceParser;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.protei.portal.core.model.helper.CaseCommentUtils.makeJiraImageString;
import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

public class JiraUtils {
    private static Pattern pattern = Pattern.compile("((?<![\\p{L}\\p{Nd}\\\\])|(?<=inltokxyzkdtnhgnsbdfinltok))\\!([^\\s\\!]((?!\\!)[\\p{L}\\p{Nd}\\p{Z}\\p{S}\\p{M}\\p{P}]*?[^\\s\\!])?)(?<!\\\\)\\!((?![\\p{L}\\p{Nd}])|(?=inltokxyzkdtnhgnsbdfinltok))");;

    public static JiraIssueData convert(ExternalCaseAppData appData) {
        return new JiraIssueData(extractEndpointId(appData), extractIssueKey(appData), extractIssueProjectId(appData));
    }

    public static String extractIssueKey(String ourStoredId) {
        return ourStoredId.substring(ourStoredId.indexOf('_') + 1);
    }

    public static String extractIssueKey(ExternalCaseAppData appData) {
        return extractIssueKey(appData.getExtAppCaseId());
    }

    public static String extractIssueProjectId(ExternalCaseAppData appData) {
        return appData.getExtAppData();
    }

    public static long extractEndpointId(ExternalCaseAppData appData) {
        return Long.parseLong(appData.getExtAppCaseId().substring(0, appData.getExtAppCaseId().indexOf('_')), 10);
    }

    public static class JiraIssueData {

        public long endpointId;
        public String key;
        public String projectId;

        public JiraIssueData(long endpointId, String key, String projectId) {
            this.endpointId = endpointId;
            this.key = key;
            this.projectId = projectId;
        }
    }

    static public class ImageNode {
        public String filename;
        public String alt;
    }

    static private ImageNode parseImageNodeByJira(String originalString) {
        if (originalString.length() >= 5 && originalString.charAt(0) != ')') {
            EmbeddedResourceParser embeddedResourceParser = new EmbeddedResourceParser(originalString);
            ImageNode node = new ImageNode();
            node.filename = embeddedResourceParser.getFilename();
            node.alt = embeddedResourceParser.getProperties().getProperty("alt");

            return node;
        }
        return null;
    }

    static public String getTextWithReplacedImages(String text, Collection<Attachment> attachments,
                                                   BiPredicate<ImageNode, Attachment> attachmentFilter,
                                                   BiFunction<ImageNode, Attachment, String> makeImageString) {
        Matcher matcher = pattern.matcher(text);
        String resultText;
        if (!matcher.find()) {
            resultText = text;
        } else {
            StringBuilder builder = new StringBuilder();
            int mark = 0;

            do {
                builder.append(text, mark, matcher.start());
                mark = matcher.end();
                String originalString = matcher.group(2);
                Optional<String> imageString = Optional.ofNullable(parseImageNodeByJira(originalString))
                        .flatMap(node -> attachments.stream()
                                .filter(attachment -> attachmentFilter.test(node, attachment))
                                .max(Comparator.comparing(ru.protei.portal.core.model.ent.Attachment::getCreated))
                                .map(attachment -> makeImageString.apply(node, attachment)));
                if (imageString.isPresent()) {
                    builder.append(imageString.get());
                } else {
                    builder.append('!').append(originalString).append('!');
                }
            } while (matcher.find());

            resultText = builder.append(text, mark, text.length()).toString();
        }
        return resultText;
    }

    static public void setTextWithReplacedImagesFromJira(CaseComment caseComment, Collection<Attachment> attachments,
                                                         Collection<CaseAttachment> caseLinkAttachments) {
        String textWithReplacedImages = getTextWithReplacedImages(
                caseComment.getText(),
                attachments,
                (node, attachment) -> attachment.getFileName().equals(node.filename),
                (node, attachment) -> {
                    String imageString = makeJiraImageString(attachment.getExtLink(),
                            attachment.getFileName() + (node.alt != null ? ", " + node.alt : ""));

                    List<CaseAttachment> commentAttachments = (caseComment.getCaseAttachments() != null ?
                            caseComment.getCaseAttachments() : new ArrayList<>());

                    Optional<CaseAttachment> caseAttachment = findCaseAttachmentByAttachmentId(caseLinkAttachments, attachment.getId());

                    if (!caseAttachment.isPresent() || caseAttachment.get().getCommentId() != null) {
                        commentAttachments.add(new CaseAttachment(caseComment.getCaseId(), attachment.getId()));
                    } else {
                        commentAttachments.add(caseAttachment.get());
                    }

                    caseComment.setCaseAttachments(commentAttachments);

                    return imageString;
                }
        );
        caseComment.setText(textWithReplacedImages);
    }

    static private Optional<CaseAttachment> findCaseAttachmentByAttachmentId(Collection<CaseAttachment>caseLinkAttachments, Long id) {
        return emptyIfNull(caseLinkAttachments).stream()
                .filter(caseLinkAttachment -> id.equals(caseLinkAttachment.getAttachmentId()))
                .max(Comparator.nullsLast(Comparator.comparing(CaseAttachment::getId)));
    }

    static public String getDescriptionWithReplacedImagesFromJira(String text, Collection<Attachment> attachments) {
        return getTextWithReplacedImages(
                text,
                attachments,
                (node, attachment) -> attachment.getFileName().equals(node.filename),
                (node, attachment) -> makeJiraImageString(attachment.getExtLink(),
                            attachment.getFileName() + (node.alt != null ? ", " + node.alt : ""))
        );
    }

    static public String getTextWithReplacedImagesToJira(String text, Collection<Attachment> attachments) {
        return getTextWithReplacedImages(
                text,
                attachments,
                (node, attachment) -> attachment.getExtLink().equals(node.filename),
                (node, attachment) -> makeJiraImageString(attachment.getFileName(),
                        node.alt != null ? node.alt : attachment.getFileName())
        );
    }

    public static final String PROJECT_CUSTOMER_ROLE = "Project Customer Role";
    public static final String PROJECT_SUPPORT_ROLE = "Project Support Role";
    public static final String PROJECT_TECH_USER_ROLE = "Project TechUser Role";
}
