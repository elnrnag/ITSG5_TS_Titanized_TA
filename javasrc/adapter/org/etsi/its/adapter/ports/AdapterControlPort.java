/**
 *  Adapter control port implementation for dynamic interactions with Test Adapter
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/ports/AdapterControlPort.java $
 *              $Id: AdapterControlPort.java 1822 2014-11-18 09:18:17Z berge $
 *
 */
package org.etsi.its.adapter.ports;

import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.Management;

/** This class implements behaviour for Adapter controller port
 *
 */

public class AdapterControlPort extends AdapterPort implements IPort, IObservable { 

    /* AdapterControl Primitives */
    private static final byte AcGnPrimitive = 0;
    private static final byte AcGn6Primitive = 1;
    
    /* AdapterControl Response */
    private static final byte AcGnResponse = 0;
    //private static final byte AcGn6Response = 1;

    /* GN Commands */
    private static final byte AcStartBeaconing = 0;
    private static final byte AcStopBeaconing = 1;
    private static final byte AcStartPassBeaconing = 2;
    private static final byte AcStopPassBeaconing = 3;
    private static final byte AcStartBeaconingMultipleNeighbour = 4;
    private static final byte AcStopBeaconingMultipleNeighbour = 5;
    private static final byte AcGetLongPosVector = 6;
    private static final byte AcEnableSecurity = 7;
    private static final byte AcDisableSecurity = 8;

    /* GN Responses */
    protected static final byte AcGnResponseFailure = 0;
    protected static final byte AcLongPosVector = 6;

    protected static final byte AcTrue = 0x01;
    protected static final byte AcFalse = 0x01;
    
    /**
     * Constructor
     * @param   portName        Name of the port
     * @param   componentName   Name of the component owning this port instance
     */
    public AdapterControlPort(final String portName, final String componentName) {
        super(portName, componentName);
    }

    @Override
    public boolean send(final byte[] message) {

        boolean result = true;
        try { 
            // Decode non protocol part
            switch(message[0]) {
                case AcGnPrimitive: {
                    byte[] data = ByteHelper.extract(message, 2, message.length - 2);
                    switch (message[1]) {
                        case AcGetLongPosVector:
                            ProcessAcGetLongPosVector(data);
                            break;
                        case AcStartBeaconing:
                            Management.getInstance(getComponentName()).startBeaconing(data);
                            break;
                        case AcStopBeaconing:
                            Management.getInstance(getComponentName()).stopBeaconing();
                            break;
                        case AcStartPassBeaconing:
                            ProcessAcStartPassBeaconing(data);
                            break;
                        case AcStopPassBeaconing:
                            Management.getInstance(getComponentName()).stopEnqueueingBeacons();
                            break;
                        case AcStartBeaconingMultipleNeighbour:
                             // TODO
                            break;
                        case AcStopBeaconingMultipleNeighbour:
                             // TODO
                            break;
                        case AcEnableSecurity: 
                            Management.getInstance(getComponentName()).setSecuredMode(data);
                            break;
                        case AcDisableSecurity: 
                            Management.getInstance(getComponentName()).unsetSecuredMode();
                            break;
                    } 
                }
                break;
                case AcGn6Primitive:
                    /* FIXME
                    try {
                        byte[] buf = new byte[4096];
                        DatagramSocket remoteAdapterSocket = new DatagramSocket();
                        InetAddress remoteAdapterAddress = InetAddress.getByName(((CharstringValue)TERFactory.getInstance().getTaParameter("Gn6RemoteAdapterIp")).getString());
                        int remoteAdapterPort = Integer.decode(((CharstringValue)TERFactory.getInstance().getTaParameter("Gn6RemoteAdapterPort")).getString());
                        
                        DatagramPacket command = new DatagramPacket(new byte[] {CMD_IFACES}, 1, remoteAdapterAddress, remoteAdapterPort);
                        DatagramPacket response = new DatagramPacket(buf, buf.length);
                        
                        remoteAdapterSocket.send(command);
                        remoteAdapterSocket.receive(response); 
                        
                        byte[] data = ByteHelper.extract(response.getData(), response.getOffset(), response.getLength());
                        if(data[0] == RPL_IFACES) {
                            // enqueue response... 
                            byte[] buffer = impl.Encode(TestAdapterMessageTypeEnum.AcGn6Response, ByteHelper.extract(data, 1, data.length - 1), new Date().getTime());                        
                            setChanged();
                            notifyObservers(new PortEvent(buffer, getPortName(), getComponentName()));
                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }         
                    */
                    break;
            } // End of 'switch' statement

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    } // End of method send

    /**
     * Processes AcGetLongPosVector command
     * @param  data
     */
    private void ProcessAcGetLongPosVector(final byte[] gnAddress)  {

        new Thread(new Runnable() {
            @Override
            public void run() { 
        
                byte[] response = null;
                byte[] responseHdr = {(byte)AcGnResponse, (byte)0x00};
                byte[] longPosVector = Management.getInstance(getComponentName()).getLongPositionVector(gnAddress);
                if((longPosVector == null) || (longPosVector.length == 0)) {
                    responseHdr[1] = (byte)AcGnResponseFailure;
                    response = ByteHelper.concat(responseHdr, new byte[]{AcTrue});   
                } 
                else {
                    responseHdr[1] = (byte)AcLongPosVector;
                    response = ByteHelper.concat(responseHdr, longPosVector);
                }
                
                setChanged();
                notifyObservers(new PortEvent(response, getPortName(), getComponentName()));
            }
        }).start();
    }

       private void ProcessAcStartPassBeaconing(final byte[] beacon)  {

            byte[] response = {(byte)AcGnResponse, (byte)AcGnResponseFailure, (byte)AcFalse};
            Management.getInstance(getComponentName()).startEnqueueingBeacons(beacon);
            
            setChanged();
            notifyObservers(new PortEvent(response, getPortName(), getComponentName()));
        }

    @Override
    public void dispose() {
        //empty
    }

} // End of class AdapterControlPort
