package tk.amberide.ide.swing;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 *
 * @author Tudor
 */
public class Dialogs {

    public static OptionDialogBuilder confirmDialog() {
        return new OptionDialogBuilder();
    }

    public static OptionDialogBuilder errorDialog() {
        return confirmDialog()
                .setOptionType(JOptionPane.OK_CANCEL_OPTION)
                .setMessageType(JOptionPane.ERROR_MESSAGE);
    }

    public static class OptionDialogBuilder {

        private String title;
        private Component parent;
        private String message;
        private int optionType;
        private int messageType;
        private Icon icon;
        private Object[] options;

        OptionDialogBuilder() {
        }
        
        /*
         Component parentComponent,
        Object message, String title, int optionType, int messageType,
        Icon icon, Object[] options, Object initialValue 
         
         */

        public int show() {
            return JOptionPane.showOptionDialog(
                    parent,
                    message,
                    title,
                    optionType,
                    messageType,
                    icon,
                    options,
                    null);
        }

        /**
         * @param title the title to set
         */
        public OptionDialogBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * @param parent the parent to set
         */
        public OptionDialogBuilder setParent(Component parent) {
            this.parent = parent;
            return this;
        }

        /**
         * @param message the message to set
         */
        public OptionDialogBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * @param optionType the optionType to set
         */
        public OptionDialogBuilder setOptionType(int optionType) {
            this.optionType = optionType;
            return this;
        }

        /**
         * @param messageType the messageType to set
         */
        public OptionDialogBuilder setMessageType(int messageType) {
            this.messageType = messageType;
            return this;
        }

        /**
         * @param icon the icon to set
         */
        public OptionDialogBuilder setIcon(Icon icon) {
            this.icon = icon;
            return this;
        }

        /**
         * @param options the options to set
         */
        public OptionDialogBuilder setOptions(Object... options) {
            this.options = options;
            return this;
        }
    }
}
