package bp.cli.console;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;

import bp.data.BPCommandResult;
import bp.util.IOLineLoop;

public class BPConsoleHandlerDynamic extends BPConsoleHandlerBase
{
	protected volatile Function<String, BPCommandResult> m_cb;
	protected volatile String m_hint;
	protected volatile boolean m_dealempty;

	public void setCommandHandler(Function<String, BPCommandResult> cb)
	{
		m_cb = cb;
	}

	protected BPCommandResult callCommand(String cmd)
	{
		BPCommandResult r = m_cb.apply(cmd);
		if (r != null)
		{
			return r;
		}
		else
		{
			exitLoop();
		}
		return BPCommandResult.OK(null);
	}

	public void setHint(String hint)
	{
		m_hint = hint;
	}

	public void writeStartHint(OutputStream out, boolean newline) throws IOException
	{
		if (newline)
			out.write('\n');
		if (m_hint != null)
		{
			out.write(m_hint.getBytes());
		}
		else
		{
			out.write(62);
		}
	}

	public boolean dealEmptyLine(OutputStream out, IOLineLoop loop) throws IOException
	{
		if (m_dealempty)
			return dealLine("", loop);
		return false;
	}

	public void setDealEmpty(boolean flag)
	{
		m_dealempty = flag;
	}

	protected void exitLoop()
	{
		super.exitLoop();
		m_cb = null;
	}
}
