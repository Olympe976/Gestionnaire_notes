package com.example.gestionnairenotes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteFormActivity extends AppCompatActivity {

    public static final String EXTRA_COLOR = "extra_color";
    public static final String EXTRA_NOTE_ID = "extra_note_id";

    private EditText etTitle, etContent;
    private Button btnSave;
    private LinearLayout formContainer;
    private NoteDao noteDao;

    private String color;
    private int noteId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_form);

        initViews();
        readIntentAndSetupMode();
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSave);
        formContainer = findViewById(R.id.formContainer);
        noteDao = AppDatabase.getInstance(this).noteDao();
    }

    private void readIntentAndSetupMode() {
        Intent intent = getIntent();
        color = intent.getStringExtra(EXTRA_COLOR);
        noteId = intent.getIntExtra(EXTRA_NOTE_ID, -1);

        if (color != null) {
            formContainer.setBackgroundColor(Color.parseColor(color));
        }

        if (noteId != -1) {
            isEditMode = true;
            btnSave.setText("Modifier");
            loadNoteData(noteId);
        } else {
            isEditMode = false;
            btnSave.setText("Créer");
        }

        btnSave.setOnClickListener(v -> saveNote());
    }

    private void loadNoteData(int id) {
        Note note = noteDao.getById(id);
        if (note != null) {
            etTitle.setText(note.getTitle());
            etContent.setText(note.getContent());
        }
    }

    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Le titre est obligatoire");
            etTitle.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            etContent.setError("Le contenu est obligatoire");
            etContent.requestFocus();
            return;
        }

        String date = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH).format(new Date());

        Note note = new Note(title, content, color, false, date);

        if (isEditMode) {
            note.setId(noteId);
            noteDao.update(note);
        } else {
            noteDao.insert(note);
        }

        finish();
    }
}