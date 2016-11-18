package com.thr.ddns;

import org.apache.commons.lang.builder.ToStringBuilder;
import com.google.common.base.Preconditions;

public class ConfigInfo {
	public final String m_regionId;// Region

	public final String m_accessKeyId;// accessKey

	public final String m_accessKeySecret;// accessSecret

	public final String m_DomainName;// 域名名称

	public final String[] m_RRs;// 主机记录

	public final String m_IpUrl;//
	
	public final String m_time;//
	
	public final String m_type="A";//

	public ConfigInfo(final String regionId, final String accessKeyId, final String accessKeySecret,
			final String DomainName, final String RRs, final String IpUrl, final String time) {
		Preconditions.checkNotNull(regionId);
		Preconditions.checkNotNull(accessKeySecret);
		Preconditions.checkNotNull(DomainName);
		Preconditions.checkNotNull(DomainName);
		Preconditions.checkNotNull(RRs);
		Preconditions.checkNotNull(IpUrl);
		Preconditions.checkNotNull(time);
		m_regionId = regionId;
		m_accessKeyId = accessKeyId;
		m_accessKeySecret = accessKeySecret;
		m_DomainName = DomainName;
		m_RRs = RRs.split(",");
		if (m_RRs.length == 0) {
			m_RRs[0] = "@";
		}
		m_IpUrl = IpUrl;
		m_time=time;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("regionId", m_regionId).append("accessKeyId", m_accessKeyId)
				.append("accessKeySecret", m_accessKeySecret).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigInfo other = (ConfigInfo) obj;
		if (m_regionId != other.m_regionId)
			return false;
		return true;
	}
}
