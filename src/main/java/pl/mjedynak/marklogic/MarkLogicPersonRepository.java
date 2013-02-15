package pl.mjedynak.marklogic;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

public class MarkLogicPersonRepository implements PersonRepository {

    private XMLDocumentManager documentManager;
    private QueryManager queryManager;

    public MarkLogicPersonRepository(XMLDocumentManager documentManager, QueryManager queryManager) {
        this.documentManager = documentManager;
        this.queryManager = queryManager;
    }

    public void addPerson(String id, String person) {
        StringHandle handle = new StringHandle(person);
        documentManager.write(id, handle);
    }

    public String getPerson(String personId) {
        StringHandle handle = new StringHandle();
        documentManager.read(personId, handle);
        return handle.get();
    }

    public void removePerson(String personId) {
        documentManager.delete(personId);
    }

    public List<String> findByName(String name) {
        KeyValueQueryDefinition query = queryManager.newKeyValueDefinition();
        queryManager.setPageLength(10);  // LIMIT RESULT
        query.put(queryManager.newElementLocator(new QName("name")), name);
        SearchHandle resultsHandle = new SearchHandle();
        queryManager.search(query, resultsHandle);
        return getResultListFor(resultsHandle);
    }

    private List<String> getResultListFor(SearchHandle resultsHandle) {
        List<String> result = new ArrayList<String>();
        for (MatchDocumentSummary summary : resultsHandle.getMatchResults()) {
            StringHandle content = new StringHandle();
            documentManager.read(summary.getUri(), content);
            result.add(content.get());
        }
        return result;
    }
}
