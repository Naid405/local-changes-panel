package isemenov.ide.ui.component;

import isemenov.ide.FileEditor;
import isemenov.ide.FileReadingException;
import isemenov.ide.FileSavingException;
import isemenov.ide.ui.ErrorHandlerUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileEditorTab extends JPanel {
    private final Path filePath;
    private final CloseableChangeDisplayingTab tabHeader;

    private final JTextPane textPane;
    private final OneTimeNotifyingDocumentEditsObserver documentEditsObserver;
    private final ActionListener saveFileActionListener;

    public FileEditorTab(FileEditor editor,
                         ActionListener closeTabListener) {
        super(new BorderLayout());
        this.filePath = editor.getFilePath();

        textPane = new JTextPane();
        textPane.setEditable(true);

        tabHeader = new CloseableChangeDisplayingTab(filePath.getFileName().toString());
        tabHeader.addCrossButtonActionListener(closeTabListener);

        documentEditsObserver = new OneTimeNotifyingDocumentEditsObserver(edited -> {
            if (edited) {
                tabHeader.setChanged();
            } else {
                tabHeader.setSaved();
            }
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    editor.setEditedState(edited);
                    return null;
                }
            }.execute();
        });

        saveFileActionListener = e -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    saveFile();
                } catch (FileSavingException ex) {
                    ErrorHandlerUI.showError(ex);
                }

                return null;
            }
        }.execute();

        textPane.registerKeyboardAction(saveFileActionListener, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK),
                                        WHEN_FOCUSED);

        this.add(new JScrollPane(textPane), BorderLayout.CENTER);
    }

    public Path getFilePath() {
        return filePath;
    }

    public void readFile() throws FileReadingException {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            textPane.read(reader, filePath);
            documentEditsObserver.reset();
            textPane.getDocument().addDocumentListener(documentEditsObserver);
        } catch (IOException e) {
            throw new FileReadingException(filePath, e);
        }
    }

    public void saveFile() throws FileSavingException {
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            textPane.write(writer);
            documentEditsObserver.reset();
        } catch (IOException e) {
            throw new FileSavingException(filePath, e);
        }
    }

    public JComponent getTabHeader() {
        return tabHeader;
    }

    public ActionListener getSaveFileAction() {
        return saveFileActionListener;
    }
}
