package com.example.gestionnairenotes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

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
    private boolean currentFavorite = false;

    private LinearLayout colorEditPalette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_form);

        setupSystemBars();
        initViews();
        readIntentAndSetupMode();
    }

    // Reserve la place des barres systeme pour que le bouton du bas reste accessible.
    private void setupSystemBars() {
        View root = findViewById(R.id.formRoot);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSave);
        formContainer = findViewById(R.id.formContainer);
        noteDao = AppDatabase.getInstance(this).noteDao();
        colorEditPalette = findViewById(R.id.colorEditPalette);
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
            colorEditPalette.setVisibility(android.view.View.VISIBLE);
            setupColorEditPalette();
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
            currentFavorite = note.isFavorite();
            currentCreatedAt = note.getCreatedAt();
        }
    }

    private void setupColorEditPalette() {
        int[] ids = {R.id.colorEdit1, R.id.colorEdit2, R.id.colorEdit3,
                R.id.colorEdit4, R.id.colorEdit5, R.id.colorEdit6};
        String[] colors = {"#219653", "#EB5757", "#2F80ED", "#F2C94C", "#F2994A", "#828282"};

        for (int i = 0; i < ids.length; i++) {
            final String selectedColor = colors[i];
            findViewById(ids[i]).setOnClickListener(v -> {
                color = selectedColor;
                formContainer.setBackgroundColor(Color.parseColor(color));
            });
        }
    }

    private String currentCreatedAt = "";
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

        String date;
        if (isEditMode) {
            date = currentCreatedAt;
        } else {
            date = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH).format(new Date());
            String[] parts = date.split(" ");
            parts[1] = parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1);
            date = parts[0] + " " + parts[1] + " " + parts[2];
        }
        Note note = new Note(title, content, color, currentFavorite, date);

        if (isEditMode) {
            note.setId(noteId);
            noteDao.update(note);
        } else {
            noteDao.insert(note);
        }

        finish();
    }
}