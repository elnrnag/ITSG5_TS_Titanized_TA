/**
 *  Main Test Adapter class. Implements TRI API
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/TestAdapter.java $
 *              $Id: TestAdapter.java 2230 2015-06-03 09:11:02Z mullers $
 *  @see        "http://t-ort.etsi.org/view_all_bug_page.php?page_number=1" 
 */
package org.etsi.its.adapter;

import java.util.Observable;
import java.util.Observer;

import org.etsi.adapter.ITERequired;
import org.etsi.adapter.TERFactory;
import org.etsi.its.adapter.ports.AdapterControlPort;
import org.etsi.its.adapter.ports.IPort;
import org.etsi.its.adapter.ports.PortEvent;
import org.etsi.its.adapter.ports.ProtocolPortFactory;
import org.etsi.its.adapter.ports.UpperTesterPort;
import org.etsi.ttcn.tci.CharstringValue;
import org.etsi.ttcn.tri.TriActionTemplate;
import org.etsi.ttcn.tri.TriAddress;
import org.etsi.ttcn.tri.TriAddressList;
import org.etsi.ttcn.tri.TriCommunicationSA;
import org.etsi.ttcn.tri.TriComponentId;
import org.etsi.ttcn.tri.TriException;
import org.etsi.ttcn.tri.TriMessage;
import org.etsi.ttcn.tri.TriParameter;
import org.etsi.ttcn.tri.TriParameterList;
import org.etsi.ttcn.tri.TriPortId;
import org.etsi.ttcn.tri.TriPortIdList;
import org.etsi.ttcn.tri.TriSignatureId;
import org.etsi.ttcn.tri.TriStatus;
import org.etsi.ttcn.tri.TriTestCaseId;

/**
 *  Main Test Adapter class. Implements TRI API in a tool independent manner
 */
@SuppressWarnings({ "serial", "deprecation" })
public class TestAdapter implements TriCommunicationSA, Observer {

    /** 
     * Mapping (component-) support for layered port
     */
    protected ComponentMgr compPortMgr;

    /**
     * Provides all TE related interfaces
     */
    protected ITERequired required;

    /**
     * Constructor
     */
    public TestAdapter() {
        super();

        compPortMgr = new ComponentMgr(this);
        required = TERFactory.getInstance();
    }

    /**
     * This method will force a Test adapter reset
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triEndTestCase()
     */
    @Override
    public TriStatus triEndTestCase() {
        triSAReset();
        return required.getTriStatus(TriStatus.TRI_OK);
    } 

    /* (non-Javadoc)
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triExecuteTestcase(org.etsi.ttcn.tri.TriTestCaseId, org.etsi.ttcn.tri.TriPortIdList)
     */
    @Override
    public TriStatus triExecuteTestcase(final TriTestCaseId tcId, final TriPortIdList portList) {
        return required.getTriStatus(TriStatus.TRI_OK);
    } 

    /* (non-Javadoc)
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triMap(org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriPortId)
     */
    @Override
    public TriStatus triMap(final TriPortId compPortId, final TriPortId tsiPortId) {

        IPort port;
        String portName = tsiPortId.getPortName();
        String ComponentId = compPortId.getComponent().getComponentId();
        
        if (tsiPortId.getPortName().equals("acPort")) {
            port = new AdapterControlPort(portName, ComponentId);
        } else if (tsiPortId.getPortName().toLowerCase().endsWith("utport")) {
            port = new UpperTesterPort(portName, ComponentId);
        } else {
            String componentName = compPortId.getComponent().getComponentName();
            
            // Dirty hack due to LinkLayer_MTC removal.
            if(componentName.equalsIgnoreCase("MTC")) {
                componentName = "NodeB";
            }
            
            port = ProtocolPortFactory.getInstance().createPort(
                    tsiPortId.getPortName(), 
                    ComponentId, 
                    ((CharstringValue)required.getTaParameter(portName)).getString(),
                    ((CharstringValue)required.getTaParameter("LinkLayer_" + componentName)).getString());
        }
        
        compPortMgr.addComponent(compPortId.getComponent());
        compPortMgr.addPort(ComponentId, tsiPortId, port);
                
        return required.getTriStatus(TriStatus.TRI_OK);
    } 

