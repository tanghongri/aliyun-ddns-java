package com.thr.ddns;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.AddDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse.Record;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * Hello world!
 *
 */
public class App {
	private static IAcsClient client = null;

	public static void SetConfig(ConfigInfo Info) {
		IClientProfile profile = DefaultProfile.getProfile(Info.m_regionId, Info.m_accessKeyId, Info.m_accessKeySecret);
		// 若报Can not find endpoint to access异常，请添加以下此行代码
		try {
			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Alidns", "alidns.aliyuncs.com");
		} catch (ClientException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		client = new DefaultAcsClient(profile);
	}

	public static void main(String[] args) {
		ConfigInfo Info = GetConfig("ddns.properties");
		if (null == client) {
			SetConfig(Info);
		}

		DescribeDomainRecordsRequest GetRequest = new DescribeDomainRecordsRequest();
		DescribeDomainRecordsResponse GetResponse;
		UpdateDomainRecordRequest UpdateRequest = new UpdateDomainRecordRequest();

		GetRequest.setProtocol(ProtocolType.HTTPS); // 指定访问协议
		GetRequest.setAcceptFormat(FormatType.JSON); // 指定api返回格式
		GetRequest.setMethod(MethodType.POST); // 指定请求方法
		GetRequest.setDomainName(Info.m_DomainName);

		String ip = "";
		String curip = "";

		while (true) {
			curip = getMyIP(Info.m_IpUrl);
			if (ip != curip) {// 获取远程域名IP
				try {
					for (int i = 0; i < Info.m_RRs.length; i++) {
						GetRequest.setRRKeyWord(Info.m_RRs[i]);
						GetRequest.setTypeKeyWord(Info.m_type);
						GetResponse = client.getAcsResponse(GetRequest);
						List<Record> list = GetResponse.getDomainRecords();
						if (list.isEmpty()) {// 无该主机记录则增加
							AddDomainRecordRequest AddRequest = new AddDomainRecordRequest();
							AddRequest.setDomainName(Info.m_DomainName);
							AddRequest.setRR(Info.m_RRs[i]);
							AddRequest.setType(Info.m_type);
							AddRequest.setValue(curip);
							client.getAcsResponse(AddRequest);
							System.out.println("Add:"+Info.m_DomainName+Info.m_RRs[i]+Info.m_type+curip);
						} else {
							for (Record domain : list) {
								if (!domain.getValue().equals(curip)) {// 修改记录
									UpdateRequest.setRecordId(domain.getRecordId());
									UpdateRequest.setRR(Info.m_RRs[i]);
									UpdateRequest.setType(Info.m_type);
									UpdateRequest.setValue(curip);
									client.getAcsResponse(UpdateRequest);
									System.out.println("Update:"+Info.m_DomainName+Info.m_RRs[i]+Info.m_type+curip);
								}
							}
						}

					}
				} catch (ServerException e) {
					e.printStackTrace();
				} catch (ClientException e) {
					e.printStackTrace();
				}
				// 保存远端IP信息
				ip = curip;
			}
			try {
				Thread.sleep(Integer.parseInt(Info.m_time) * 60 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static ConfigInfo GetConfig(String path) {
		ConfigInfo Info = null;
		if (path.isEmpty()) {
			return Info;
		}
		Properties prop = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(path));
			prop.load(in); /// 加载属性列表
			Info = new ConfigInfo(prop.getProperty("regionId", ""), prop.getProperty("accessKeyId", ""),
					prop.getProperty("accessKeySecret", ""), prop.getProperty("DomainName", ""),
					prop.getProperty("RRs", ""), prop.getProperty("IpUrl", ""), prop.getProperty("Time", ""));
			in.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return Info;
	}

	private static String getMyIP(String IpUrl) {
		String ip = "";
		String inputLine = "";
		String read = "";
		try {
			URL url = new URL(IpUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			while ((read = in.readLine()) != null) {
				inputLine += read;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Pattern p = Pattern.compile(
				"((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])");
		Matcher m = p.matcher(inputLine);
		if (m.find()) {
			ip = m.group(0);
			System.out.println("curip:"+ip);
		}
		return ip;
	}

}
