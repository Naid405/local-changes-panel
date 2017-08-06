package isemenov.ide.ui.component;

import isemenov.ide.vcs.VCSFileStatus;

import javax.swing.*;
import java.nio.file.Path;
import java.util.*;

/**
 * Container for files VCS statuses
 * Most of the methods are synchronized bc we are operating with indexes of arraylist instead of iterators
 */
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

    public void updateEditedStatusForFile(Path file, boolean edited) {
        if (this.getSize() == 0)
            return;
        for (int i = 0; i < this.getSize(); i++) {
            FileVCSStatus fileStatus = this.getElementAt(i);
            if (fileStatus.getFile().equals(file)) {
                fileStatus.setEdited(edited);
                this.fireContentsChanged(this, i, i);
            }
        }
    }

    public void addFiles(Map<Path, VCSFileStatus> statusMap) {
        if (statusMap.size() == 0)
            return;

        //Remove already present
        Map<Path, VCSFileStatus> map = new HashMap<>(statusMap);
        for (int i = 0; i < this.getSize(); i++) {
            FileVCSStatus fileStatus = this.getElementAt(i);
            map.remove(fileStatus.getFile());
        }

        int initial = this.getSize();
        for (Map.Entry<Path, VCSFileStatus> entry : map.entrySet()) {
            projectFileStatuses.add(new FileVCSStatus(entry.getKey(), entry.getValue(), false));
        }
        this.fireIntervalAdded(this, Math.max(initial, 0), this.getSize() - 1);
    }

    public void updateVCSStatusesForFiles(Map<Path, VCSFileStatus> statusMap) {
        if (this.getSize() == 0 || statusMap.size() == 0)
            return;

        for (int i = 0; i < this.getSize(); i++) {
            FileVCSStatus fileStatus = this.getElementAt(i);
            VCSFileStatus status = statusMap.get(fileStatus.getFile());
            if (status != null && !fileStatus.getVcsFileStatus().equals(status)) {
                fileStatus.setVcsFileStatus(status);
                this.fireContentsChanged(this, i, i);
            }
        }
    }

    public void removeFiles(Set<Path> removedFiles) {
        if (this.getSize() == 0 || removedFiles.size() == 0)
            return;

        for (int i = 0; i < this.getSize(); i++) {
            FileVCSStatus fileStatus = this.getElementAt(i);
            if (fileStatus != null && removedFiles.contains(fileStatus.getFile())) {
                projectFileStatuses.remove(i);
                this.fireIntervalRemoved(this, i, i);
            }
        }
    }
}
