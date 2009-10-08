// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   WFAkukPlugin.java
package de.cismet.cids.navigator.plugins;

import Sirius.navigator.plugin.context.PluginContext;
import Sirius.navigator.plugin.interfaces.*;
import Sirius.navigator.types.treenode.DefaultMetaTreeNode;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.server.middleware.types.MetaObject;
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

public class WFAkukPlugin
        implements PluginSupport {

    private class ShowInAkuk
            implements PluginMethod {

        public void invoke()
                throws Exception {
            log.fatal("Show in AKUK");
            Collection selectedNodes = pluginContext.getMetadata().getSelectedNodes();
            Iterator it = selectedNodes.iterator();
            Vector mos = new Vector();
            try {
                while (it.hasNext()) {
                    log.debug("selected node");
                    DefaultMetaTreeNode node = (DefaultMetaTreeNode) it.next();
                    if (node instanceof ObjectTreeNode) {
                        MetaObject mo = ((ObjectTreeNode) node).getMetaObject();
                        if (mo.getClassKey().equalsIgnoreCase(akukClassKey)) {
                            log.debug("Metaobject hinzugefuegt");
                            mos.add(mo);
                        } else {
                            log.debug((new StringBuilder()).append("falscher ClassKey:").append(mo.getClassKey()).append("=?=").append(akukClassKey).toString());
                        }
                    } else {
                        log.warn((new StringBuilder()).append("Node ist kein ObjectTreeNode, sondern:").append(node.getClass()).toString());
                    }
                }
                if (mos.size() > 0) {
                    String outString = "";
                    for (Iterator i$ = mos.iterator(); i$.hasNext();) {
                        MetaObject mo = (MetaObject) i$.next();
                        outString = (new StringBuilder()).append(outString).append(mo.getID()).append("\r\n").toString();
                    }

                    log.debug((new StringBuilder()).append("outstring").append(outString).toString());
                    try {
                        (new File((new StringBuilder()).append(home).append(fs).append(exchangeDirectory).toString())).mkdirs();
                        BufferedWriter out = new BufferedWriter(new FileWriter((new StringBuilder()).append(home).append(fs).append(exchangeDirectory).append(fs).append(exchangeFile).toString()));
                        out.write(outString);
                        out.close();
                    } catch (IOException e) {
                        log.error("Fehler beim Schreiben des WF-AKUK Exchange Files", e);
                    }
                    try {
                        Runtime rt = Runtime.getRuntime();
                        rt.exec((new StringBuilder()).append(home).append(fs).append(exchangeDirectory).append(fs).append(triggerExe).toString());
                    } catch (Throwable t) {
                        log.error("Fehler beim Aufruf der WF-AKUK triggerExe", t);
                    }
                }
            } catch (Throwable t) {
                log.fatal("Unerwarteter Fehler im WF-AKUK-Plugin", t);
            }
        }

        public String getId() {
            return getClass().getName();
        }
        

        private ShowInAkuk() {
           super();
        }
    }

    public WFAkukPlugin(PluginContext pluginContext) {
        pluginMethods = new HashMap();
        showInAkukMethod = new ShowInAkuk();
        home = "";
        fs = "";
        pluginMethods.put(showInAkukMethod.getId(), showInAkukMethod);
        this.pluginContext = pluginContext;
        akukClassKey = pluginContext.getEnvironment().getParameter("akukClassKey");
        exchangeFile = pluginContext.getEnvironment().getParameter("exchangeFile");
        exchangeDirectory = pluginContext.getEnvironment().getParameter("exchangeDirectory");
        triggerExe = pluginContext.getEnvironment().getParameter("exchangeTriggerExe");
        home = System.getProperty("user.home");
        fs = System.getProperty("file.separator");
    }

    public PluginUI getUI(String id) {
        return null;
    }

    public PluginMethod getMethod(String id) {
        return (PluginMethod) pluginMethods.get(id);
    }

    public void setVisible(boolean flag) {
    }

    public void setActive(boolean flag) {
    }

    public String getId() {
        return "wfakukplugin";
    }

    public Iterator getUIs() {
        LinkedList ll = new LinkedList();
        return ll.iterator();
    }

    public PluginProperties getProperties() {
        return null;
    }

    public Iterator getMethods() {
        return pluginMethods.values().iterator();
    }
    private final Logger log = Logger.getLogger(getClass());
    private HashMap pluginMethods;
    private ShowInAkuk showInAkukMethod;
    private PluginContext pluginContext;
    private String akukClassKey;
    private String exchangeFile;
    private String exchangeDirectory;
    private String triggerExe;
    private String home;
    private String fs;
}
