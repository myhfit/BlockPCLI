package bp.cli.console;

import bp.BPCore;
import bp.data.BPCommand;
import bp.data.BPCommandResult;

public class BPConsoleHandlerCoreCommand extends BPConsoleHandlerBase
{
	protected BPCommandResult callCommand(String cmd)
	{
		BPCommandResult r = BPCore.callCommand(BPCommand.fromText(cmd));
		return r;
	}
}
