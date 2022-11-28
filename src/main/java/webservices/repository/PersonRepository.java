package webservices.repository;

import ge.ufc.webservices.exception.PersonNotFound;

public interface PersonRepository {

    String Check(int id) throws PersonNotFound;

    long Pay(int id, double amount) throws PersonNotFound;

}
