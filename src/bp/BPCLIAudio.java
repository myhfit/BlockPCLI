package bp;

import bp.cli.BPCLILoopAudio;
import bp.util.ClassUtil;
import bp.util.ClassUtil.BPExtClassLoader;

public class BPCLIAudio
{
	public final static void main(String[] args)
	{
		String extjarstr = "bpaudio.jar";
		if (extjarstr != null && extjarstr.length() > 0)
		{
			BPExtClassLoader cloader = ClassUtil.getExtensionClassLoader();
			String[] jars = extjarstr.split(",");
			for (String jar : jars)
			{
				cloader.addExtURL("exts/" + jar);
			}
		}
		BPCLILauncher.MAINLOOP = new BPCLILoopAudio();
		BPCLILauncher.main(args);
	}
}
