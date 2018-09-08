package conf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {
	private static final String MONGOS_IP = "MONGOS_IP";
	private static final String MONGOS_PORT = "MONGOS_PORT";
	private static final String UPPER_MIDDLEWARE_IP = "UPPER_MIDDLEWARE_IP";
	private static final String LOWER_MIDDELWARE_IP = "LOWER_MIDDLEWARE_IP";
	private static final String SERVER_PORT = "SERVER_PORT";
	private static final String LOCAL = "LOCAL";
	
	private List<String> mongosIP = new ArrayList<String>();
	private Integer mongosPort = 30000;
	private String upperMWIP = "";
	private List<String> lowerMWIP = new ArrayList<String>();
	private Integer serverPort = 53000;
	private Boolean local = false;
	
	public Config(String configPath) throws IOException {
		FileReader fileReader = new FileReader(configPath);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] config = line.split(":");
			if (config[0].indexOf(MONGOS_IP) != -1) {
				String[] mongosip = config[1].split(";");
				for (String ip : mongosip) {
					mongosIP.add(ip);
				}
			} else if (config[0].indexOf(MONGOS_PORT) != -1) {
				mongosPort = Integer.valueOf(config[1]);
			} else if (config[0].indexOf(SERVER_PORT) != -1) {
				serverPort = Integer.valueOf(config[1]);
			} else if (config[0].indexOf(UPPER_MIDDLEWARE_IP) != -1) {
				upperMWIP = config[1];
			} else if (config[0].indexOf(LOWER_MIDDELWARE_IP) != -1) {
				String[] mwip = config[1].split(";");
				for (String ip : mwip) {
					lowerMWIP.add(ip);
				}
			} else if (config[0].indexOf(LOCAL) != -1) {
				local = Boolean.parseBoolean(config[1]);
			}
		}
		bufferedReader.close();
		fileReader.close();
	}

	public List<String> getMongosIP() {
		return mongosIP;
	}

	public Integer getMongosPort() {
		return mongosPort;
	}

	public Integer getServerPort() {
		return serverPort;
	}

	public String getUpperMWIP() {
		return upperMWIP;
	}

	public List<String> getLowerMWIP() {
		return lowerMWIP;
	}

	public Boolean getLocal() {
		return local;
	}
}
