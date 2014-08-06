package com.doteyplay.luna.client;

import org.apache.log4j.Logger;

import com.doteyplay.luna.client.container.SynchronicConnectionPool;
import com.doteyplay.luna.common.message.DecoderMessage;
import com.doteyplay.luna.common.message.EncoderMessage;


public class SynchronicClientManager
{

	private static Logger logger = Logger
			.getLogger(SynchronicClientManager.class.getName());
	private SynchronicConnectionPool pool;
	private ConnectionInfo info;

	public SynchronicClientManager()
	{
	}

	public void initial(ConnectionInfo connectionInfo)
	{
		info = connectionInfo;
		pool = new SynchronicConnectionPool(info);
	}

	public DecoderMessage synInvoke(EncoderMessage request)
	{
		try
		{
			return pool.synInvoke(request);
		} catch (Exception e)
		{
			logger.error(
					(new StringBuilder())
							.append("��RPCʧ�ܡ�����Զ�˵ķ����������ṩ���񣬻��߱������Ӻľ���")
							.append(info.toString()).toString(), e);
			return null;
		}
	}
	
	public void asynInvoke(EncoderMessage request)
	{
		try
		{
			pool.asynInvoke(request);
		} catch (Exception e)
		{
			logger.error(
					(new StringBuilder())
							.append("��RPCʧ�ܡ�����Զ�˵ķ����������ṩ���񣬻��߱������Ӻľ���")
							.append(info.toString()).toString(), e);
		}
	}
	
	

	public boolean relocate(String ip, int port)
	{
		if (ip == null || port <= 0 || port > 65535)
			return false;
		info.setServerAddress(ip);
		info.setServerPort(port);
		try
		{
			if (pool == null)
				initial(info);
			else
				pool.relocate(info);
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

}