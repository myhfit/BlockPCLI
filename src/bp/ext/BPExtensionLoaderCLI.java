package bp.ext;

import bp.util.IOLineLoop;

public interface BPExtensionLoaderCLI extends BPExtensionLoaderUI
{
	public final static String UITYPE_CLI = "CLI";

	default String getUIType()
	{
		return UITYPE_CLI;
	}
	
	default void setup(IOLineLoop mainloop)
	{

	}
}
