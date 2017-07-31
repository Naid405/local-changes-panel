package isemenov.ide.plugin.vcs;

import javax.swing.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*Most of the methods are synchronized bc we are operating with indexes of arraylist instead of iterators
* */
public class VSCFileStatusesList extends AbstractListModel<FileVCSStatus> {
    private final List<FileVCSStatus> projectFileStatuses;

    public VSCFileStatusesList() {
        this.projectFileStatuses = new ArrayList<>();
    }

    @Override
    public int getSize() {
        return projectFileStatuses.size();
    }

    @Override
    public FileVCSStatus getElementAt(int index) {
        return projectFileStatuses.get(index);
    }

    synchronized void updateUnsavedStatusForFile(Path file, boolean unsaved) {
        if (this.getSize() == 0)
            return;
        for (int i = 0; i < this.getSize(); i++) {
            FileVCSStatus fileStatus = this.getElementAt(i);
            if (fileStatus.getFile().equals(file)) {
                fileStatus.setUnsaved(unsaved);
                this.fireContentsChanged(this, i, i);
            }
        }
    }

    synchronized void updateVCSStatusForFile(Path file, VCSFileStatus status) {
        if (this.getSize() == 0)
            return;
        for (int i = 0; i < this.getSize(); i++) {
            FileVCSStatus fileStatus = this.getElementAt(i);
            if (fileStatus.getFile().equals(file)) {
                fileStatus.setVcsFileStatus(status);
                this.fireContentsChanged(this, i, i);
            }
        }
    }

    synchronized void updateVCSStatusesForPaths(Map<Path, VCSFileStatus> statusMap) {
        if (this.getSize() == 0)
            return;
        for (int i = 0; i < this.getSize(); i++) {
            FileVCSStatus fileStatus = this.getElementAt(i);
            Path path = fileStatus.getFile();
            VCSFileStatus newStatus = statusMap.get(path);
            //To get status for files under ignored directories
            if (newStatus == null) {
                newStatus = VCSFileStatus.UNCHANGED;
            }


            if (!fileStatus.getVcsFileStatus().equals(newStatus)) {
                fileStatus.setVcsFileStatus(newStatus);
                this.fireContentsChanged(this, i, i);
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    synchronized void addProjectFileStatus(Path file, VCSFileStatus status, boolean unsaved) {
        for (FileVCSStatus fileStatus : projectFileStatuses) {
            if (fileStatus.getFile().equals(file))
                return;
        }
        projectFileStatuses.add(new FileVCSStatus(file, status, unsaved));
        this.fireContentsChanged(this, this.getSize() - 1, this.getSize() - 1);
    }

    synchronized void clear() {
        projectFileStatuses.clear();
        this.fireContentsChanged(this, 0, this.getSize() - 1);
    }
}
