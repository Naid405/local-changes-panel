package isemenov.ide.ui.component;

import isemenov.ide.vcs.VCSFileStatus;

import java.nio.file.Path;

public class FileVCSStatus {
    private final Path file;
    private VCSFileStatus vcsFileStatus;
    private boolean edited;

    public FileVCSStatus(Path file, VCSFileStatus vcsFileStatus, boolean edited) {
        this.file = file;
        this.vcsFileStatus = vcsFileStatus;
        this.edited = edited;
    }

    public Path getFile() {
        return file;
    }

    public VCSFileStatus getVcsFileStatus() {
        return vcsFileStatus;
    }

    public void setVcsFileStatus(VCSFileStatus vcsFileStatus) {
        this.vcsFileStatus = vcsFileStatus;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    @Override
    public String toString() {
        return file.toAbsolutePath().toString() + "  " + vcsFileStatus.toString() + "  " + (edited ? "*" : "");
    }
}
