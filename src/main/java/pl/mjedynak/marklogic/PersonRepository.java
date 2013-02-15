package pl.mjedynak.marklogic;

import java.util.List;

public interface PersonRepository {

    void addPerson(String id, String person);

    String getPerson(String id);

    void removePerson(String id);

    List<String> findByName(String name);
}
