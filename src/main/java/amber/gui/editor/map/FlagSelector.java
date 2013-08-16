package amber.gui.editor.map;

import amber.Amber;
import amber.data.map.Flag;
import amber.gui.dialogs.NewFlagDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultCellEditor;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Tudor
 */
public class FlagSelector extends JPanel {

    protected JScrollPane scroller;
    protected JTable flagTable;
    protected DefaultTableModel model = new DefaultTableModel(
            new Object[0][0],
            new String[]{
        "Colour", "Name", "ID"
    }) {
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return new boolean[]{
                true, true, true
            }[columnIndex];
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            return new Class[]{
                Color.class, String.class, Integer.class
            }[columnIndex];
        }
    };

    static {
        Flag.registerFlag(new Flag(null, "PASSABLE", 0));
        Flag.registerFlag(new Flag(Color.RED, "IMPASSABLE", 1));
        Flag.registerFlag(new Flag(Color.GREEN, "GRASS", 2));
        Flag.registerFlag(new Flag(Color.CYAN, "SURF", 3));
    }

    public FlagSelector(final MapContext context) {
        super(new BorderLayout());
        flagTable = new JTable(model);
        scroller = new JScrollPane();
        scroller.setViewportView(flagTable);
        flagTable.setFillsViewportHeight(true);
        flagTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(scroller, BorderLayout.CENTER);
        JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem("New Flag")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                NewFlagDialog nfd = new NewFlagDialog(Amber.getUI());
                nfd.setVisible(true);
                Flag f = nfd.getFlag();
                if (f != null) {
                    Flag.registerFlag(f);
                    synchronize();
                }
            }
        });
        flagTable.setComponentPopupMenu(popup);
        flagTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Color) {
                    JLabel ret = new JLabel();
                    ret.setOpaque(true);
                    ret.setBackground((Color) value);
                    return ret;
                } else {
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            }
        });
        ((DefaultCellEditor) flagTable.getDefaultEditor(String.class)).setClickCountToStart(3);
        flagTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()) {
            private JSpinner spinner = new JSpinner();
            private Flag flag;

            {
                spinner.setBorder(null);
                setClickCountToStart(3);
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                int id = Integer.parseInt(value.toString());
                spinner.setValue(id);
                flag = Flag.byId(id);
                return spinner;
            }

            @Override
            public Object getCellEditorValue() {
                if (flag != null) {
                    flag.setId((Integer) spinner.getValue());
                    synchronize();
                }
                return String.format("%02d", spinner.getValue());
            }
        });
        flagTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() > 1 && flagTable.columnAtPoint(evt.getPoint()) == 0) {
                    int row = flagTable.rowAtPoint(evt.getPoint());
                    Color color = JColorChooser.showDialog(flagTable, "Choose flag colour...", (Color) flagTable.getValueAt(row, 0));
                    if (color != null) {
                        flagTable.setValueAt(color, row, 0);
                        Flag f = Flag.byId(Integer.parseInt(model.getValueAt(2, row).toString()));
                        if (f != null) {
                            f.setColor(color);
                            System.out.println("Set flag colour: " + color);
                        }
                    }
                }
            }
        });
        flagTable.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent evt) {
                int column = evt.getColumn();
                if (column == 1 && evt.getType() == TableModelEvent.UPDATE) {
                    int row = evt.getFirstRow();
                    int id = Integer.parseInt(model.getValueAt(row, 2).toString());
                    Flag f = Flag.byId(id);
                    if (f != null) {
                        f.setName(model.getValueAt(row, column).toString());
                    }
                }
            }
        });

        flagTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            int selected = flagTable.getSelectedRow();
                            if (selected == -1 && flagTable.getRowCount() > 0) {
                                flagTable.getSelectionModel().addSelectionInterval(0, 0);
                            }
                            int id = Integer.parseInt(model.getValueAt(flagTable.getSelectedRow(), 2).toString());
                            System.out.println(id);
                            context.flag = Flag.byId(id);
                        }
                    });
                }
            }
        });
        synchronize();
    }

    public void synchronize() {
        model.getDataVector().clear();
        for (Flag f : Flag.flags()) {
            model.addRow(new Object[]{f.getColor(), f.getName(), String.format("%02d", f.getId())});
        }
    }
}
