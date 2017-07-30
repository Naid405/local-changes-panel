package isemenov.ide.event.editor;

import isemenov.ide.DocumentEditor;
import isemenov.ide.ProjectFile;

public class EditorFileSavedEvent extends EditorFileEvent {
    public EditorFileSavedEvent(ProjectFile projectFile, DocumentEditor documentEditor) {
        super(projectFile, documentEditor);
    }
}
