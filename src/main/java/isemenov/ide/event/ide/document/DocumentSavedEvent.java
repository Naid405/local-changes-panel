package isemenov.ide.event.ide.document;

import isemenov.ide.DocumentEditor;

import javax.swing.text.Document;
import java.nio.file.Path;

public class DocumentSavedEvent extends DocumentEditorEvent {
    public DocumentSavedEvent(Path file,
                              DocumentEditor documentEditor,
                              Document document) {
        super(file, documentEditor, document);
    }
}
