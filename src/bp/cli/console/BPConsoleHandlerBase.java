package bp.cli.console;

import java.io.IOException;
import java.io.OutputStream;

import bp.cli.util.CLIUtil;
import bp.data.BPCommandResult;
import bp.util.IOLineLoop;
import bp.util.LogicUtil.WeakRefGo;

public abstract class BPConsoleHandlerBase implements BPConsoleHandler
{
	protected volatile WeakRefGo<IOLineLoop> m_llref;
	protected volatile boolean m_endflag;

	public BPConsoleHandlerBase()
	{
	}

	public void setup(IOLineLoop ll)
	{
		m_llref = new WeakRefGo<IOLineLoop>(ll);
		ll.addHandler(this);
	}

	public boolean dealLine(String line, IOLineLoop loop) throws IOException
	{
		OutputStream out = loop.getOutputStream();
		if (line != null)
		{
			BPCommandResult result = callCommand(line);
			if (result != null && result.data != null)
			{
				writeData(result.data, out);
				writeLine("", out);
			}
			out.flush();
		}
		return m_endflag;
	}

	protected abstract BPCommandResult callCommand(String cmd);

	protected OutputStream getOutputStream()
	{
		return m_llref.exec(ll -> ll.getOutputStream());
	}

	public void writeStartHint(OutputStream out, boolean newline) throws IOException
	{
		if (out == null)
			out = getOutputStream();
		if (newline)
			out.write('\n');
		out.write(62);
		out.flush();
	}

	public void writeLine(String text, OutputStream out)
	{
		if (out == null)
			out = getOutputStream();
		try
		{
			out.write(text.getBytes());
			out.write((byte) '\n');
			out.flush();
		}
		catch (IOException e)
		{
		}
	}

	public void writeData(Object data, OutputStream out)
	{
		CLIUtil.writeData(data, out);
	}

	protected void exitLoop()
	{
		m_endflag = true;
		m_llref = null;
	}
}
