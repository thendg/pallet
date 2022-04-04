package models.note;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import helpers.GUI;
import helpers.IO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class NoteView {
    private static Consumer<String> onError;

    private Note note;
    private String path;
    private Consumer<Integer> onDelete;

    @FXML
    private Label title;
    @FXML
    private Label para;
    @FXML
    private Label time;
    @FXML
    private ImageView deleteBtn;

    public void setNote(Note note, String path, Consumer<Integer> onDelete) {
        try {
            this.path = String.format("%s/%s.json", path, note);
            this.note = note;
            this.onDelete = onDelete;
            IO.writeJSON(note, this.path);
            populate();
        } catch (IOException e) {
            onError.accept(e.getMessage());
        }

    }

    public void setNote(String path, Consumer<Integer> onDelete) {
        try {
            this.path = path;
            note = IO.readJSON(path, Note.class);
            this.onDelete = onDelete;
            populate();
        } catch (IOException e) {
            onError.accept(e.getMessage());
        }
    }

    private void populate() {
        title.setText(note.title());
        para.setText(note.para());
        time.setText(new SimpleDateFormat("HH:mm").format(note.lastEdited()));
        if (note.isOwned()) {
            GUI.decorateBtn(deleteBtn, (event) -> delete());
        } else
            deleteBtn.setVisible(false);
    }

    private void delete() {
        File outfile = new File(path);
        if (outfile.exists())
            outfile.delete();
        onDelete.accept(note.hashCode());
    }

    public static void setOnError(Consumer<String> onError) {
        NoteView.onError = onError;
    }
}