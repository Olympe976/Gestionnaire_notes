package com.example.gestionnairenotes;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    // Déclaration des views
    private RecyclerView recyclerView;
    private EditText etSearch;
    private Button btnFavoris;
    private ImageButton fabAdd;
    private LinearLayout colorPaletteLayout;
    private TextView tvEmpty;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ⏳ ATTEND FALILOU
        // noteDao = AppDatabase.getInstance(this).noteDao();

        initViews();

        // ⏳ ATTEND AMINATA
        // setupRecyclerView();

        setupSearch();
        setupFavorisButton();
        setupFAB();
    }

    //Initialise les références aux vues du layout.
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        etSearch = findViewById(R.id.etSearch);
        btnFavoris = findViewById(R.id.btnFavoris);
        fabAdd = findViewById(R.id.fabAdd);
        colorPaletteLayout = findViewById(R.id.colorPaletteLayout);
        tvEmpty = findViewById(R.id.tvEmpty);
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
                R.id.dotGreen,
                R.id.dotRed,
                R.id.dotBlue,
                R.id.dotYellow,
                R.id.dotOrange,
                R.id.dotGray
        };

        for (int i = 0; i < dotIds.length; i++) {
            final String color = PALETTE_COLORS[i];
            findViewById(dotIds[i]).setOnClickListener(v -> {
                // Fermer la palette avant de naviguer
                colorPaletteLayout.setVisibility(View.GONE);
                isPaletteOpen = false;
                // ⏳ ATTEND MARIAMA - NoteFormActivity
                // navigateToCreate(color);
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Toujours fermer la palette au retour sur cet écran
        isPaletteOpen = false;
        colorPaletteLayout.setVisibility(View.GONE);

        // ⏳ ATTEND FALILOU - NoteDao
        // loadNotes();
    }

    private void loadNotes() {
        // ⏳ ATTEND FALILOU
    }

    private void setupRecyclerView() {
        // ⏳ ATTEND AMINATA
    }

    private void navigateToCreate(String color) {
        // ⏳ ATTEND MARIAMA
    }
}