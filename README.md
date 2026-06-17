# Gestionnaire de Notes

Application Android de gestion de notes personnelles avec couleurs et favoris.

## Équipe

| Membre | Rôle |
|--------|------|
| Olympe | Chef de dépôt + MainActivity |
| Falilou | Base de données (Room) |
| Aminata | Liste des notes (RecyclerView) |
| Mariama | Écran Ajout / Édition |
| Mahmoud | Layouts XML + Design + README |

## Technologies

- Java
- Android SDK
- Room (base de données locale)
- RecyclerView
- LiveData

## Fonctionnalités

- Créer, modifier et supprimer des notes
- Choisir une couleur pour chaque note
- Marquer une note en favori
- Rechercher une note par titre
- Filtrer les notes favorites
- Trier les notes (récentes, anciennes, alphabétique)
- Partager une note vers une autre application
- Mode sombre

## Utilisation

### Écran principal

| Action | Effet |
|--------|-------|
| Taper dans la barre de recherche | Filtre la liste en temps réel par titre |
| Bouton **Favoris** | Affiche uniquement les notes favorites |
| Bouton **Trier** | Ouvre un menu : *Plus récentes*, *Plus anciennes*, *Alphabétique* |
| Bouton **+** (FAB) | Ouvre la palette de 6 couleurs pour créer une note |
| Choisir une couleur dans la palette | Ouvre le formulaire de création avec la couleur choisie |
| Icône **soleil / lune** | Bascule entre le mode clair et le mode sombre |
| **Clic simple** sur une note | Ouvre le formulaire d'édition |
| **Double clic** sur une note | Ajoute ou retire la note des favoris |
| **Clic long** sur une note | Demande confirmation puis supprime la note |

Un compteur sous la barre de recherche indique en permanence le nombre de notes affichées.

### Écran de création / édition

| Action | Effet |
|--------|-------|
| Champ **Titre** | Saisie du titre (obligatoire) |
| Champ **Contenu** | Saisie du contenu sur plusieurs lignes (obligatoire) |
| Palette d'édition *(édition seulement)* | Change la couleur de la note en temps réel |
| Bouton **Partager** *(édition seulement)* | Envoie la note via WhatsApp, mail, SMS, etc. |
| Bouton **Créer** / **Modifier** | Enregistre la note et revient à la liste |

Une note ne peut pas être enregistrée si le titre ou le contenu est vide.

### Persistance

Toutes les notes sont sauvegardées localement et restent disponibles après fermeture ou redémarrage de l'application. Le mode sombre choisi est également conservé.

## Lancement

1. Cloner le dépôt
2. Ouvrir le projet dans Android Studio
3. Lancer sur un émulateur ou un téléphone Android
