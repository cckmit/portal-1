package ru.protei.portal.core.renderer.impl.markup.jira.macro;

import com.atlassian.renderer.v2.components.MacroTag;
import com.atlassian.renderer.v2.components.WikiContentHandler;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroManager;

import java.util.Set;

/**
 * @see com.atlassian.renderer.v2.WikiMarkupParser
 */
public class WikiMarkupParser {
    private WikiContentHandler wikiContentHandler;
//  Portal-jira-changed: MacroManager -> CustomMacroManager
    private CustomMacroManager macroManager;

//  Portal-jira-changed: MacroManager -> CustomMacroManager
    public WikiMarkupParser(CustomMacroManager macroManager, WikiContentHandler wikiContentHandler) {
        this.wikiContentHandler = wikiContentHandler;
        this.macroManager = macroManager;
    }

    public String parse(String wiki) {
        StringBuffer out = new StringBuffer(wiki.length());
        if (wiki.indexOf("{") == -1) {
            this.wikiContentHandler.handleText(out, wiki);
            return out.toString();
        } else {
            int lastStart = 0;
            boolean inEscape = false;

            for(int i = 0; i < wiki.length(); ++i) {
                char c = wiki.charAt(i);
                if (!inEscape) {
                    switch(c) {
                    case '\\':
                        inEscape = true;
                        break;
                    case '{':
                        if (wiki.length() > i + 1 && "{*?^_-+~".indexOf(wiki.charAt(i + 1)) != -1) {
                            ++i;
                        } else {
                            // Portal-jira-changed: begin - parse only macros that defined by macro manager
                            boolean isMacro = false;
                            if (wiki.length() > i + 1) {
                                Set tags = macroManager.getMacroTags();
                                String wikiTag = wiki.substring(i + 1);
                                for (Object tag : tags) {
                                    if (tag instanceof String && wikiTag.startsWith((String) tag)) {
                                        isMacro = true;
                                        break;
                                    }
                                }
                            }
                            if (!isMacro) {
                                ++i;
                            } else {
                            // Portal-jira-changed: end
                                this.wikiContentHandler.handleText(out, wiki.substring(lastStart, i));
                                lastStart = i + 1;
                                i = this.handlePotentialMacro(wiki, i, out);
                                lastStart = i + 1;
                            }
                        }
                    }
                } else {
                    inEscape = false;
                }
            }

            if (lastStart < wiki.length()) {
                this.wikiContentHandler.handleText(out, wiki.substring(lastStart));
            }

            return out.toString();
        }
    }

    private int handlePotentialMacro(String wiki, int i, StringBuffer out) {
        MacroTag startTag = MacroTag.makeMacroTag(wiki, i);
        if (startTag != null) {
            Macro macro = this.getMacroByName(startTag.command);
            if (macro == null || macro.hasBody()) {
                this.setEndTagIfPresent(wiki, startTag);
            }

            if (startTag.getEndTag() != null) {
                MacroTag endTag = startTag.getEndTag();
                String body = wiki.substring(startTag.endIndex + 1, endTag.startIndex);
                if ("\n".equals(body) && startTag.isNewlineAfter() && endTag.isNewlineBefore()) {
                    endTag.removeNewlineBefore();
                }

                this.makeMacro(out, startTag, body);
                i = endTag.endIndex;
            } else {
                this.makeMacro(out, startTag, "");
                i = startTag.endIndex;
            }
        } else {
            out.append('{');
        }

        return i;
    }

    private void makeMacro(StringBuffer buffer, MacroTag startTag, String body) {
        this.wikiContentHandler.handleMacro(buffer, startTag, body);
    }

    private Macro getMacroByName(String name) {
        return name == null ? null : this.macroManager.getEnabledMacro(name.toLowerCase());
    }

    private void setEndTagIfPresent(String wiki, MacroTag startTag) {
        boolean inEscape = false;

        for(int i = startTag.startIndex + startTag.originalText.length(); i < wiki.length(); ++i) {
            char c = wiki.charAt(i);
            if (inEscape) {
                inEscape = false;
            } else if (c == '{') {
                MacroTag endTag = MacroTag.makeMacroTag(wiki, i);
                if (endTag != null && startTag.command.equals(endTag.command) && endTag.argString.length() == 0) {
                    startTag.setEndTag(endTag);
                    return;
                }
            } else if (c == '\\') {
                inEscape = true;
            }
        }

    }
}
