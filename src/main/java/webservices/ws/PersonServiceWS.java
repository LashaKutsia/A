package webservices.ws;

import ge.ufc.webservices.exception.PersonNotFound;

import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@HandlerChain(file = "/handlers.xml")
public interface PersonServiceWS {

    @WebMethod(operationName = "Check")
    @WebResult(name = "CheckResult")
    String Check(@WebParam(name = "PersonId") int id) throws PersonNotFound;

    @WebMethod(operationName = "Pay")
    @WebResult(name = "Transaction_id")
    long Pay(@WebParam(name = "user_id") int userId,@WebParam(name = "amount") double amount) throws PersonNotFound;


}
