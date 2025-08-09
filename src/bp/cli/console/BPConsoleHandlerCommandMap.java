package bp.cli.console;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import bp.data.BPCommandResult;
import bp.util.LogicUtil.WeakRefGoFunction;

public class BPConsoleHandlerCommandMap extends BPConsoleHandlerBase
{
	protected Map<String, WeakRefGoFunction<String, ?>> m_cmds;

	protected Function<String, Void> m_showcmd;
	protected volatile boolean m_ignorecase;

	public BPConsoleHandlerCommandMap()
	{
		m_cmds = new ConcurrentHashMap<String, WeakRefGoFunction<String, ?>>();
		m_showcmd = this::showCommands;
		m_ignorecase = false;
	}

	public void setIgnoreCase(boolean flag)
	{
		m_ignorecase = flag;
	}

	public Function<String, Void> getShowCommandFunction()
	{
		return m_showcmd;
	}

	protected BPCommandResult callCommand(String _cmd)
	{
		BPCommandResult rc = new BPCommandResult();
		Map<String, WeakRefGoFunction<String, ?>> cmds = m_cmds;
		boolean ignorecase = m_ignorecase;
		String cmd = _cmd.trim();
		if (cmd.length() > 0)
		{
			String k;
			int vi = cmd.indexOf(" ");
			if (vi > -1)
			{
				k = cmd.substring(0, vi);
			}
			else
			{
				k = cmd;
			}
			if (ignorecase)
				k = k.toLowerCase();
			WeakRefGoFunction<String, ?> rcmd = cmds.get(k);
			if (rcmd != null)
			{
				Object r = rcmd.apply(cmd);
				if (r != null)
					rc.data = r;
				rc.success = true;
			}
		}
		return rc;
	}

	public void bindCommand(String cmd, Function<String, ?> cb)
	{
		WeakRefGoFunction<String, ?> ref = new WeakRefGoFunction<>(cb);
		String tcmd = m_ignorecase ? cmd.toLowerCase() : cmd;
		m_cmds.put(tcmd, ref);
	}

	public Void showCommands(String cmd)
	{
		Map<String, WeakRefGoFunction<String, ?>> cmds = new HashMap<String, WeakRefGoFunction<String, ?>>(m_cmds);
		OutputStream out = getOutputStream();
		for (String key : cmds.keySet())
		{
			try
			{
				out.write(key.getBytes());
				out.write((byte) '\n');
			}
			catch (IOException e)
			{
			}
		}
		return null;
	}

	public void unbindCommand(String cmd)
	{
		m_cmds.remove(m_ignorecase ? cmd.toLowerCase() : cmd);
	}
}
