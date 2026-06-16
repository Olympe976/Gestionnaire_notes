package com.example.gestionnairenotes;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
    private LinearLayout colorEditPalette;
    private NoteDao noteDao;

    private String color = "#219653";
    private int noteId = -1;
    private boolean isEditMode = false;
    private boolean currentFavorite = false;
    private String currentCreatedAt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_form);

        setupSystemBars();
        initViews();
        readIntentAndSetupMode();
    }

    private void setupSystemBars() {
        View root = findViewById(R.id.formRoot);
        int base = (int) (16 * getResources().getDisplayMetrics().density);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(base + bars.left, base + bars.top, base + bars.right, base + bars.bottom);
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
        colorEditPalette = findViewById(R.id.colorEditPalette);
        noteDao = AppDatabase.getInstance(this).noteDao();
    }

    private void readIntentAndSetupMode() {
        Intent intent = getIntent();
        String colorExtra = intent.getStringExtra(EXTRA_COLOR);
        if (colorExtra != null) {
            color = colorExtra;
        }
        noteId = intent.getIntExtra(EXTRA_NOTE_ID, -1);

        applyFormColor(color);
        setupColorEditPalette();

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
            currentFavorite = note.isFavorite();
            currentCreatedAt = note.getCreatedAt();
        }
    }

    private void setupColorEditPalette() {
        int[] ids = {R.id.colorEdit1, R.id.colorEdit2, R.id.colorEdit3,
                R.id.colorEdit4, R.id.colorEdit5, R.id.colorEdit6};
        String[] palette = {"#219653", "#EB5757", "#2F80ED", "#F2C94C", "#F2994A", "#828282"};

        for (int i = 0; i < ids.length; i++) {
            final String chosen = palette[i];
            findViewById(ids[i]).setOnClickListener(v -> applyFormColor(chosen));
        }
    }

    // Applique la couleur sur la carte en gardant les coins arrondis.
    private void applyFormColor(String hex) {
        color = hex;
        float radius = 18 * getResources().getDisplayMetrics().density;
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(hex));
        bg.setCornerRadius(radius);
        formContainer.setBackground(bg);
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
