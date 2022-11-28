package webservices.ws;

import ge.ufc.webservices.DatabaseException;
import ge.ufc.webservices.DatabaseManager;
import ge.ufc.webservices.exception.PersonNotFound;
import ge.ufc.webservices.repository.PersonRepository;
import ge.ufc.webservices.repository.PersonRepositoryImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jws.WebService;
import java.sql.Connection;

@WebService(endpointInterface = "ge.ufc.webservices.ws.PersonServiceWS")
public class PersonService implements PersonServiceWS {

    private Connection connection;
    private PersonRepository personRepository;
    private static final Logger logger = LogManager.getLogger();
    private PersonRepository personRepositoryImpl = new PersonRepositoryImpl(connection);

    @Override
    public String Check(int id) throws PersonNotFound {
        try {
            connection = DatabaseManager.getDatabaseConnection();
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
        logger.info("Close connection");
        DatabaseManager.close(connection);
        personRepository = new PersonRepositoryImpl(connection);
        return personRepositoryImpl.Check(id);
    }


    @Override
    public long Pay(int id, double amount) {
        try {
            connection = DatabaseManager.getDatabaseConnection();
            personRepository = new PersonRepositoryImpl(connection);
            return personRepositoryImpl.Pay(id,amount);
        }
        catch (PersonNotFound | DatabaseException e) {
            throw new RuntimeException(e);
        }

    }


}
