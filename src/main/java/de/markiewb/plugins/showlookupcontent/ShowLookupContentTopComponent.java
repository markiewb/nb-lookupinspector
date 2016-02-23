/**
 * Copyright 2016 markiewb
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.markiewb.plugins.showlookupcontent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

@TopComponent.Description(
        preferredID = "showlookupcontentTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window/Debug", id = "de.markiewb.plugins.showlookupcontent.ShowLookupContentTopComponent")
@ActionReference(path = "Menu/Window/Debug", position = 2000)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_showlookupcontentAction",
        preferredID = "showlookupcontentTopComponent")
@Messages({
    "CTL_showlookupcontentAction=Lookup content inspector",
    "CTL_showlookupcontentTopComponent=Lookup content inspector",
    "HINT_showlookupcontentTopComponent=This is the lookup content inspector. It shows the content of the current lookup."
})
public final class ShowLookupContentTopComponent extends TopComponent implements LookupListener {

    public ShowLookupContentTopComponent() {
        initComponents();
        setName(Bundle.CTL_showlookupcontentTopComponent());
        setToolTipText(Bundle.HINT_showlookupcontentTopComponent());

        lookupResult = Utilities.actionsGlobalContext().lookupResult(Object.class);

    }

    public void formatRecursive(int depth, final Class<? extends Object> aClass, List<String> list) {
        if (null == aClass) {
            return;
        }
        // everything is an Object, so do not print
        if (Object.class.equals(aClass)) {
            return;
        }
        String indentedText = getIndentedText(depth, aClass.toString());

        list.add(indentedText);

        Class<? extends Object> superclass = aClass.getSuperclass();
        if (null != superclass) {
            formatRecursive(depth + 1, superclass, list);
        }
        Class<?>[] interfaces = aClass.getInterfaces();
        if (null != interfaces && interfaces.length > 0) {
            for (Class<?> aInterface : interfaces) {
                formatRecursive(depth + 1, aInterface, list);
            }
        }
    }

    public List<String> getContentFromLookup(Collection<? extends Object> allInstances) {
        txtLookupContent.setText("# of instances: " + allInstances.size() + "\n");
        List<String> list = new ArrayList<String>();
        list.add("");
        list.add("--Classes in lookup--");
        for (Object node : allInstances) {
            final Class<? extends Object> aClass = node.getClass();
            list.add(String.format("%-70s %s", aClass.toString().trim(), node.toString().trim()));
        }
        list.add("");
        list.add("--Classes in lookup incl. their hierarchy--");
        for (Object node : allInstances) {
            final Class<? extends Object> aClass = node.getClass();
            int depth = 0;
            formatRecursive(depth, aClass, list);
        }
        return list;
    }

    public List<String> getDocumentProperties() {
        List<String> list = new ArrayList<String>();
        final JTextComponent comp = EditorRegistry.lastFocusedComponent();
        if (null != comp) {
            Document document = comp.getDocument();
            if (null != document) {

                if (document instanceof AbstractDocument) {
                    AbstractDocument doc = (AbstractDocument) document;
                    list.add(getIndentedText(0, "--Document--"));
                    list.add(getIndentedText(1, "" + document));
                    list.add("");

                    Dictionary<Object, Object> documentProperties = doc.getDocumentProperties();
                    Enumeration<Object> keys = documentProperties.keys();
                    //Convert to sorted map
                    Map<String, String> map = new TreeMap<String, String>();
                    while (keys.hasMoreElements()) {
                        Object key = keys.nextElement();
                        Object value = documentProperties.get(key);
                        if ("interface java.lang.CharSequence".equals("" + key)) {
                            value = "DOCUMENT CONTENT WILL NOT BE DISPLAYED";
                        }

                        map.put("" + key, "" + value);
                    }

                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        list.add(getIndentedText(1, key + "=" + value));
                    }

                }
            }
        }
        return list;
    }

    public String getIndentedText(int depth, final String text) {
        int indent = depth * 8;
        final String indentedText = indent(indent, text);
        return indentedText;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Collection<? extends Object> allInstances = lookupResult.allInstances();
        final TopComponent tc = TopComponent.getRegistry().getActivated();
        txtTopComponent.setText("" + tc + "\n");

        if (tc != this) {
            List<String> list = new ArrayList<String>();
            list.addAll(getContentFromLookup(allInstances));
            list.addAll(getDocumentProperties());
            for (String text : list) {
                txtLookupContent.append(text + "\n");
            }
            txtLookupContent.setCaretPosition(0);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtTopComponent = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtLookupContent = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        txtTopComponent.setColumns(20);
        txtTopComponent.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        txtTopComponent.setRows(5);
        jScrollPane1.setViewportView(txtTopComponent);

        jSplitPane1.setLeftComponent(jScrollPane1);

        txtLookupContent.setColumns(20);
        txtLookupContent.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        txtLookupContent.setRows(5);
        jScrollPane3.setViewportView(txtLookupContent);

        jSplitPane1.setRightComponent(jScrollPane3);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ShowLookupContentTopComponent.class, "ShowLookupContentTopComponent.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextArea txtLookupContent;
    private javax.swing.JTextArea txtTopComponent;
    // End of variables declaration//GEN-END:variables
    Result<Object> lookupResult;

    @Override
    public void componentOpened() {

        lookupResult.addLookupListener(this);
    }

    @Override
    public void componentClosed() {
        lookupResult.removeLookupListener(this);
    }

    private String indent(int indent, String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        sb.append(text);
        return sb.toString();
    }

}
