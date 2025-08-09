package bp.cli;

import bp.util.CommandLineArgs;

public interface BPCLILoop
{
	int loop();
	
	default void stop()
	{
		
	}

	default void setup(CommandLineArgs cliargs)
	{

	}
}
