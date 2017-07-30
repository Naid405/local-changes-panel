package isemenov.ide.event.editor;

import isemenov.ide.DocumentEditor;
import isemenov.ide.ProjectFile;

public class EditorFileClosedEvent extends EditorFileEvent {
    public EditorFileClosedEvent(ProjectFile projectFile, DocumentEditor documentEditor) {
        super(projectFile, documentEditor);
    }
}
