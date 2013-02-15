package pl.mjedynak.marklogic;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.marklogic.client.DatabaseClientFactory.Authentication.DIGEST;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.xmlmatchers.XmlMatchers.isEquivalentTo;
import static org.xmlmatchers.transform.XmlConverters.the;

public class MarkLogicPersonRepositoryIntegrationTest {

    private static final String NAME = "Robin van Persie";
    private static final String SAMPLE_PERSON = "<person><name>" + NAME + "</name><age>29</age></person>";

    private MarkLogicPersonRepository personRepository;

    @Before
    public void setUp() {
        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8003, "rest-writer", "x", DIGEST);
        personRepository = new MarkLogicPersonRepository(client.newXMLDocumentManager(), client.newQueryManager());
    }

    @Test
    public void shouldAddAndRetrievePersonAsXmlDocument() {
        // given
        String personId = randomUUID().toString();
        personRepository.addPerson(personId, SAMPLE_PERSON);
        // when
        String result = personRepository.getPerson(personId);
        // then
        assertThat(the(result), isEquivalentTo(the(SAMPLE_PERSON)));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldRemovePerson() {
        // given
        String personId = randomUUID().toString();
        personRepository.addPerson(personId, SAMPLE_PERSON);
        // when
        personRepository.removePerson(personId);
        // then
        personRepository.getPerson(personId);
    }

    @Test
    public void shouldFindPersonByName() {
        // given
        personRepository.addPerson(randomUUID().toString(), SAMPLE_PERSON);
        // when
        List<String> result = personRepository.findByName(NAME);
        // then
        assertThat(the(result.get(0)), isEquivalentTo(the(SAMPLE_PERSON)));
    }
}
