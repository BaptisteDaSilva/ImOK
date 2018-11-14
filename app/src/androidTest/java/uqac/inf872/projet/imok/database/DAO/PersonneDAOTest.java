package uqac.inf872.projet.imok.database.DAO;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import uqac.inf872.projet.imok.database.ImOKDatabase;
import uqac.inf872.projet.imok.models.Personne;
import uqac.inf872.projet.imok.utils.LiveDataTestUtil;

import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PersonneDAOTest {

    // DATA SET FOR TEST
    private static Personne PERSONNE_DEMO = new Personne("RIBEIRO DA SILVA", "Baptiste");

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // FOR DATA
    private ImOKDatabase database;

    @Before
    public void initDb() {
        this.database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                ImOKDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() {
        database.close();
    }

    @Test
    public void insertAndGetPersonne() throws InterruptedException {
        // BEFORE : Adding a new user
        long personneID = this.database.personneDao().createPersonne(PERSONNE_DEMO);
        // TEST
        Personne personne = LiveDataTestUtil.getValue(this.database.personneDao().getPersonne(personneID));
        assertTrue(personne.getPrenom().equals(PERSONNE_DEMO.getPrenom())
                && personne.getNom().equals(PERSONNE_DEMO.getNom())
                && personne.getId() == personneID);
    }
}
