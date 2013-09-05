package tk.amberide.ide.gui.editor.text;

import tk.amberide.Amber;
import tk.amberide.ide.data.io.FileIO;
import tk.amberide.ide.gui.FileViewerPanel;
import tk.amberide.ide.gui.misc.ErrorHandler;
import tk.amberide.ide.gui.misc.RubyConsole;
import tk.amberide.ide.swing.UIUtil;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import static org.fife.ui.rsyntaxtextarea.SyntaxConstants.*;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jruby.Ruby;

/**
 * @author Tudor
 */
public class ScriptEditorPanel extends FileViewerPanel {

    /**
     * A lookup for extension <-> syntax highlighting style. Queries should be
     * lowercase, and without the '.' prefix. For example, fetching the Ruby
     * syntax highlighter would require a query for "rb".
     */
    public static final HashMap<String, String> SYNTAX_EXTENSIONS = new HashMap<String, String>() {
        {
            put("rb", SYNTAX_STYLE_RUBY);
            put("as", SYNTAX_STYLE_ACTIONSCRIPT);
            put("c", SYNTAX_STYLE_C);
            put("cpp", SYNTAX_STYLE_CPLUSPLUS);
            put("cs", SYNTAX_STYLE_CSHARP);
            put("dtd", SYNTAX_STYLE_DTD);
            put("css", SYNTAX_STYLE_CSS);
            put("java", SYNTAX_STYLE_JAVA);
            put("scala", SYNTAX_STYLE_JAVA);
            put("js", SYNTAX_STYLE_JAVASCRIPT);
            put("lua", SYNTAX_STYLE_SCALA);
            put("py", SYNTAX_STYLE_PYTHON);
            put("jsp", SYNTAX_STYLE_JSP);
            put("bat", SYNTAX_STYLE_WINDOWS_BATCH);
            put("sh", SYNTAX_STYLE_UNIX_SHELL);
            put("xml", SYNTAX_STYLE_XML);
            put("html", SYNTAX_STYLE_HTML);
            put("htm", SYNTAX_STYLE_HTML);
        }
    };

    protected boolean modified = false;

