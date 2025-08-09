package bp.ext;

import java.io.IOException;

import bp.BPCLICore;
import bp.cli.console.BPConsoleHandlerDynamic;
import bp.util.IOLineLoop;
import bp.util.Std;

public class BPExtensionLoaderCLIMain implements BPExtensionLoaderCLI
{
	public String getName()
	{
		return "UI-CLI";
	}

	public String[] getParentExts()
	{
		return null;
	}

	public String[] getDependencies()
	{
		return null;
	}

	public void setup(IOLineLoop mainloop)
	{
		Std.setupUI(null, null, BPExtensionLoaderCLIMain::confirm_u, BPExtensionLoaderCLIMain::prompt_u, BPExtensionLoaderCLIMain::select_u);
	}

	private static boolean confirm_u(String str)
	{
		writeOut(str + "\n(Y or N)?:");
		String r = readLine();
		return r.toUpperCase().equals("Y");
	}

	private static String prompt_u(String str)
	{
		writeOut(str + ":");
		return readLine();
	}

	private static String readLine()
	{
		String[] r = new String[1];
		BPConsoleHandlerDynamic dynamic = new BPConsoleHandlerDynamic();
		dynamic.setDealEmpty(true);
		dynamic.setCommandHandler(cmd ->
		{
			r[0] = cmd;
			return null;
		});
		BPCLICore.lineLoop().addHandlerAndWait(dynamic);
		return r[0];
	}

	private static void writeOut(String str)
	{
		try
		{
			BPCLICore.lineLoop().getOutputStream().write(str.getBytes());
		}
		catch (IOException e)
		{
			Std.err(e);
		}
	}

	private static String select_u(String[] strs)
	{
		for (int i = 0; i < strs.length; i++)
			writeOut(((i + 1) + ":") + strs[i] + "\n");
		String cmd = readLine();
		String rc = null;
		try
		{
			int c = Integer.parseInt(cmd);
			rc = strs[c - 1];
		}
		catch (Exception e)
		{
		}
		return rc;
	}
}
