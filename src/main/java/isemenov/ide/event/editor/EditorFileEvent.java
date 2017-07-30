package isemenov.ide.event.editor;

import isemenov.ide.DocumentEditor;
import isemenov.ide.ProjectFile;
import isemenov.ide.event.ide.IDEEvent;

public abstract class EditorFileEvent extends IDEEvent {
    private final ProjectFile projectFile;
    private final DocumentEditor documentEditor;

    public EditorFileEvent(ProjectFile projectFile, DocumentEditor documentEditor) {
        this.projectFile = projectFile;
        this.documentEditor = documentEditor;
    }

    public ProjectFile getProjectFile() {
        return projectFile;
    }

    public DocumentEditor getDocumentEditor() {
        return documentEditor;
    }
}
