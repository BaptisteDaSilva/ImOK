package uqac.inf872.projet.imok.base.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import uqac.inf872.projet.imok.models.Personne;

@Dao
public interface PersonneDAO {

    @Query("SELECT * FROM Personne")
    LiveData<List<Personne>> getPersonnes();

    @Query("SELECT * FROM Personne WHERE id = :personneId")
    LiveData<Personne> getPersonne(long personneId);

    @Insert
    long createPersonne(Personne personne);

    @Update
    int updatePersonne(Personne personne);

    @Delete
    int deletePersonne(Personne personne);
}
