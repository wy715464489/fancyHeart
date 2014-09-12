package sample1.client;

import org.apache.mina.core.session.IoSession;

import com.doteyplay.luna.common.action.ActionController;
import com.doteyplay.luna.common.action.IBaseAction;
import com.doteyplay.luna.common.message.DecoderMessage;

public class TestClientController implements ActionController {

	@Override
	public IBaseAction getAction(DecoderMessage message) {
		System.out.println(message.getCommandId()+":"+message.getRoleId()+":");
		return new ClientAction();
	}

	@Override
	public void sessionClose(IoSession session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionOpen(IoSession session)
	{
		
	}

}