    /**
     * Creates new form ScriptEditorPanel
     *
     * @param file the file to load into the form
     */
    public ScriptEditorPanel(final File file) {
        super(file);
        initComponents();
        editor.setCodeFoldingEnabled(true);
        editor.setAntiAliasingEnabled(true);
        editor.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                // Lines start at 0, so +1
                cursorLabel.setText(editor.getCaretLineNumber() + 1 + " | " + editor.getCaretOffsetFromLineStart());
            }
        });
        scrollPane.setLineNumbersEnabled(true);
        UIUtil.mapInput(this, JComponent.WHEN_IN_FOCUSED_WINDOW, KeyEvent.VK_S, InputEvent.CTRL_MASK, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        String ext = FileIO.getFileExtension(file);
        toolbar.setVisible(ext.equals("rb"));
        editor.setSyntaxEditingStyle(SYNTAX_EXTENSIONS.containsKey(ext) ? SYNTAX_EXTENSIONS.get(ext) : SyntaxConstants.SYNTAX_STYLE_NONE);
        editor.setTabSize(3);
        editor.setCaretPosition(0);
        editor.setMarkOccurrences(true);
        editor.setClearWhitespaceLinesEnabled(false);
        Gutter gutter = scrollPane.getGutter();
        gutter.setBookmarkingEnabled(true);
        gutter.setBookmarkIcon(new ImageIcon(ClassLoader.getSystemResource("icon/TextEditor.Bookmark.png")));

        try {
            editor.setText(FileIO.read(file));
        } catch (Exception e) {
            ErrorHandler.alert(e);
        }

        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                modified = true;
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                modified = true;
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                modified = true;
            }
        });
    }

    private void ensureOutputShown() {
        if (jSplitPane1.getDividerLocation() >= jSplitPane1.getMaximumDividerLocation() - 20) {
            jSplitPane1.setDividerLocation(0.8D);
            jSplitPane1.revalidate();
        }
    }

    @Override
    public JMenu[] getContextMenus() {
        return new JMenu[0];
    }

    @Override
    public boolean modified() {
        return modified;
    }

    @Override
    public void save() {
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(file));
            out.println(editor.getText());
            modified = false;
        } catch (Exception ex) {
            ErrorHandler.alert(ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolbar = new javax.swing.JToolBar();
        runButton = new javax.swing.JButton();
        irbButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        cursorLabel = new javax.swing.JLabel();
        jSplitPane1 = new tk.amberide.ide.swing.misc.ThinSplitPane();
        scrollPane = new org.fife.ui.rtextarea.RTextScrollPane();
        editor = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
        closeableTabbedPane = new tk.amberide.ide.swing.tabs.CloseableTabbedPane();

        toolbar.setBorder(null);
        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        toolbar.setAlignmentY(0.5F);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("amber/Bundle"); // NOI18N
        runButton.setText(bundle.getString("ScriptEditorPanel.runButton.text")); // NOI18N
        runButton.setFocusable(false);
        runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        toolbar.add(runButton);

        irbButton.setText(bundle.getString("ScriptEditorPanel.irbButton.text")); // NOI18N
        irbButton.setFocusable(false);
        irbButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        irbButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        irbButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                irbButtonActionPerformed(evt);
            }
        });
        toolbar.add(irbButton);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.setLayout(new java.awt.BorderLayout());

        cursorLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jPanel1.add(cursorLabel, java.awt.BorderLayout.CENTER);

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerSize(0);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(0, 0));
        jSplitPane1.setPreferredSize(new java.awt.Dimension(0, 0));

        scrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        editor.setBorder(null);
        editor.setColumns(5);
        editor.setRows(5);
        editor.setAlignmentX(0.1F);
        editor.setAlignmentY(0.1F);
        editor.setAnimateBracketMatching(false);
        editor.setAntiAliasingEnabled(false);
        editor.setCodeFoldingEnabled(true);
        scrollPane.setViewportView(editor);

        jSplitPane1.setLeftComponent(scrollPane);

        closeableTabbedPane.setMinimumSize(new java.awt.Dimension(0, 0));
        closeableTabbedPane.setPreferredSize(new java.awt.Dimension(0, 0));
        jSplitPane1.setRightComponent(closeableTabbedPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(377, 377, 377)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE))
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addDirectoryTree(Ruby runtime, File root) {
        runtime.getLoadService().addPaths(root.getAbsolutePath());
        for (File node : root.listFiles()) {
            if (node.isDirectory()) {
                addDirectoryTree(runtime, node);
            }
        }
    }

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        ensureOutputShown();
        final RubyConsole con = new RubyConsole();
        con.setMargin(new Insets(8, 8, 8, 8));
        RTextScrollPane pane = new RTextScrollPane();
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setViewportView(con);
        addDirectoryTree(con.getRuntime(), Amber.getWorkspace().getRootDirectory());
        closeableTabbedPane.add(pane, "Run");
        closeableTabbedPane.setSelectedComponent(pane);

        new Thread() {
            @Override
            public void run() {
                con.eval(editor.getText());
                con.getRuntime().getOutputStream().println("\n\nExited.");
                con.setEditable(false);
            }
        }.start();
    }//GEN-LAST:event_runButtonActionPerformed

    private void irbButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_irbButtonActionPerformed
        ensureOutputShown();
        final RubyConsole con = new RubyConsole();
        con.setMargin(new Insets(8, 8, 8, 8));
        RTextScrollPane pane = new RTextScrollPane();
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setViewportView(con);
        addDirectoryTree(con.getRuntime(), Amber.getWorkspace().getRootDirectory());
        closeableTabbedPane.add(pane, "IRB");
        closeableTabbedPane.setSelectedComponent(pane);

        con.getRuntime().getOutputStream().println("Welcome to the IRB Console\n\n");
        new Thread() {
            @Override
            public void run() {
                con.eval("require 'irb'; require 'irb/completion'; IRB.start");
            }
        }.start();
    }//GEN-LAST:event_irbButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private tk.amberide.ide.swing.tabs.CloseableTabbedPane closeableTabbedPane;
    private javax.swing.JLabel cursorLabel;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea editor;
    private javax.swing.JButton irbButton;
    private javax.swing.JPanel jPanel1;
    private tk.amberide.ide.swing.misc.ThinSplitPane jSplitPane1;
    private javax.swing.JButton runButton;
    private org.fife.ui.rtextarea.RTextScrollPane scrollPane;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables
}
