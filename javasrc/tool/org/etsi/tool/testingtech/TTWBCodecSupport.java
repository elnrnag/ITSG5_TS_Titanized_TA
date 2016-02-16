/**
 * @author    STF 424_ITS_Test_Platform
 * @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/tool/org/etsi/tool/testingtech/TTWBCodecSupport.java $
 *             $Id: TTWBCodecSupport.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.tool.testingtech;

import org.etsi.codec.ITCIRequired;
import org.etsi.ttcn.tci.TciCDProvided;

import com.testingtech.ttcn.extension.CodecProvider;
import com.testingtech.ttcn.tri.AbstractPlugin;
import com.testingtech.util.plugin.IPlugin;
import com.testingtech.util.plugin.IPluginParameter;
import com.testingtech.util.plugin.PluginInitException;
import com.testingtech.util.plugin.PluginInstantiationException;
import com.testingtech.util.plugin.PluginParameter;
import com.testingtech.util.plugin.PluginRepository;

import de.tu_berlin.cs.uebb.muttcn.runtime.RB;

/**
 * This class provides an implementation of the ITCIRequired interface to create a instance of TTCN-3 codec
 * 
 * @category Factory
 */
public class TTWBCodecSupport implements ITCIRequired{

	private RB _rb;
	
	/**
	 * Constructor
	 * @param rb TTWB Test Execution instance
	 */
	public TTWBCodecSupport(RB rb) {
		_rb = rb;
	}
	
	/**
	 * We overwrite this method to bypass the codec caching mechanism provided by TTwb.
	 * The reason is that the TA is based on multicomponent/one port mapping mechanism
	 * 
	 * @param encodingName The encoding identifier
	 * @return The codec instance on success, null otherwise
	 */
	@Override
    public TciCDProvided getCodec(String encodingName) {
		if (encodingName == null)
			return null;
		IPluginParameter[] parameters = new IPluginParameter[]{
			new PluginParameter("encoding", encodingName)
		};
		IPlugin plugin = PluginRepository.getInstance().getPluginByParameters(
				CodecProvider.class.getName(), parameters);
		if (plugin != null) {
			try {
				CodecProvider codecProvider = (CodecProvider) plugin.instantiate(CodecProvider.class.getName(), parameters, _rb.getExternalLoader());
				try {
					TciCDProvided codec = codecProvider.getCodec(_rb, encodingName);
					if (codec!=null && codec instanceof AbstractPlugin) {
						((AbstractPlugin) codec).initAbstractPlugin(_rb);
					}
					return codec;
				} catch (PluginInitException e) {
					e.printStackTrace();
				}
			}
			catch (PluginInstantiationException e) {
				e.printStackTrace();
			}
		} else {
			TciCDProvided codec = _rb.TestAdapter.getCodec(encodingName);
			if (codec!=null && codec instanceof AbstractPlugin) {
				((AbstractPlugin) codec).initAbstractPlugin(_rb);
			}
			return codec;
		}

		return null;
	}
	
} // End of class BaseCodec
