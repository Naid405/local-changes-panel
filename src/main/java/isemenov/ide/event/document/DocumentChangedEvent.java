package isemenov.ide.event.document;

import isemenov.ide.DocumentEditor;

import javax.swing.text.Document;
import java.nio.file.Path;

public class DocumentChangedEvent extends DocumentEditorEvent {
    public DocumentChangedEvent(Path file,
                                DocumentEditor documentEditor,
                                Document document) {
        super(file, documentEditor, document);
    }
}
