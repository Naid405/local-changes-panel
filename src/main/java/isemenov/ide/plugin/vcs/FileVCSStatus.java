package isemenov.ide.plugin.vcs;

import java.nio.file.Path;

public class FileVCSStatus {
    private Path file;
    private VCSFileStatus vcsFileStatus;
    private boolean unsaved;

    public FileVCSStatus(Path file, VCSFileStatus vcsFileStatus, boolean unsaved) {
        this.file = file;
        this.vcsFileStatus = vcsFileStatus;
        this.unsaved = unsaved;
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

    public void setUnsaved(boolean unsaved) {
        this.unsaved = unsaved;
    }

    @Override
    public String toString() {
        return file.toAbsolutePath().toString() + "  " + vcsFileStatus.toString() + "  " + (unsaved ? "*" : "");
    }
}
