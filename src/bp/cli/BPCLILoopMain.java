package bp.cli;

import bp.BPCLICore;
import bp.cli.console.BPConsoleHandlerCoreCommand;

public class BPCLILoopMain implements BPCLILoop
{
	public int loop()
	{
		BPCLICore.initMainLoop();
		BPConsoleHandlerCoreCommand h = new BPConsoleHandlerCoreCommand();
		h.setup(BPCLICore.lineLoop());
		BPCLICore.lineLoop().startLoop();
		BPCLICore.lineLoop().waitLoop();
		return 0;
	}
	
	public void stop()
	{
		BPCLICore.lineLoop().stopLoop();
	}
}
