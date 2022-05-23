package ru.protei.portal.ui.webts.client;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.ui.webts.client.model.TypescriptWebGwtEvents;

import java.util.Arrays;

public class TypescriptWebEntryPoint implements EntryPoint {

    public static void setFactoryReference(Activity activity) {
        TypescriptWebEntryPoint.activity = activity;
    }

    @Override
    public void onModuleLoad() {
        String[] injects = {
                "runtime.bundle.js",
                "vendors.bundle.js",
                "protei-lib.bundle.js",
                "index.bundle.js"
        };
        injectChain(injects);
    }

    private void injectChain(String[] relativePaths) {
        if (relativePaths == null || relativePaths.length == 0) {
            onAllInjected();
            return;
        }
        String currentInject = relativePaths[0];
        String[] nextInjects = Arrays.copyOfRange(relativePaths, 1, relativePaths.length);
        GWT.log("TypescriptWebEntryPoint#onModuleLoad(): will inject '" + currentInject + "'");
        ScriptInjector
                .fromUrl(GWT.getModuleBaseURL() + currentInject)
                .setCallback(injectionChainCallback(currentInject, nextInjects))
                .setWindow(ScriptInjector.TOP_WINDOW).inject();
    }

    private Callback<Void, Exception> injectionChainCallback(final String scriptName, final String[] nextInjections) {
        return new Callback<Void, Exception>() {
            public void onFailure(Exception e) {
                GWT.log("TypescriptWebEntryPoint#onModuleLoad(): injection failed for '" + scriptName + "'");
            }

            public void onSuccess(Void aVoid) {
                GWT.log("TypescriptWebEntryPoint#onModuleLoad(): successfully injected '" + scriptName + "'");
                injectChain(nextInjections);
            }
        };
    }

    private void onAllInjected() {
        TypescriptWebEntryPoint.activity.fireEvent(new TypescriptWebGwtEvents.Init());
    }

    private static Activity activity;
}
