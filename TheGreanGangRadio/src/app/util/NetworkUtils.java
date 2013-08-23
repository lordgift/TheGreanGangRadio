package app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import app.pojo.NetworkDetail;

public class NetworkUtils {
	private static final Logger log = LogUtils.getLogger(NetworkUtils.class);

	private static final NetworkUtils INSTANCE = new NetworkUtils();

	private NetworkUtils() {
	}

	public static NetworkUtils getInstance() {
		return INSTANCE;
	}

	/**
	 * getHostName of client computer name by pinging ip address <BR/>
	 * then substring for get only computer name
	 * 
	 * @param ipAddress
	 *            use for get name of this ip
	 * @return computer name
	 */
	public String getHostNameByCommandLine(String ipAddress) {
		log.debug("Enter getHostNameByCommandLine");
		String hostName = null;
		try {
			// remoteHostName =
			// InetAddress.getByName(ipAddress).getCanonicalHostName();

			StringBuilder command = new StringBuilder();
			command.append("cmd /C ");
			command.append("\"");
			command.append(" ping -a "); // -a Resolve addresses to hostnames.
			command.append(ipAddress);
			command.append(" -n 1 "); // -n count Number of echo requests to
										// send.
			command.append("\"");

			log.debug(command.toString());
			InputStream input = Runtime.getRuntime().exec(command.toString()).getInputStream();

			BufferedReader buff = new BufferedReader(new InputStreamReader(input));

			String line = null;
			StringBuilder output = new StringBuilder();
			while ((line = buff.readLine()) != null) {
				output.append(line);
			}

			buff.close();
			input.close();

			// substring to get hostname Ex. Pinging NATTAPHONG-T
			// [157.179.132.232] with..........
			if (output.indexOf(" [") != -1) {
				int startIndex = "Pinging ".length();
				int endIndex = output.indexOf(" [");

				hostName = output.substring(startIndex, endIndex);
//				 log.debug(remoteHostName);

//				 command = new StringBuilder();
//				 command.append("cmd /C ");
//				 command.append("\"");
//				 command.append(" nbtstat -a "); // -A (Adapter status) Lists
//				 the remote machine's name table given its IP address.
//				 command.append(ipAddress);
//				 command.append("\"");
			} else {
				hostName = ipAddress;
			}
		} catch (IOException e) {
			log.error("Error in getHostNameByCommandLine", e);
		} finally {
			log.debug("Quit getHostNameByCommandLine");
		}
		return hostName;
	}

	/**
	 * this method managing related session attribute
	 * {@link Constants#SESSION_REMOTE_NETWORK_DETAIL}<BR/>
	 * then get detail of remoting client in {@link NetworkDetail} pojo <BR/>
	 * 
	 * @return {@link NetworkDetail} pojo
	 */
	public NetworkDetail managingSessionNetworkDetail() {
//		log.debug("Enter managingSessionNetworkDetail");

		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpSession session = request.getSession();

		NetworkDetail networkDetail = (NetworkDetail) session.getAttribute(Constants.SESSION_REMOTE_NETWORK_DETAIL);

		if (networkDetail == null) {
			String ipAddress = request.getRemoteAddr();
			String hostName = getHostNameByCommandLine(ipAddress);

			networkDetail = new NetworkDetail();
			networkDetail.setIpAddress(ipAddress);
			networkDetail.setHostName(hostName);

			session.setAttribute(Constants.SESSION_REMOTE_NETWORK_DETAIL, networkDetail);
		}
		log.debug("Entering >> " + networkDetail.getHostName() + " [" + networkDetail.getIpAddress() + "]");

//		log.debug("Quit managingSessionNetworkDetail");
		return networkDetail;
	}
	
	/**
	 * change PC's name to get alias name by using HARDCODE 
	 * @param hostName
	 * @return alias, otherwise hostName
	 */
	public static String getAliasOfHostName(String hostName) {
//		log.debug("Enter getAliasOfHostName");
		
		//initial for out of cases
		String alias = hostName;
		
		if("jarupath-j".equalsIgnoreCase(hostName)) {
			alias = "LordGift";
			
		} else if("prapassorn-pc".equalsIgnoreCase(hostName)) {
			alias = "Chocogemⓟ";
			
		} else if("pitchapong-b".equalsIgnoreCase(hostName)) {
			alias = "งัว";
			
		} else if("sirinthip-p".equalsIgnoreCase(hostName)) {
			alias = "หม่ามี้";
			
		} else if("nattapong-t".equalsIgnoreCase(hostName)) {
			alias = "Akermiji";
			
		} else if("fikree_s".equalsIgnoreCase(hostName)) {
			alias = "Fik";
			
		} else if("jatuponr-pc".equalsIgnoreCase(hostName)) {
			alias = "Samoky";
			
		} else if("natchaya-s".equalsIgnoreCase(hostName)) {
			alias = "Aew Sukmanee";
			
		} else if("VALAN-B".equalsIgnoreCase(hostName)) {
			alias = "โมจิ";
			
		} else if("CHINJA-PC".equalsIgnoreCase(hostName)) {
			alias = "Chinja";
			
		} else if("piyawan-y".equalsIgnoreCase(hostName)) {
			alias = "JipJup";
		}
		log.debug("alias = " + alias);
		
//		log.debug("Quit getAliasOfHostName");
		return alias;
	}

}
