package tk.amberide.ide.data.io;

/*
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, 
 * MA  02111-1307, USA.
 */
import java.util.*;
import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Class for monitoring changes in disk files. Usage:
 *
 * 1. Implement the FileListener interface. 2. Create a FileMonitor instance. 3.
 * Add the file(s)/directory(ies) to listen for.
 *
 * fileChanged() will be called when a monitored file is created, deleted or its
 * modified time changes.
 *
 * @author <a href="mailto:info@geosoft.no">GeoSoft</a>
 */
public class FileMonitor {

    private Timer timer_;
    private HashMap<File, Long> files_;
    private Collection<WeakReference<FileListener>> listeners_;

    /**
     * Create a file monitor instance with specified polling interval.
     *
     * @param pollingInterval Polling interval in milli seconds.
     */
    public FileMonitor(long pollingInterval) {
        files_ = new HashMap<File, Long>();
        listeners_ = new ArrayList<WeakReference<FileListener>>();

        timer_ = new Timer(true);
        timer_.schedule(new FileMonitorNotifier(), 0, pollingInterval);
    }

    /**
     * Stop the file monitor polling.
     */
    public void stop() {
        timer_.cancel();
    }

    /**
     * Add file to listen for. File may be any java.io.File (including a
     * directory) and may well be a non-existing file in the case where the
     * creating of the file is to be trepped.
     * <p>
     * More than one file can be listened for. When the specified file is
     * created, modified or deleted, listeners are notified.
     *
     * @param file File to listen for.
     */
    public void addFile(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                addFile(f);
            }
        }
        if (!files_.containsKey(file)) {
            long modifiedTime = file.exists() ? file.lastModified() : -1;
            files_.put(file, new Long(modifiedTime));
        }
    }

    /**
     * Remove specified file for listening.
     *
     * @param file File to remove.
     */
    public void removeFile(File file) {
        files_.remove(file);
    }

    /**
     * Add listener to this file monitor.
     *
     * @param fileListener Listener to add.
     */
    public void addListener(FileListener fileListener) {
        // Don't add if its already there
        for (Iterator<WeakReference<FileListener>> i = listeners_.iterator(); i.hasNext();) {
            WeakReference<FileListener> reference = i.next();
            FileListener listener = reference.get();
            if (listener == fileListener) {
                return;
            }
        }

        // Use WeakReference to avoid memory leak if this becomes the
        // sole reference to the object.
        listeners_.add(new WeakReference(fileListener));
    }

    /**
     * Remove listener from this file monitor.
     *
     * @param fileListener Listener to remove.
     */
    public void removeListener(FileListener fileListener) {
        for (Iterator<WeakReference<FileListener>> i = listeners_.iterator(); i.hasNext();) {
            WeakReference<FileListener> reference = i.next();
            FileListener listener = reference.get();
            if (listener == fileListener) {
                i.remove();
                break;
            }
        }
    }

    /**
     * This is the timer thread which is executed every n milliseconds according
     * to the setting of the file monitor. It investigates the file in question
     * and notify listeners if changed.
     */
    private class FileMonitorNotifier extends TimerTask {

        public void run() {
            // Loop over the registered files and see which have changed.
            // Use a copy of the list in case listener wants to alter the
            // list within its fileChanged method.
            Collection<File> files = new ArrayList<File>(files_.keySet());

            for (Iterator i = files.iterator(); i.hasNext();) {
                File file = (File) i.next();
                long lastModifiedTime = files_.get(file);
                long newModifiedTime = file.exists() ? file.lastModified() : -1;

                // Chek if file has changed
                if (newModifiedTime != lastModifiedTime) {

                    // Register new modified time
                    files_.put(file, newModifiedTime);

                    // Notify listeners
                    for (Iterator<WeakReference<FileListener>> j = listeners_.iterator(); j.hasNext();) {
                        WeakReference<FileListener> reference = j.next();
                        FileListener listener = reference.get();

                        // Remove from list if the back-end object has been GC'd
                        if (listener == null) {
                            j.remove();
                        } else {
                            listener.fileChanged(file);
                        }
                    }
                }
            }
        }
    }

    /**
     * Interface for listening to disk file changes.
     *
     * @see FileMonitor
     *
     * @author <a href="mailto:info@geosoft.no">GeoSoft</a>
     */
    public interface FileListener {

        /**
         * Called when one of the monitored files are created, deleted or
         * modified.
         *
         * @param file File which has been changed.
         */
        void fileChanged(File file);
    }
}
