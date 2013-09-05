package tk.amberide.ide.gui.dialogs.res;

import tk.amberide.Amber;
import tk.amberide.engine.al.Audio;
import tk.amberide.ide.data.res.IResourceListener;
import tk.amberide.ide.data.res.Resource;
import tk.amberide.ide.data.res.Tileset;
import tk.amberide.engine.gl.model.obj.WavefrontObject;
import tk.amberide.ide.gui.editor.map.res.ModelThumbnail;
import tk.amberide.ide.gui.misc.AudioPlayerPanel;
import tk.amberide.ide.swing.UIUtil;
import java.io.File;
import javax.sound.sampled.AudioFormat;

import javax.swing.*;
import javax.swing.table.TableModel;

/**
 * @author Tudor
 */
public class ResourceDialog extends javax.swing.JDialog implements IResourceListener {

    public static final int TILESET_PANEL = 0,
            AUDIO_PANEL = 1,
            MODEL_PANEL = 2;
    private JFileChooser browser;

    /**
     * Creates new form ResourceDialog
     */
    public ResourceDialog(java.awt.Frame parent) {
        super(parent);
        initComponents();

        UIUtil.setTreeEnabled(modelDetailsTable, false);
        UIUtil.setTreeEnabled(modelViewGroup, false);

        updateTilesetList();
        updateClipList();
        updateModelList();

        browser = new JFileChooser("Import directory...");
        browser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // ech
    }

    /**
     * Opens the given panel: one of TILESET_PANEL, AUDIO_PANEL, or MODEL_PANEL
     *
     * @param panel
     */
    public void setPanel(int panel) {
        switch (panel) {
            case TILESET_PANEL:
            case AUDIO_PANEL:
            case MODEL_PANEL:
                tabbedPane.setSelectedIndex(panel);
                break;
            default:
                throw new IllegalArgumentException("invalid panel");
        }
    }

    private void updateTilesetList() {
        ((DefaultListModel) tilesets.getModel()).clear();
        for (Resource<Tileset> set : Amber.getResourceManager().getTilesets()) {
            ((DefaultListModel) tilesets.getModel()).addElement(set.getName());
        }
        updateTilesetPreview();
        if (tilesets.getModel().getSize() > 0) {
            UIUtil.setTreeEnabled(tileDetailsGroup, true);
            UIUtil.setTreeEnabled(tileViewGroup, true);
        } else {
            UIUtil.setTreeEnabled(tileDetailsGroup, false);
            UIUtil.setTreeEnabled(tileViewGroup, false);
        }
    }

    private void updateClipList() {
        ((DefaultListModel) clips.getModel()).clear();
        for (Resource<Audio> clip : Amber.getResourceManager().getClips()) {
            ((DefaultListModel) clips.getModel()).addElement(clip.getName());
        }
        updateClipPreview();
        if (clips.getModel().getSize() > 0) {
            UIUtil.setTreeEnabled(audioDetailsGroup, true);
            UIUtil.setTreeEnabled(audioPlayGroup, true);
        } else {
            UIUtil.setTreeEnabled(audioDetailsGroup, false);
            UIUtil.setTreeEnabled(audioPlayGroup, false);
        }
    }

    private void updateModelList() {
        ((DefaultListModel) models.getModel()).clear();
        for (Resource<WavefrontObject> model : Amber.getResourceManager().getModels()) {
            ((DefaultListModel) models.getModel()).addElement(model.getName());
        }
        updateModelPreview();
        if (models.getModel().getSize() > 0) {
            UIUtil.setTreeEnabled(modelDetailsTable, true);
            UIUtil.setTreeEnabled(modelViewGroup, true);
        } else {
            UIUtil.setTreeEnabled(modelDetailsTable, false);
            UIUtil.setTreeEnabled(modelViewGroup, false);
        }
    }

