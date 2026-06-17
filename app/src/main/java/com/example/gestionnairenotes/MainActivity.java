package com.example.gestionnairenotes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteInteractionListener{

    // Déclaration des views
    private RecyclerView recyclerView;
    private EditText etSearch;
    private Button btnFavoris;
    private FloatingActionButton fabAdd;
    private LinearLayout colorPaletteLayout;
    private TextView tvEmpty;

    private TextView tvNoteCount;
    private NoteAdapter adapter;

    private NoteDao noteDao;

    private LiveData<List<Note>> currentSource;

    private boolean showFavoritesOnly = false;
    private boolean isPaletteOpen = false;

    // Palette de couleurs
    private static final String[] PALETTE_COLORS = {
            "#219653",  // Vert
            "#EB5757",  // Rouge
            "#2F80ED",  // Bleu
            "#F2C94C",  // Jaune
            "#F2994A",  // Orange
            "#828282"   // Gris
    };

    private enum SortType { NEWEST, OLDEST, ALPHABETICAL }
    private SortType currentSort = SortType.NEWEST;
    private Button btnSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Evite que les barres systeme (statut en haut, navigation en bas) cachent le contenu
        setupSystemBars();

        // On recupere l'instance de connection a la base de données
        noteDao = AppDatabase.getInstance(this).noteDao();

        initViews();

        setupRecyclerView();

        setupSearch();
        setupFavorisButton();
        setupFAB();

        setupSortButton();
    }

    // Reserve la place des barres systeme et garde des icones de statut lisibles sur fond clair.
    private void setupSystemBars() {
        View root = findViewById(R.id.mainRoot);
        int base = (int) (12 * getResources().getDisplayMetrics().density);

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

    //Initialise les références aux vues du layout.
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        etSearch = findViewById(R.id.etSearch);
        btnFavoris = findViewById(R.id.btnFavoris);
        fabAdd = findViewById(R.id.fabAdd);
        colorPaletteLayout = findViewById(R.id.colorPaletteLayout);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvNoteCount = findViewById(R.id.tvNoteCount);
        btnSort = findViewById(R.id.btnSort);
    }


    // Recherche en temps réel : filtre la liste à chaque frappe.
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadNotes();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }


     // Bouton Favoris : bascule le filtre + met à jour l'apparence du bouton.
    private void setupFavorisButton() {
        btnFavoris.setOnClickListener(v -> {
            showFavoritesOnly = !showFavoritesOnly;

            if (showFavoritesOnly) {
                // Etat actif : fond noir, texte blanc
                btnFavoris.setBackgroundColor(Color.BLACK);
                btnFavoris.setTextColor(Color.WHITE);
            } else {
                // Etat inactif : transparent, texte noir
                btnFavoris.setBackgroundColor(Color.TRANSPARENT);
                btnFavoris.setTextColor(Color.BLACK);
            }

            loadNotes();
        });
    }


    // FAB + palette de couleurs.
    private void setupFAB() {
        // Toggle affichage de la palette au clic sur le FAB
        fabAdd.setOnClickListener(v -> {
            isPaletteOpen = !isPaletteOpen;
            colorPaletteLayout.setVisibility(isPaletteOpen ? View.VISIBLE : View.GONE);
        });

        // IDs des pastilles dans le même ordre que PALETTE_COLORS[]
        int[] dotIds = {
                R.id.colorGreen,
                R.id.colorRed,
                R.id.colorBlue,
                R.id.colorYellow,
                R.id.colorOrange,
                R.id.colorGray
        };

        for (int i = 0; i < dotIds.length; i++) {
            final String color = PALETTE_COLORS[i];
            findViewById(dotIds[i]).setOnClickListener(v -> {
                // Fermer la palette avant de naviguer
                colorPaletteLayout.setVisibility(View.GONE);
                isPaletteOpen = false;
                navigateToCreate(color);
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Toujours fermer la palette au retour sur cet écran
        isPaletteOpen = false;
        colorPaletteLayout.setVisibility(View.GONE);

        loadNotes();
    }

    private void loadNotes() {
        String query = etSearch.getText().toString().trim();

        if(currentSource != null){
            currentSource.removeObservers(this);
        }

         if (showFavoritesOnly) {
             currentSource =  noteDao.getFavorites();
         } else if (!query.isEmpty()) {
             currentSource = noteDao.searchByTitle(query);
         } else {
             switch (currentSort) {
                 case OLDEST:
                     currentSource = noteDao.getAllNotesOldest();
                     break;
                 case ALPHABETICAL:
                     currentSource = noteDao.getAllNotesByTitle();
                     break;
                 default: // NEWEST
                     currentSource = noteDao.getAllNotes();
             }
         }

         currentSource.observe(this, notes -> updateUI(notes));
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
    }

    private void navigateToCreate(String color) {
         Intent intent = new Intent(this, NoteFormActivity.class);
         intent.putExtra(NoteFormActivity.EXTRA_COLOR, color);
         startActivity(intent);
    }


     private void updateUI(List<Note> notes) {
         adapter.updateList(notes);
         boolean isEmpty = notes.isEmpty();
         tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
         recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
         tvNoteCount.setText(getString(R.string.note_counter, notes.size()));
     }

     @Override
     public void onNoteClick(Note note) {
         Intent intent = new Intent(this, NoteFormActivity.class);
         intent.putExtra(NoteFormActivity.EXTRA_NOTE_ID, note.getId());
         intent.putExtra(NoteFormActivity.EXTRA_COLOR, note.getColor());
         startActivity(intent);
     }


     @Override
     public void onNoteDoubleClick(Note note) {
         boolean nouvelEtat = !note.isFavorite();
         noteDao.toggleFavorite(note.getId(), nouvelEtat);
         Toast.makeText(this,
                 nouvelEtat ? "Ajoutée aux favoris" : "Retirée des favoris",
                 Toast.LENGTH_SHORT).show();
     }

    @Override
    public void onNoteLongClick(Note note) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_title)
                .setMessage(R.string.delete_message)
                .setPositiveButton(R.string.delete_confirm, (dialog, which) -> {
                    noteDao.delete(note);
                    Toast.makeText(this,
                            R.string.delete_done,
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.delete_cancel, null)
                .show();
    }

    private void setupSortButton() {
        btnSort.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, btnSort);
            popup.getMenuInflater().inflate(R.menu.sort_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.sort_newest) {
                    currentSort = SortType.NEWEST;
                } else if (id == R.id.sort_oldest) {
                    currentSort = SortType.OLDEST;
                } else if (id == R.id.sort_alpha) {
                    currentSort = SortType.ALPHABETICAL;
                }
                loadNotes(); // recharge avec le nouveau tri
                return true;
            });
            popup.show();
        });
    }
}