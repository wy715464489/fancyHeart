package sample1.server;

import org.apache.mina.core.session.IoSession;

import com.doteyplay.luna.common.action.ActionController;
import com.doteyplay.luna.common.action.IBaseAction;
import com.doteyplay.luna.common.message.DecoderMessage;

public class TestActionController implements ActionController {

	@Override
	public IBaseAction getAction(DecoderMessage message) {
		System.out.println("TestActionController:"+message.getCommandId()+":"+message.getRoleId()+":");
		return new TestAction();
	}

	@Override
	public void sessionClose(IoSession session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionOpen(IoSession session)
	{
		// TODO Auto-generated method stub
		
	}

}
