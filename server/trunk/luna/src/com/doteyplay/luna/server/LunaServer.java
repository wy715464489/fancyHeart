package com.doteyplay.luna.server;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.doteyplay.luna.common.action.ActionController;
import com.doteyplay.luna.common.message.DecoderMessage;
import com.doteyplay.luna.common.message.EncoderMessage;
import com.doteyplay.luna.common.protocol.DefaultProtocolHandler;
import com.doteyplay.luna.common.protocol.codec.DefaultProtocolCodecFactory;
import com.doteyplay.luna.common.threadpool.DefaultExecutorFilter;
import com.doteyplay.luna.common.threadpool.IUserPkIdDecoder;
import com.doteyplay.luna.common.threadpool.UserOrderedThreadPoolExecutor;

/**
 * ����������Ҫ������Server
 * 
 */
public class LunaServer
{
	private final static Logger logger = Logger.getLogger(LunaServer.class);

	/**
	 * �߳�����
	 */
	private static final int THREAD_NUM = 32;
	/**
	 * ��������С
	 */
	private static final int BUFFER_SIZE = 1024 * 16;
	/**
	 * readBufĬ�ϴ�С
	 */
	private static final int READ_BUFFER_SIZE = 1024 * 2;
	/**
	 * �Ƿ��ӳٷ���
	 */
	private static final boolean TCP_NO_DELAY = true;
	/**
	 * �˿�
	 */
	private int port;
	/**
	 * �Ự�����ľ��
	 */
	private IoHandler ioHandler;
	/**
	 * Э�鹤��
	 */
	private ProtocolCodecFactory protocolCodeFactory = new DefaultProtocolCodecFactory();

	/**
	 * ���Ʒַ���Controller
	 */
	private ActionController actionController;
	/**
	 * NIOSocket
	 */
	private NioSocketAcceptor nioSocketAcceptor;

	/**
	 * Ĭ�ϵĹ��캯��
	 * 
	 * @param port
	 * @param acceptor
	 * @param handler
	 */
	public LunaServer(int port, ActionController controller)
	{
		this.port = port;
		this.actionController = controller;
		this.ioHandler = new DefaultProtocolHandler(this.actionController);
	}

	/**
	 * ����Server
	 */
	public void start(boolean isOrderByUserId)
	{
		try
		{
			InetSocketAddress address = new InetSocketAddress(this.port);
			this.nioSocketAcceptor = new NioSocketAcceptor(Runtime.getRuntime()
					.availableProcessors() + 1);
			this.nioSocketAcceptor.getFilterChain().addLast("codec",
					new ProtocolCodecFilter(this.protocolCodeFactory));
			// this.nioSocketAcceptor.getFilterChain()
			// .addLast(
			// "threadPool",
			// new ExecutorFilter(Executors
			// .newFixedThreadPool(THREAD_NUM)));

			IoFilter filter = null;
			if (isOrderByUserId)
				filter = new DefaultExecutorFilter(
						new UserOrderedThreadPoolExecutor(THREAD_NUM,
								new IUserPkIdDecoder()
								{
									@Override
									public long getUserId(Object param)
									{
										if (!param.getClass().isAssignableFrom(
												DecoderMessage.class))
											throw new RuntimeException(
													"ioevent param error,userIdDecoder error!!!");
										DecoderMessage dMessage = (DecoderMessage) param;
										if (logger.isDebugEnabled())
											logger.debug("==========threadpool exe========"
													+ dMessage.getRoleId());
										return dMessage.getRoleId();
									}
								}));
			else
				filter = new ExecutorFilter(
						Executors.newFixedThreadPool(THREAD_NUM));
			this.nioSocketAcceptor.getFilterChain().addLast("threadPool",
					filter);

			this.nioSocketAcceptor.getSessionConfig().setReceiveBufferSize(
					BUFFER_SIZE);
			// this.nioSocketAcceptor.getSessionConfig().setReadBufferSize(READ_BUFFER_SIZE);
			this.nioSocketAcceptor.getSessionConfig().setTcpNoDelay(
					TCP_NO_DELAY);
			this.nioSocketAcceptor.setHandler(this.ioHandler);

			try
			{
				nioSocketAcceptor.getSessionConfig().setSoLinger(0);
				nioSocketAcceptor.setReuseAddress(true);
			} catch (Exception e)
			{
				logger.error(e.getMessage(), e);
			}

			this.nioSocketAcceptor.bind(address);
			logger.error("ϵͳ�������");
		} catch (Exception e)
		{
			logger.error("ϵͳ�����쳣��", e);
		}
	}

	/**
	 * ���������������з���ֵ
	 * 
	 * @return
	 */
	public boolean newStart()
	{
		try
		{
			InetSocketAddress address = new InetSocketAddress(this.port);
			this.nioSocketAcceptor = new NioSocketAcceptor(Runtime.getRuntime()
					.availableProcessors() + 1);
			this.nioSocketAcceptor.getFilterChain().addLast("codec",
					new ProtocolCodecFilter(this.protocolCodeFactory));
			this.nioSocketAcceptor.getFilterChain().addLast(
					"threadPool",
					new DefaultExecutorFilter(
							new UserOrderedThreadPoolExecutor(THREAD_NUM,
									new IUserPkIdDecoder()
									{
										@Override
										public long getUserId(Object param)
										{
											if (param.getClass()
													.isAssignableFrom(
															IoBuffer.class))
												throw new RuntimeException(
														"ioevent param error,userIdDecoder error!!!");
											IoBuffer buf = (IoBuffer) param;
											return buf.getInt(6);
										}
									})));
			this.nioSocketAcceptor.getSessionConfig().setReceiveBufferSize(
					BUFFER_SIZE);
			// this.nioSocketAcceptor.getSessionConfig().setReadBufferSize(READ_BUFFER_SIZE);
			this.nioSocketAcceptor.getSessionConfig().setTcpNoDelay(
					TCP_NO_DELAY);
			this.nioSocketAcceptor.setHandler(this.ioHandler);
			this.nioSocketAcceptor.bind(address);
			logger.error("ϵͳ�������");
			return true;
		} catch (Exception e)
		{
			logger.error("ϵͳ�����쳣��", e);
			return false;
		}
	}

	/**
	 * ���͹㲥�㲥���ͻ���
	 * 
	 * @param message
	 */
	public void broadCast(EncoderMessage message)
	{
		Map<Long, IoSession> sessions = this.nioSocketAcceptor
				.getManagedSessions();
		Collection<IoSession> col = sessions.values();
		for (IoSession session : col)
		{
			session.write(message);
		}
	}

	/**
	 * ж�ذ�
	 */
	public void stop()
	{
		Map<Long, IoSession> sessions = this.nioSocketAcceptor
				.getManagedSessions();
		Collection<IoSession> col = sessions.values();
		EncoderMessage message = closeMessage();
		for (IoSession session : col)
		{
			session.write(message);
		}
		this.nioSocketAcceptor.setCloseOnDeactivation(true);
		this.nioSocketAcceptor.unbind();
		this.nioSocketAcceptor.dispose();
		logger.error("ϵͳֹͣ��ɣ�");
	}

	/**
	 * ����ر�ָ��
	 * 
	 * @return
	 */
	private EncoderMessage closeMessage()
	{
		EncoderMessage message = new EncoderMessage(Short.MAX_VALUE, false);
		message.setRoleId(Integer.MAX_VALUE);
		return message;
	}

}