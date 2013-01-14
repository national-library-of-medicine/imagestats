package gov.nih.nlm.ceb.lpf.imagestats.server;

import java.io.PrintStream;
import java.util.Set;

import javax.xml.namespace.QName;
//import javax.xml.soap.SOAPEnvelope;
//import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
//import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {
	// change this to redirect output if desired
	private static PrintStream out = System.out;

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
    //boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY); 
    boolean debug = Boolean.parseBoolean(System.getProperty("DEBUG"));
    
/*
        if (outbound) { 
            // OUTBOUND 

            SOAPMessage msg = ((SOAPMessageContext) smc).getMessage(); 

            // get SOAP-Part 
            SOAPPart sp = msg.getSOAPPart(); 

            //edit Envelope 
            try {
            SOAPEnvelope env = sp.getEnvelope(); 

            // add namespaces 
//            env.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema"); 
//            env.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance"); 
//            env.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");
            //env.addAttribute(new QName("SOAP-ENV:encodingStyle"), "http://schemas.xmlsoap.org/soap/encoding/");
//            env.setPrefix("SOAP-ENV");
//            env.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
           }
            catch(SOAPException e) {
            	e.printStackTrace();
            }
        }
        */
        if(debug) {
    		  logToSystemOut(smc);
        }
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		logToSystemOut(smc);
		return true;
	}

	// nothing to clean up
	public void close(MessageContext messageContext) {
	}

	/*
	 * Check the MESSAGE_OUTBOUND_PROPERTY in the context to see if this is an
	 * outgoing or incoming message. Write a brief message to the print stream and
	 * output the message. The writeTo() method can throw SOAPException or
	 * IOException
	 */
	private void logToSystemOut(SOAPMessageContext smc) {
		Boolean outboundProperty = (Boolean) smc
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outboundProperty.booleanValue()) {
			out.println("\nOutbound message:");
		} else {
			out.println("\nInbound message:");
		}

		SOAPMessage message = smc.getMessage();
		try {
			message.writeTo(out);
			out.println(""); // just to add a newline
		} catch (Exception e) {
			out.println("Exception in handler: " + e);
		}
	}

}
