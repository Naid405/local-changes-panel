package isemenov.ide.event.editor;

import isemenov.ide.DocumentEditor;
import isemenov.ide.ProjectFile;

public class EditorFileOpenedEvent extends EditorFileEvent {
    public EditorFileOpenedEvent(ProjectFile projectFile, DocumentEditor documentEditor) {
        super(projectFile, documentEditor);
    }
}
