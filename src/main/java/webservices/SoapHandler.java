package webservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;


public class SoapHandler implements SOAPHandler<SOAPMessageContext> {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        SOAPMessage message = context.getMessage();
        boolean isResponse = ((Boolean) context.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();

        try {
            String msg = soapMessageToString(message);
            if (isResponse) {
                logger.info("SOAP response returned: \n{}", msg);
            } else {
                logger.info("SOAP request received: \n{}", msg);
            }
        } catch (SOAPException | IOException e) {
            logger.error(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        handleMessage(context);
        return true;
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public void close(MessageContext arg0) {

    }

    private String soapMessageToString(SOAPMessage message) throws SOAPException, IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            message.writeTo(out);
            return getPrettyPrintXml(out.toString());
        }
    }

    private String getPrettyPrintXml(String xmlString) {
        try {
            String XML_LINARIZATION_REGEX = "(>|&gt;){1,1}(\\t)*(\\n|\\r)+(\\s)*(<|&lt;){1,1}";
            String XML_LINARIZATION_REPLACEMENT = "$1$5";

            xmlString = xmlString.replaceAll(XML_LINARIZATION_REGEX, XML_LINARIZATION_REPLACEMENT);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document d = builder.parse(new InputSource(new StringReader(xmlString)));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 2);

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(d);
            transformer.transform(source, result);
            String resultString = result.getWriter().toString();

            return resultString;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return xmlString;
        }
    }
}