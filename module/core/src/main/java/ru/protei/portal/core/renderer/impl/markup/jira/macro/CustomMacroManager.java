package ru.protei.portal.core.renderer.impl.markup.jira.macro;

import com.atlassian.renderer.v2.V2SubRenderer;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroManager;
import com.atlassian.renderer.v2.macro.basic.*;
import com.atlassian.renderer.v2.macro.code.CodeMacro;
import com.atlassian.renderer.v2.macro.code.formatter.*;

import java.util.*;

/**
 * @see com.atlassian.renderer.v2.macro.DefaultMacroManager
 */
@SuppressWarnings("unchecked")
public class CustomMacroManager implements MacroManager {
    private HashMap macros = new HashMap();

    public CustomMacroManager(V2SubRenderer subRenderer) {
        this.macros.put("anchor", new BasicAnchorMacro());
        this.macros.put("code", new CodeMacro(subRenderer, Collections.singletonList(new MacroNoneFormatter())));
        this.macros.put("quote", new QuoteMacro());
        this.macros.put("noformat", new NoformatMacro(subRenderer));
        this.macros.put("panel", new PanelMacro(subRenderer));
        this.macros.put("color", new ColorMacro());
        this.macros.put("loremipsum", new LoremIpsumMacro());
        this.macros.put("html", new InlineHtmlMacro());
    }

    public void registerMacro(String name, Macro macro) {
        this.macros.put(name, macro);
    }

    private List getCodeFormatters() {
        ArrayList codeFormatters = new ArrayList();
        codeFormatters.add(new SqlFormatter());
        codeFormatters.add(new JavaFormatter());
        codeFormatters.add(new JavaScriptFormatter());
        codeFormatters.add(new ActionScriptFormatter());
        codeFormatters.add(new XmlFormatter());
        codeFormatters.add(new NoneFormatter());
        return codeFormatters;
    }

    public Macro getEnabledMacro(String name) {
        return (Macro)this.macros.get(name);
    }

    public void unregisterMacro(String name) {
        this.macros.remove(name);
    }

    // Portal-jira-changed: added method to get all tags
    public Set getMacroTags() {
        return macros.keySet();
    }
}