    private void updateTilesetPreview() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                String name = (String) tilesets.getSelectedValue();
                tilesetPreviewLabel.setText("");
                Tileset sheet = Amber.getResourceManager().getTileset(name);
                tilesetPreviewLabel.setIcon(sheet != null ? new ImageIcon(sheet.getImage()) : null);
            }
        });
    }

    private void updateClipPreview() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                String name = (String) clips.getSelectedValue();
                audioPlayGroup.removeAll();
                Resource<Audio> res = Amber.getResourceManager().getAudioResource(name);
                if (res != null) {
                    Audio clip = res.get();
                    audioPlayGroup.add(new AudioPlayerPanel(res));
                    AudioFormat data = clip.getFormat();

                    if (data != null) {
                        TableModel mod = detailsTable.getModel();
                        mod.setValueAt(data.getEncoding(), 0, 1);
                        mod.setValueAt(data.getFrameRate() + "Hz", 1, 1);
                        mod.setValueAt(data.getFrameSize() + " bytes", 2, 1);
                        mod.setValueAt(data.getSampleRate(), 3, 1);
                        mod.setValueAt(data.getSampleSizeInBits() + " bits", 4, 1);
                        mod.setValueAt(data.getChannels(), 5, 1);
                        mod.setValueAt(data.getChannels() == 2 ? "stereo" : "mono", 6, 1);
                        mod.setValueAt(data.isBigEndian(), 7, 1);
                    }
                    UIUtil.adjustColumnPreferredWidths(detailsTable);
                }
                audioPlayGroup.revalidate();
            }
        });
    }

    private void updateModelPreview() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                String name = (String) models.getSelectedValue();
                Resource<WavefrontObject> res = Amber.getResourceManager().getModelResource(name);
                if (res != null) {
                    WavefrontObject model = res.get();
                    TableModel mod = modelDetailsTable.getModel();

                    mod.setValueAt("Wavefront OBJ", 0, 1);
                    mod.setValueAt(model.getGroups().size() + " groups", 1, 1);
                    mod.setValueAt(model.getVertices().size() + " vertices", 2, 1);
                    mod.setValueAt(model.getNormals().size() + " normals", 3, 1);
                    mod.setValueAt(model.getMaterials().size() + " materials", 4, 1);
                    UIUtil.adjustColumnPreferredWidths(modelDetailsTable);
                }

                modelPreviewLabel.setText("");
                WavefrontObject sheet = Amber.getResourceManager().getModel(name);
                modelPreviewLabel.setIcon(sheet != null ? new ImageIcon(ModelThumbnail.getModelImage(sheet, 150, 150)) : null);
            }
        });
    }

    public void tilesetImported(Resource<Tileset> sheet) {
        updateTilesetList();
        resourceImported(sheet, tilesets);
    }

    public void audioImported(Resource<Audio> clip) {
        updateClipList();
        resourceImported(clip, clips);
    }

    public void tilesetRemoved(Resource<Tileset> sheet) {
        resourceRemoved(sheet, tilesets, new Runnable() {
            public void run() {
                updateTilesetList();
            }
        });
    }

    public void audioRemoved(Resource<Audio> sheet) {
        resourceRemoved(sheet, clips, new Runnable() {
            public void run() {
                updateClipList();
            }
        });
    }

    public void modelImported(Resource<WavefrontObject> model) {
        updateModelList();
        resourceImported(model, models);
    }

    public void modelRemoved(Resource<WavefrontObject> model) {
        resourceRemoved(model, models, new Runnable() {
            public void run() {
                updateModelList();
            }
        });
    }

    private void resourceRemoved(Resource res, JList list, Runnable callback) {
        int index = ((DefaultListModel) list.getModel()).indexOf(res.getName());
        callback.run();
        if (list.getModel().getSize() > 0) {
            list.setSelectedIndex(index > 0 ? index - 1 : index + 1);
        }
    }

    private void resourceImported(Resource res, JList list) {
        models.setSelectedIndex(((DefaultListModel) models.getModel()).indexOf(res.getName())); // Select the resource just imported
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tilesets = new javax.swing.JList();
        importTilesetDirectory = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        newTilesetButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        deleteTilesetButton = new javax.swing.JButton();
        tileDetailsGroup = new javax.swing.JPanel();
        tileViewGroup = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tilesetPreviewLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        clips = new javax.swing.JList();
        importClipDirectory = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        newClipButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        deleteClipButton = new javax.swing.JButton();
        audioDetailsGroup = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        detailsTable = new javax.swing.JTable();
        audioPlayGroup = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        models = new javax.swing.JList();
        jButton15 = new javax.swing.JButton();
        jToolBar3 = new javax.swing.JToolBar();
        importModelButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        deleteModelButton = new javax.swing.JButton();
        modelViewGroup = new javax.swing.JPanel();
        modelPreviewLabel = new javax.swing.JLabel();
        detailsScrollPane = new javax.swing.JScrollPane();
        modelDetailsTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("amber/Bundle"); // NOI18N
        setTitle(bundle.getString("ResourceDialog.title")); // NOI18N
        setMaximumSize(null);
        setMinimumSize(new java.awt.Dimension(395, 406));
        setModal(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tabbedPane.setFocusable(false);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ResourceDialog.jPanel3.border.title"))); // NOI18N

        tilesets.setModel(new DefaultListModel());
        tilesets.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                tilesetsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(tilesets);

        importTilesetDirectory.setText(bundle.getString("ResourceDialog.importTilesetDirectory.text")); // NOI18N
        importTilesetDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importTilesetDirectoryActionPerformed(evt);
            }
        });

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setMaximumSize(new java.awt.Dimension(57, 31));
        jToolBar2.setMinimumSize(new java.awt.Dimension(57, 31));
        jToolBar2.setPreferredSize(new java.awt.Dimension(57, 31));

        newTilesetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/General.Add.png"))); // NOI18N
        newTilesetButton.setText(bundle.getString("ResourceDialog.newTilesetButton.text")); // NOI18N
        newTilesetButton.setToolTipText(bundle.getString("ResourceDialog.newTilesetButton.toolTipText")); // NOI18N
        newTilesetButton.setDefaultCapable(false);
        newTilesetButton.setFocusable(false);
        newTilesetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newTilesetButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        newTilesetButton.setMaximumSize(new java.awt.Dimension(26, 26));
        newTilesetButton.setMinimumSize(new java.awt.Dimension(26, 26));
        newTilesetButton.setPreferredSize(new java.awt.Dimension(26, 26));
        newTilesetButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newTilesetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newTilesetButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(newTilesetButton);
        jToolBar2.add(filler2);

        deleteTilesetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/General.Remove.png"))); // NOI18N
        deleteTilesetButton.setText(bundle.getString("ResourceDialog.deleteTilesetButton.text")); // NOI18N
        deleteTilesetButton.setToolTipText(bundle.getString("ResourceDialog.deleteTilesetButton.toolTipText")); // NOI18N
        deleteTilesetButton.setDefaultCapable(false);
        deleteTilesetButton.setFocusable(false);
        deleteTilesetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteTilesetButton.setIconTextGap(0);
        deleteTilesetButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        deleteTilesetButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        deleteTilesetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTilesetButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(deleteTilesetButton);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(2, 2, 2))
            .addComponent(importTilesetDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(importTilesetDirectory))
        );

        tileDetailsGroup.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ResourceDialog.tileDetailsGroup.border.title"))); // NOI18N

        javax.swing.GroupLayout tileDetailsGroupLayout = new javax.swing.GroupLayout(tileDetailsGroup);
        tileDetailsGroup.setLayout(tileDetailsGroupLayout);
        tileDetailsGroupLayout.setHorizontalGroup(
            tileDetailsGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 208, Short.MAX_VALUE)
        );
        tileDetailsGroupLayout.setVerticalGroup(
            tileDetailsGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 26, Short.MAX_VALUE)
        );

        tileViewGroup.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ResourceDialog.tileViewGroup.border.title"))); // NOI18N
        tileViewGroup.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));

        tilesetPreviewLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tilesetPreviewLabel.setText(bundle.getString("ResourceDialog.tilesetPreviewLabel.text")); // NOI18N
        jScrollPane2.setViewportView(tilesetPreviewLabel);

        javax.swing.GroupLayout tileViewGroupLayout = new javax.swing.GroupLayout(tileViewGroup);
        tileViewGroup.setLayout(tileViewGroupLayout);
        tileViewGroupLayout.setHorizontalGroup(
            tileViewGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );
        tileViewGroupLayout.setVerticalGroup(
            tileViewGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tileDetailsGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tileViewGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tileDetailsGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tileViewGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );

        tabbedPane.addTab(bundle.getString("ResourceDialog.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jPanel1.setPreferredSize(new java.awt.Dimension(370, 374));

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ResourceDialog.jPanel10.border.title"))); // NOI18N

        clips.setModel(new DefaultListModel());
        clips.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                clipsValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(clips);

        importClipDirectory.setText(bundle.getString("ResourceDialog.importClipDirectory.text")); // NOI18N
        importClipDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importClipDirectoryActionPerformed(evt);
            }
        });

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setMaximumSize(new java.awt.Dimension(57, 31));
        jToolBar1.setMinimumSize(new java.awt.Dimension(57, 31));
        jToolBar1.setPreferredSize(new java.awt.Dimension(57, 31));

        newClipButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/General.Add.png"))); // NOI18N
        newClipButton.setText(bundle.getString("ResourceDialog.newClipButton.text")); // NOI18N
        newClipButton.setToolTipText(bundle.getString("ResourceDialog.newClipButton.toolTipText")); // NOI18N
        newClipButton.setDefaultCapable(false);
        newClipButton.setFocusable(false);
        newClipButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newClipButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        newClipButton.setMaximumSize(new java.awt.Dimension(26, 26));
        newClipButton.setMinimumSize(new java.awt.Dimension(26, 26));
        newClipButton.setPreferredSize(new java.awt.Dimension(26, 26));
        newClipButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newClipButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newClipButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(newClipButton);
        jToolBar1.add(filler1);

        deleteClipButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/General.Remove.png"))); // NOI18N
        deleteClipButton.setText(bundle.getString("ResourceDialog.deleteClipButton.text")); // NOI18N
        deleteClipButton.setToolTipText(bundle.getString("ResourceDialog.deleteClipButton.toolTipText")); // NOI18N
        deleteClipButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        deleteClipButton.setFocusable(false);
        deleteClipButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteClipButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        deleteClipButton.setMaximumSize(new java.awt.Dimension(29, 29));
        deleteClipButton.setMinimumSize(new java.awt.Dimension(29, 29));
        deleteClipButton.setPreferredSize(new java.awt.Dimension(29, 29));
        deleteClipButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        deleteClipButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteClipButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(deleteClipButton);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(2, 2, 2))
            .addComponent(importClipDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(importClipDirectory))
        );

        audioDetailsGroup.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ResourceDialog.audioDetailsGroup.border.title"))); // NOI18N

        jScrollPane4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        detailsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Encoding:", null},
                {"Framerate:", null},
                {"Frame size:", null},
                {"Sample rate:", null},
                {"Sample size:", null},
                {"Channels:", null},
                {"Mode:", null},
                {"Big endian:", null}
            },
            new String [] {
                "Property", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        detailsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(detailsTable);

        javax.swing.GroupLayout audioDetailsGroupLayout = new javax.swing.GroupLayout(audioDetailsGroup);
        audioDetailsGroup.setLayout(audioDetailsGroupLayout);
        audioDetailsGroupLayout.setHorizontalGroup(
            audioDetailsGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(audioDetailsGroupLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addContainerGap())
        );
        audioDetailsGroupLayout.setVerticalGroup(
            audioDetailsGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, audioDetailsGroupLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        audioPlayGroup.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ResourceDialog.audioPlayGroup.border.title"))); // NOI18N
        audioPlayGroup.setAlignmentX(0.0F);
        audioPlayGroup.setAlignmentY(0.0F);
        audioPlayGroup.setLayout(new java.awt.GridBagLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(audioDetailsGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(audioPlayGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(audioDetailsGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(audioPlayGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );

        tabbedPane.addTab(bundle.getString("ResourceDialog.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ResourceDialog.jPanel12.border.title"))); // NOI18N

        models.setModel(new DefaultListModel());
        models.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                modelsValueChanged(evt);
            }
        });
        jScrollPane6.setViewportView(models);

        jButton15.setText(bundle.getString("ResourceDialog.jButton15.text")); // NOI18N
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        importModelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/General.Add.png"))); // NOI18N
        importModelButton.setText(bundle.getString("ResourceDialog.text")); // NOI18N
        importModelButton.setToolTipText(bundle.getString("ResourceDialog.importModelButton.toolTipText")); // NOI18N
        importModelButton.setFocusable(false);
        importModelButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        importModelButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        importModelButton.setMaximumSize(new java.awt.Dimension(26, 26));
        importModelButton.setMinimumSize(new java.awt.Dimension(26, 26));
        importModelButton.setName(""); // NOI18N
        importModelButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        importModelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importModelButtonActionPerformed(evt);
            }
        });
        jToolBar3.add(importModelButton);
        jToolBar3.add(filler3);

        deleteModelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/General.Remove.png"))); // NOI18N
        deleteModelButton.setText(bundle.getString("ResourceDialog.jButton12.text")); // NOI18N
        deleteModelButton.setToolTipText(bundle.getString("ResourceDialog.deleteModelButton.toolTipText")); // NOI18N
        deleteModelButton.setFocusable(false);
        deleteModelButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteModelButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        deleteModelButton.setName(""); // NOI18N
        deleteModelButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(deleteModelButton);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(2, 2, 2))
            .addComponent(jButton15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jScrollPane6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton15))
        );

        modelViewGroup.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ResourceDialog.modelViewGroup.border.title"))); // NOI18N
        modelViewGroup.setLayout(new java.awt.BorderLayout());

        modelPreviewLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        modelPreviewLabel.setText(bundle.getString("ResourceDialog.modelPreviewLabel.text")); // NOI18N
        modelPreviewLabel.setMaximumSize(null);
        modelViewGroup.add(modelPreviewLabel, java.awt.BorderLayout.CENTER);

        detailsScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ResourceDialog.detailsScrollPane.border.title"))); // NOI18N
        detailsScrollPane.setMaximumSize(new java.awt.Dimension(462, 423));
        detailsScrollPane.setMinimumSize(new java.awt.Dimension(462, 423));

        modelDetailsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Format", null},
                {"Faces", null},
                {"Vertices", null},
                {"Normals", null},
                {"Materials", null}
            },
            new String [] {
                "Property", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        modelDetailsTable.getTableHeader().setReorderingAllowed(false);
        detailsScrollPane.setViewportView(modelDetailsTable);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(modelViewGroup, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                    .addComponent(detailsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(detailsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(modelViewGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10))))
        );

        tabbedPane.addTab(bundle.getString("ResourceDialog.jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void newTilesetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newTilesetButtonActionPerformed
        new NewTilesetDialog(Amber.getUI()).setVisible(true);
    }//GEN-LAST:event_newTilesetButtonActionPerformed

    private void newClipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newClipButtonActionPerformed
        new NewAudioDialog(Amber.getUI()).setVisible(true);
    }//GEN-LAST:event_newClipButtonActionPerformed

    private void importTilesetDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importTilesetDirectoryActionPerformed
        for (File f : ImportResourceDirectoryDialog.showResourceDirectoryDialog(Amber.getUI(), "png", "jpg", "jpeg", "gif", "bmp")) {
            new NewTilesetDialog(Amber.getUI(), f).setVisible(true);
        }
    }//GEN-LAST:event_importTilesetDirectoryActionPerformed

    private void tilesetsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_tilesetsValueChanged
        updateTilesetPreview();
    }//GEN-LAST:event_tilesetsValueChanged

    private void deleteTilesetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteTilesetButtonActionPerformed
        Amber.getResourceManager().removeTileset((String) tilesets.getSelectedValue());
    }//GEN-LAST:event_deleteTilesetButtonActionPerformed

    private void importClipDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importClipDirectoryActionPerformed
        for (File f : ImportResourceDirectoryDialog.showResourceDirectoryDialog(Amber.getUI(), "wav", "aiff", "ogg", "mid", "midi")) {
            new NewAudioDialog(Amber.getUI(), f).setVisible(true);
        }
    }//GEN-LAST:event_importClipDirectoryActionPerformed

    private void deleteClipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteClipButtonActionPerformed
        Amber.getResourceManager().removeAudio((String) clips.getSelectedValue());
    }//GEN-LAST:event_deleteClipButtonActionPerformed

    private void clipsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_clipsValueChanged
        updateClipPreview();
    }//GEN-LAST:event_clipsValueChanged

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        Amber.getResourceManager().removeResourceListener(this);
    }//GEN-LAST:event_formWindowClosed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        Amber.getResourceManager().addResourceListener(this);
    }//GEN-LAST:event_formWindowOpened

    private void importModelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importModelButtonActionPerformed
        new NewModelDialog(Amber.getUI()).setVisible(true);
    }//GEN-LAST:event_importModelButtonActionPerformed

    private void modelsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_modelsValueChanged
        updateModelPreview();
    }//GEN-LAST:event_modelsValueChanged

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        for (File f : ImportResourceDirectoryDialog.showResourceDirectoryDialog(Amber.getUI(), "obj")) {
            new NewModelDialog(Amber.getUI(), f).setVisible(true);
        }
    }//GEN-LAST:event_jButton15ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel audioDetailsGroup;
    private javax.swing.JPanel audioPlayGroup;
    private javax.swing.JList clips;
    private javax.swing.JButton deleteClipButton;
    private javax.swing.JButton deleteModelButton;
    private javax.swing.JButton deleteTilesetButton;
    private javax.swing.JScrollPane detailsScrollPane;
    private javax.swing.JTable detailsTable;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton importClipDirectory;
    private javax.swing.JButton importModelButton;
    private javax.swing.JButton importTilesetDirectory;
    private javax.swing.JButton jButton15;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JTable modelDetailsTable;
    private javax.swing.JLabel modelPreviewLabel;
    private javax.swing.JPanel modelViewGroup;
    private javax.swing.JList models;
    private javax.swing.JButton newClipButton;
    private javax.swing.JButton newTilesetButton;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JPanel tileDetailsGroup;
    private javax.swing.JPanel tileViewGroup;
    private javax.swing.JLabel tilesetPreviewLabel;
    private javax.swing.JList tilesets;
    // End of variables declaration//GEN-END:variables
}
