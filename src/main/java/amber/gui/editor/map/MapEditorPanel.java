package amber.gui.editor.map;

import amber.Amber;
import amber.al.Audio;
import amber.data.res.Tileset;
import amber.data.map.LevelMap;
import amber.data.map.codec.Codec;
import amber.data.res.IResourceListener;
import amber.data.res.Resource;
import amber.gl.model.obj.WavefrontObject;
import amber.gui.dialogs.NewLayerDialog;
import amber.gui.dialogs.NewTilesetDialog;
import amber.gui.dialogs.ResourceDialog;
import amber.gui.editor.FileViewerPanel;
import amber.swing.LabelBuilder;
import amber.swing.UIUtil;
import amber.swing.misc.FileDropHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.lwjgl.LWJGLException;

/**
 * @author Tudor
 */
public class MapEditorPanel extends FileViewerPanel {

    private TileSelector tilechooser;
    private ModelSelector modelchooser;
    private FlagSelector flagchooser;
    private IMapComponent renderer;
    private LevelMap map;

    /**
     * Creates new form MapEditorPanel
     */
    public MapEditorPanel(final File mapFile) throws IOException, LWJGLException {
        super(mapFile);
        initComponents();
        map = Codec.CODECS.get(Codec.CURRENT_VERSION).loadMap(new DataInputStream(new FileInputStream(mapFile)));
        renderer = map.getType() == LevelMap.Type._2D ? new GLMapComponent2D(map) : new GLMapComponent3D(map);
        renderer.getMapContext().outputFile = mapFile;
        cardinalityButton.setVisible(renderer.getMapContext().EXT_cardinalSupported);
        tilePanel.setLayout(new BorderLayout());
        tilechooser = new TileSelector(renderer.getMapContext());

        resourcesTabbedPane.validate();
        if (Amber.getResourceManager().getTilesets().size() > 0) {
            tilePanel.add(tilechooser);
            tilechooser.synchronize(); // don't add label in first place
        } else {
            LabelBuilder builder = new LabelBuilder();
            builder.append("No tile groups found -- ");
            builder.setForeground(new Color(80, 100, 200));
            builder.action("create one.", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ResourceDialog res = new ResourceDialog(Amber.getUI());
                    res.setPanel(ResourceDialog.TILESET_PANEL);
                    res.setVisible(true);
                }
            });
            JComponent label = builder.create();
            label.setToolTipText("You can also drop files here to import them.");
            tilePanel.add(label, BorderLayout.CENTER);
            tilePanel.setTransferHandler(new FileDropHandler() {
                @Override
                public void filesDropped(File[] files) {
                    for (File file : files) {
                        new NewTilesetDialog(Amber.getUI(), file).setVisible(true);
                    }
                }
            });
        }
        if (!renderer.getMapContext().EXT_modelSelectionSupported) {
            resourcesTabbedPane.remove(1); // Models tab
        } else {
            modelchooser = new ModelSelector(renderer.getMapContext());
            if (Amber.getResourceManager().getModels().size() > 0) {
                modelPanel.add(modelchooser);
                modelchooser.synchronize();
            } else {
                LabelBuilder builder = new LabelBuilder();
                builder.append("No models found -- ");
                builder.setForeground(new Color(80, 100, 200));
                builder.action("add one.", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ResourceDialog res = new ResourceDialog(Amber.getUI());
                        res.setPanel(ResourceDialog.MODEL_PANEL);
                        res.setVisible(true);
                    }
                });
                modelPanel.add(builder.create(), BorderLayout.CENTER);
            }
        }
        Amber.getResourceManager().addResourceListener(new IResourceListener() {
            public void tilesetImported(Resource<Tileset> sheet) {
                if (Amber.getResourceManager().getTilesets().size() > 0) {
                    // A tileset was imported; remove the import label
                    tilePanel.removeAll();
                    tilePanel.add(tilechooser);
                }
                tilechooser.synchronize();
            }

            public void tilesetRemoved(Resource<Tileset> sheet) {
                tilechooser.synchronize();
            }

            public void modelImported(Resource<WavefrontObject> model) {
                if (Amber.getResourceManager().getModels().size() > 0) {
                    modelPanel.removeAll();
                    modelPanel.add(modelchooser);
                }
                modelchooser.synchronize();
            }

            public void modelRemoved(Resource<WavefrontObject> model) {
                modelchooser.synchronize();
            }

            public void audioImported(Resource<Audio> clip) {
            }

            public void audioRemoved(Resource<Audio> clip) {
            }
        });

        resourcesTabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.out.println("Tab: " + resourcesTabbedPane.getSelectedIndex());
                switch (resourcesTabbedPane.getSelectedIndex()) {
                    case 0: // tiles
                        renderer.getMapContext().drawType = MapContext.TYPE_TILE;
                        break;
                    case 1: // models
                        if (renderer.getMapContext().EXT_modelSelectionSupported) {
                            renderer.getMapContext().drawType = MapContext.EXT_TYPE_MODEL;
                        }
                        break;
                    case 2: // flags
                        renderer.getMapContext().drawType = MapContext.TYPE_FLAG;
                        break;
                }
            }
        });

        flagPanel.add(flagchooser = new FlagSelector(renderer.getMapContext()));

        UIUtil.groupButtons(brushButton, fillButton, eraseButton, selectButton);

        UIUtil.adjustColumnPreferredWidths(table);
        UIUtil.makeDnD(table);

        for (int l = map.getLayers().size(); l != 0; l--) {
            ((DefaultTableModel) table.getModel()).addRow(new Object[]{map.getLayer(l - 1).getName(), true});
        }
        table.getSelectionModel().addSelectionInterval(0, table.getRowCount() - 1); // Select background layer      
        if (renderer != null) {
            mapScene.add(renderer.getComponent(), BorderLayout.CENTER);
        }
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        renderer.getMapContext().layer = table.getRowCount() - table.getSelectedRow() - 1;
                        System.out.println("chose layer " + renderer.getMapContext().map.getLayer(renderer.getMapContext().layer).getName());
                    }
                });
            }
        });
        JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem("New Layer")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new NewLayerDialog(Amber.getUI(), map).setVisible(true);
                if (map.getLayers().size() != table.getModel().getRowCount()) {
                    ((DefaultTableModel) table.getModel()).insertRow(0, new Object[]{map.getLayer(map.getLayers().size() - 1).getName(), true});
                }
            }
        });
        table.setComponentPopupMenu(popup);
        UIUtil.setHeaderIcon(table, 1, new ImageIcon(ClassLoader.getSystemResource("icon/MapEditor.Layers.Visible.png")));
        UIUtil.setHeaderIcon(table, 2, new ImageIcon(ClassLoader.getSystemResource("icon/MapEditor.Layers.Lock.png")));
    }

    @Override
    public JMenu[] getContextMenus() {
        return renderer.getContextMenus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mapResourcesSplitPane = new amber.swing.misc.ThinSplitPane();
        resourcesTabbedPane = new javax.swing.JTabbedPane();
        tilePanel = new javax.swing.JPanel();
        modelPanel = new javax.swing.JPanel();
        flagPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        thinSplitPane1 = new amber.swing.misc.ThinSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        widgetTabbedPane = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        mapBack = new javax.swing.JPanel();
        scenePanel = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        brushButton = new javax.swing.JToggleButton();
        eraseButton = new javax.swing.JToggleButton();
        fillButton = new javax.swing.JToggleButton();
        selectButton = new javax.swing.JToggleButton();
        cardinalityButton = new javax.swing.JToggleButton();
        mapScene = new javax.swing.JPanel();

        mapResourcesSplitPane.setBorder(null);
        mapResourcesSplitPane.setDividerLocation(256);
        mapResourcesSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        mapResourcesSplitPane.setResizeWeight(1.0);
        mapResourcesSplitPane.setMaximumSize(new java.awt.Dimension(256, 32));
        mapResourcesSplitPane.setMinimumSize(new java.awt.Dimension(256, 32));
        mapResourcesSplitPane.setPreferredSize(new java.awt.Dimension(0, 0));

        tilePanel.setMaximumSize(new java.awt.Dimension(256, 32));
        tilePanel.setMinimumSize(new java.awt.Dimension(256, 32));

        javax.swing.GroupLayout tilePanelLayout = new javax.swing.GroupLayout(tilePanel);
        tilePanel.setLayout(tilePanelLayout);
        tilePanelLayout.setHorizontalGroup(
            tilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 283, Short.MAX_VALUE)
        );
        tilePanelLayout.setVerticalGroup(
            tilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 228, Short.MAX_VALUE)
        );

        resourcesTabbedPane.addTab("Tiles", tilePanel);
        resourcesTabbedPane.addTab("Models", modelPanel);

        flagPanel.setLayout(new java.awt.BorderLayout());
        resourcesTabbedPane.addTab("Flags", flagPanel);

        jPanel1.setLayout(new java.awt.BorderLayout());

        thinSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        thinSplitPane1.setTopComponent(jScrollPane2);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setFillsViewportHeight(true);
        jScrollPane3.setViewportView(jTable1);

        thinSplitPane1.setRightComponent(jScrollPane3);

        jPanel1.add(thinSplitPane1, java.awt.BorderLayout.CENTER);

        resourcesTabbedPane.addTab("Regions", jPanel1);

        mapResourcesSplitPane.setLeftComponent(resourcesTabbedPane);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Visible", "Locked"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        table.setDragEnabled(true);
        table.setDropMode(javax.swing.DropMode.INSERT_ROWS);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(table);

        widgetTabbedPane.addTab("Layers", jScrollPane1);

        mapResourcesSplitPane.setRightComponent(widgetTabbedPane);

        mapBack.setLayout(new java.awt.BorderLayout());

        scenePanel.setLayout(new java.awt.BorderLayout());

        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setAlignmentX(0.0F);
        toolBar.setDoubleBuffered(true);
        toolBar.setMaximumSize(new java.awt.Dimension(26, 26));
        toolBar.setMinimumSize(new java.awt.Dimension(26, 26));
        toolBar.setPreferredSize(new java.awt.Dimension(26, 26));
        toolBar.setRequestFocusEnabled(false);

        brushButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/MapEditor.Brush.png"))); // NOI18N
        brushButton.setSelected(true);
        brushButton.setToolTipText("Brush tool...");
        brushButton.setFocusable(false);
        brushButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        brushButton.setMaximumSize(new java.awt.Dimension(26, 26));
        brushButton.setMinimumSize(new java.awt.Dimension(26, 26));
        brushButton.setPreferredSize(new java.awt.Dimension(26, 26));
        brushButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        brushButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                brushButtonActionPerformed(evt);
            }
        });
        toolBar.add(brushButton);

        eraseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/MapEditor.Eraser.png"))); // NOI18N
        eraseButton.setToolTipText("Eraser tool...");
        eraseButton.setFocusable(false);
        eraseButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        eraseButton.setMaximumSize(new java.awt.Dimension(26, 26));
        eraseButton.setMinimumSize(new java.awt.Dimension(26, 26));
        eraseButton.setPreferredSize(new java.awt.Dimension(26, 26));
        eraseButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(eraseButton);

        fillButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/MapEditor.Fill.png"))); // NOI18N
        fillButton.setToolTipText("Fill tool...");
        fillButton.setFocusable(false);
        fillButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fillButton.setMaximumSize(new java.awt.Dimension(26, 26));
        fillButton.setMinimumSize(new java.awt.Dimension(26, 26));
        fillButton.setPreferredSize(new java.awt.Dimension(26, 26));
        fillButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fillButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillButtonActionPerformed(evt);
            }
        });
        toolBar.add(fillButton);

        selectButton.setText("Select");
        selectButton.setFocusable(false);
        selectButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });
        toolBar.add(selectButton);

        cardinalityButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/MapEditor.Compass.png"))); // NOI18N
        cardinalityButton.setSelected(true);
        cardinalityButton.setToolTipText("Use cardinal placing...");
        cardinalityButton.setAlignmentX(0.5F);
        cardinalityButton.setFocusable(false);
        cardinalityButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cardinalityButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cardinalityButton.setMaximumSize(new java.awt.Dimension(26, 26));
        cardinalityButton.setMinimumSize(new java.awt.Dimension(26, 26));
        cardinalityButton.setPreferredSize(new java.awt.Dimension(26, 26));
        cardinalityButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cardinalityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cardinalityButtonActionPerformed(evt);
            }
        });
        toolBar.add(cardinalityButton);

        scenePanel.add(toolBar, java.awt.BorderLayout.NORTH);

        mapScene.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        mapScene.setLayout(new java.awt.BorderLayout());
        scenePanel.add(mapScene, java.awt.BorderLayout.CENTER);

        mapBack.add(scenePanel, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(mapBack, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(mapResourcesSplitPane, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mapBack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mapResourcesSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void fillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillButtonActionPerformed
        renderer.getMapContext().drawMode = MapContext.MODE_FILL;
    }//GEN-LAST:event_fillButtonActionPerformed

    private void brushButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_brushButtonActionPerformed
        renderer.getMapContext().drawMode = MapContext.MODE_BRUSH;
    }//GEN-LAST:event_brushButtonActionPerformed

    private void cardinalityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cardinalityButtonActionPerformed
        if (renderer.getMapContext().EXT_cardinalSupported) {
            renderer.getMapContext().EXT_cardinal = !renderer.getMapContext().EXT_cardinal;
        }
    }//GEN-LAST:event_cardinalityButtonActionPerformed

    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        renderer.getMapContext().drawMode = MapContext.MODE_SELECT;
    }//GEN-LAST:event_selectButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton brushButton;
    private javax.swing.JToggleButton cardinalityButton;
    private javax.swing.JToggleButton eraseButton;
    private javax.swing.JToggleButton fillButton;
    private javax.swing.JPanel flagPanel;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel mapBack;
    private amber.swing.misc.ThinSplitPane mapResourcesSplitPane;
    private javax.swing.JPanel mapScene;
    private javax.swing.JPanel modelPanel;
    private javax.swing.JTabbedPane resourcesTabbedPane;
    private javax.swing.JPanel scenePanel;
    private javax.swing.JToggleButton selectButton;
    private javax.swing.JTable table;
    private amber.swing.misc.ThinSplitPane thinSplitPane1;
    private javax.swing.JPanel tilePanel;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JTabbedPane widgetTabbedPane;
    // End of variables declaration//GEN-END:variables
}
