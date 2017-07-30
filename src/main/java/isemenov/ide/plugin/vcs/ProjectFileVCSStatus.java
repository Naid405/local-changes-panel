package isemenov.ide.plugin.vcs;

import isemenov.ide.ProjectFile;

public class ProjectFileVCSStatus {
    private ProjectFile file;
    private VCSFileStatus vcsFileStatus;
    private boolean unsaved;

    public ProjectFileVCSStatus(ProjectFile file, VCSFileStatus vcsFileStatus, boolean unsaved) {
        this.file = file;
        this.vcsFileStatus = vcsFileStatus;
        this.unsaved = unsaved;
    }

    public ProjectFile getFile() {
        return file;
    }

    public VCSFileStatus getVcsFileStatus() {
        return vcsFileStatus;
    }

    public void setVcsFileStatus(VCSFileStatus vcsFileStatus) {
        this.vcsFileStatus = vcsFileStatus;
    }

    public void setUnsaved(boolean unsaved) {
        this.unsaved = unsaved;
    }

    @Override
    public String toString() {
        return file.getFilePath().toAbsolutePath().toString() + "  " + vcsFileStatus.toString() + "  " + (unsaved ? "*" : "");
    }
}