    /**
     * This method will force port unmapping   
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triSAReset()
     */
    @Override
    public TriStatus triSAReset() {
        
        // Force port unmapping
        compPortMgr.removeAllPorts();
        
        // Load certificates
        org.etsi.certificates.CertificatesIOFactory.getInstance().loadCertificates(((CharstringValue)TERFactory.getInstance().getTaParameter("TsSecuredRootPath")).getString(), ((CharstringValue)TERFactory.getInstance().getTaParameter("TsSecuredConfiId")).getString());
        
        return required.getTriStatus(TriStatus.TRI_OK);
    } 

    /* (non-Javadoc)
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triSend(org.etsi.ttcn.tri.TriComponentId, org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriAddress, org.etsi.ttcn.tri.TriMessage)
     */
    @Override
    public TriStatus triSend(TriComponentId componentId, TriPortId tsiPortId, TriAddress address, TriMessage message) {

        IPort port = compPortMgr.getPort(componentId.getComponentId(), tsiPortId.getPortName());
        if (port == null) {
            return required.getTriStatus(TriStatus.TRI_ERROR, "Unknown port");
        }
        
        port.send(message.getEncodedMessage());
        return required.getTriStatus(TriStatus.TRI_OK);                
    } 

    /* (non-Javadoc)
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triSendBC(org.etsi.ttcn.tri.TriComponentId, org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriMessage)
     */
    @Override
    public TriStatus triSendBC(TriComponentId componentId, TriPortId portId, TriMessage message) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triSendBC not implemented");
    }

    /* (non-Javadoc)
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triSendMC(org.etsi.ttcn.tri.TriComponentId, org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriAddressList, org.etsi.ttcn.tri.TriMessage)
     */
    @Override
    public TriStatus triSendMC(TriComponentId componentId, TriPortId portId, TriAddressList addressList, TriMessage message) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triSendMC  not implemented");
    }

    /* (non-Javadoc)
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triUnmap(org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriPortId)
     */
    @Override
    public TriStatus triUnmap(final TriPortId compPortId, final TriPortId tsiPortId) {
        compPortMgr.removePort(compPortId.getComponent().getComponentId(), tsiPortId.getPortName());
        return required.getTriStatus(TriStatus.TRI_OK);
    } 

    /**
     * Enqueues a message on the specified port
     * @param  tsiPort         Port where the message will be enqueued
     * @param  sutAddress      SUT address
     * @param  receiverComp    Component on which port the message will be enqueued
     * @param  rcvMessage      Message to be enqueued
     */
    private void enqueueMsg(TriPortId tsiPort, TriAddress sutAddress, TriComponentId receiverComp, TriMessage rcvMessage) {

        required.getCommunicationTE().triEnqueueMsg(tsiPort, sutAddress, receiverComp, rcvMessage);
    } 

    /* (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object context) {

        if(context instanceof PortEvent) {
            // Extract context
            PortEvent p = (PortEvent)context;
            TriPortId port = null;
            TriComponentId compId = null;

            port = (TriPortId)compPortMgr.getPortId(p.getComponentName(), p.getPortName());
            compId = compPortMgr.getComponent(p.getComponentName());

            // Enqueue message
            if (port != null) {
                enqueueMsg(
                        port, 
                        required.getTriAddress(new byte[] { }), 
                        compId, 
                        required.getTriMessage(p.get_message()));
            }
        }
    } // End of method update

    /**
     * TriCall not implemented
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triCall(org.etsi.ttcn.tri.TriComponentId, org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriAddress, org.etsi.ttcn.tri.TriSignatureId, org.etsi.ttcn.tri.TriParameterList)
     */
    @Override
    public TriStatus triCall(TriComponentId componentId, TriPortId tsiPortId, TriAddress sutAddress, TriSignatureId signatureId, TriParameterList parameterList) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triCall not implemented");    
    }

    /**
     * TriCallBC not implemented
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triCallBC(org.etsi.ttcn.tri.TriComponentId, org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriSignatureId, org.etsi.ttcn.tri.TriParameterList)
     */
    @Override
    public TriStatus triCallBC(TriComponentId componentId, TriPortId tsiPortId,    TriSignatureId signatureId, TriParameterList parameterList) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triCallBC not implemented");    
    }

    /**
     * TriCallMC not implemented
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triCallMC(org.etsi.ttcn.tri.TriComponentId, org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriAddressList, org.etsi.ttcn.tri.TriSignatureId, org.etsi.ttcn.tri.TriParameterList)
     */
    @Override
    public TriStatus triCallMC(TriComponentId componentId, TriPortId tsiPortId,    TriAddressList sutAddresses, TriSignatureId signatureId, TriParameterList parameterList) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triCallMC not implemented");    
    }

    /**
     * triRaise not implemented
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triRaise(org.etsi.ttcn.tri.TriComponentId, org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriAddress, org.etsi.ttcn.tri.TriSignatureId, org.etsi.ttcn.tri.TriException)
     */
    @Override
    public TriStatus triRaise(TriComponentId componentId, TriPortId tsiPortId, TriAddress sutAddress, TriSignatureId signatureId, TriException exception) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triRaise not implemented");    
    }

    /**
     * triRaiseBC not implemented
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triRaiseBC(org.etsi.ttcn.tri.TriComponentId, org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriSignatureId, org.etsi.ttcn.tri.TriException)
     */
    @Override
    public TriStatus triRaiseBC(TriComponentId componentId,    TriPortId tsitPortId, TriSignatureId signatureId, TriException exc) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triRaiseBC not implemented");    
    }

    /**
     * triRaiseMC not implemented
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triRaiseMC(org.etsi.ttcn.tri.TriComponentId, org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriAddressList, org.etsi.ttcn.tri.TriSignatureId, org.etsi.ttcn.tri.TriException)
     */
    @Override
    public TriStatus triRaiseMC(TriComponentId componentId,    TriPortId tsitPortId, TriAddressList sutAddresses, TriSignatureId signatureId, TriException exc) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triRaiseMC not implemented");    
    }

    /**
     * triReply not implemented
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triReply(org.etsi.ttcn.tri.TriComponentId, org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriAddress, org.etsi.ttcn.tri.TriSignatureId, org.etsi.ttcn.tri.TriParameterList, org.etsi.ttcn.tri.TriParameter)
     */
    @Override
    public TriStatus triReply(TriComponentId componentId, TriPortId tsiPortId, TriAddress sutAddress, TriSignatureId signatureId, TriParameterList parameterList, TriParameter returnValue) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triReply not implemented");    
    }

    /**
     * triReplyBC not implemented
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triReplyBC(org.etsi.ttcn.tri.TriComponentId, org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriSignatureId, org.etsi.ttcn.tri.TriParameterList, org.etsi.ttcn.tri.TriParameter)
     */
    @Override
    public TriStatus triReplyBC(TriComponentId componentId, TriPortId tsiPortId, TriSignatureId signatureId, TriParameterList parameterList, TriParameter returnValue) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triReplyBCnot implemented");    
    }

    /**
     * triReplyMC not implemented
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triReplyMC(org.etsi.ttcn.tri.TriComponentId, org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriAddressList, org.etsi.ttcn.tri.TriSignatureId, org.etsi.ttcn.tri.TriParameterList, org.etsi.ttcn.tri.TriParameter)
     */
    @Override
    public TriStatus triReplyMC(TriComponentId componentId, TriPortId tsiPortId, TriAddressList sutAddresses, TriSignatureId signatureId, TriParameterList parameterList, TriParameter returnValue) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triReplyMC not implemented");    
    }

    /**
     * triSutActionInformal not implemented
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triSutActionInformal(java.lang.String)
     */
    @Override
    public TriStatus triSutActionInformal(String description) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triSutActionInformal not implemented");    
    }

    /**
     * triSutActionTemplate not implemented
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triSutActionTemplate(org.etsi.ttcn.tri.TriActionTemplate)
     */
    @Override
    public TriStatus triSutActionTemplate(TriActionTemplate templateValue) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triSutActionTemplate not implemented");
    }

    /**
     * triMapParam not implemented
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triMapParam(org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriParameterList)
     */
    @Override
    public TriStatus triMapParam(TriPortId compPortId, TriPortId tsiPortId,    TriParameterList paramList) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triMapParam not implemented");
    }

    /**
     * triUnmapParam not implemented
     * @see org.etsi.ttcn.tri.TriCommunicationSA#triUnmapParam(org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriPortId, org.etsi.ttcn.tri.TriParameterList)
     */
    @Override
    public TriStatus triUnmapParam(TriPortId compPortId, TriPortId tsiPortId, TriParameterList paramList) {
        return required.getTriStatus(TriStatus.TRI_ERROR, "triUnmapParam not implemented");
    }
} 
