package isemenov.ide.event.editor;

import isemenov.ide.DocumentEditor;
import isemenov.ide.ProjectFile;

public class EditorFileChangedEvent extends EditorFileEvent {
    public EditorFileChangedEvent(ProjectFile projectFile, DocumentEditor documentEditor) {
        super(projectFile, documentEditor);
    }
}
