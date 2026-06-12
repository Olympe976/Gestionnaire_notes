package com.example.gestionnairenotes;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("UPDATE notes SET isFavorite = :newState WHERE id = :id")
    void toggleFavorite(int id, boolean newState);

    // les plus recentes d'abord
    @Query("SELECT * FROM notes ORDER BY id DESC")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM notes WHERE isFavorite = 1 ORDER BY id DESC")
    LiveData<List<Note>> getFavorites();

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :search || '%' ORDER BY id DESC")
    LiveData<List<Note>> searchByTitle(String search);

    // pour pre-remplir le formulaire de modification
    @Query("SELECT * FROM notes WHERE id = :id")
    Note getById(int id);
}
