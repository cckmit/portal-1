package ru.protei.portal.core.utils;

import ru.protei.portal.core.model.ent.ExternalCaseAppData;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraUtils {
    private static FileTypeMap fileTypeMap;
    private static Pattern pattern = Pattern.compile("((?<![\\p{L}\\p{Nd}\\\\])|(?<=inltokxyzkdtnhgnsbdfinltok))\\!([^\\s\\!]((?!\\!)[\\p{L}\\p{Nd}\\p{Z}\\p{S}\\p{M}\\p{P}]*?[^\\s\\!])?)(?<!\\\\)\\!((?![\\p{L}\\p{Nd}])|(?=inltokxyzkdtnhgnsbdfinltok))");;

    public static URL getResource(String resourceName, Class<?> callingClass) {
        URL url = null;
        url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url == null) {
            url = JiraUtils.class.getClassLoader().getResource(resourceName);
        }

        if (url == null) {
            url = callingClass.getClassLoader().getResource(resourceName);
        }

        return url;
    }

    public static InputStream getResourceAsStream(String resourceName, Class<?> callingClass) {
        URL url = getResource(resourceName, callingClass);

        try {
            return url != null ? url.openStream() : null;
        } catch (IOException var4) {
            return null;
        }
    }

    static {
        InputStream mimeTypesStream = getResourceAsStream("mime.types", JiraUtils.class);
        fileTypeMap = new MimetypesFileTypeMap(mimeTypesStream);
    }

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

    public static Pattern getJiraImagePattern() {
        return pattern;
    }

    public static class ImageNode {
        public String filename;
        public String alt;
    }

    static public ImageNode parseImageNode(String originalString) {
        ImageNode node = new ImageNode();
        // copypasta EmbeddedResourceParser.class
        if (originalString.length() >= 5 && originalString.charAt(0) != ')') {
            int index = originalString.indexOf(124);
            // find resourceString, propertiesString
            String resourceString;
            String propertiesString;
            if (index == -1) {
                resourceString = originalString;
                propertiesString = "";
            } else {
                resourceString = originalString.substring(0, index);
                propertiesString = originalString.substring(index + 1);
            }

            if (resourceString.indexOf(58) != -1) {
                // find space
                resourceString = resourceString.substring(resourceString.indexOf(58) + 1);
            }

            if (resourceString.indexOf(94) != -1) {
                // find page
                resourceString = resourceString.substring(resourceString.indexOf(94) + 1);
            }

            // find filename
            node.filename = resourceString;

            // check file_ext
            String type = fileTypeMap.getContentType(resourceString);
            if (!type.startsWith("image")) {
                return null;
            }

            // parse params
            StringTokenizer st = new StringTokenizer(propertiesString, ",");
            while(st.hasMoreTokens()) {
                String paramPair = st.nextToken();
                if (paramPair.indexOf(61) > 0) {
                    String paramName = paramPair.substring(0, paramPair.indexOf(61)).trim();
                    String paramValue = paramPair.substring(paramPair.indexOf(61) + 1).trim();
                    if (paramValue.startsWith("\"") && paramValue.endsWith("\"")) {
                        paramValue = paramValue.substring(1, paramValue.length() - 1);
                    }

                    if ("alt".equals(paramName.toLowerCase())) {
                        node.alt = paramValue;
                    }
                }
            }
            return node;
        } else {
            return null;
        }
    }
}
