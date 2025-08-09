package bp;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.BPCore.BPPlatform;
import bp.cli.BPCLILoop;
import bp.cli.BPCLILoopMain;
import bp.config.FormatAssocs;
import bp.env.BPEnvs;
import bp.ext.BPExtensionLoader;
import bp.ext.BPExtensionLoaderCLI;
import bp.ext.BPExtensionManager;
import bp.tool.BPToolManager;
import bp.util.ClassUtil;
import bp.util.CommandLineArgs;
import bp.util.FileUtil;
import bp.util.Std;
import bp.util.TextUtil;
import bp.util.ClassUtil.BPExtClassLoader;

public class BPCLILauncher
{
	public static volatile BPCLILoop MAINLOOP;

	public final static void main(String[] args)
	{
		String stdmode = System.getProperty("bp.util.Std");
		if ("debug".equals(stdmode))
		{
			Std.setStdMode(Std.STDMODE_DEBUG);
		}
		else if ("info".equals(stdmode))
		{
			Std.setStdMode(Std.STDMODE_INFO);
		}
		List<String> vmargs = ManagementFactory.getRuntimeMXBean().getInputArguments();
		for (String vmarg : vmargs)
		{
			if (vmarg.startsWith("-agentlib:jdwp"))
			{
				Std.setStdMode(Std.STDMODE_DEBUG);
				break;
			}
		}

		CommandLineArgs cliargs = new CommandLineArgs(args);

		File f = new File(".bpenvcfgs.cli");
		Map<String, String> envs = null;
		if (!f.exists() || !f.isFile())
			f = new File(".bpenvcfgs");
		if (f.exists() && f.isFile())
		{
			byte[] bs = FileUtil.readFile(f.getAbsolutePath());
			if (bs != null)
			{
				String str = TextUtil.toString(bs, "utf-8");
				envs = TextUtil.getPlainMap(str);
			}
		}
		if (envs == null)
			envs = new HashMap<String, String>();
		String extjarstr = envs.get("extensionjars");
		if (extjarstr != null && extjarstr.length() > 0)
		{
			BPExtClassLoader cloader = ClassUtil.getExtensionClassLoader();
			String[] jars = extjarstr.split(",");
			for (String jar : jars)
			{
				if (!jar.contains("gui"))
					cloader.addExtURL("exts/" + jar);
			}
		}

		BPCore.setPlatform(BPPlatform.CLI);
		BPCore.setCommandLineArgs(cliargs);
		BPCore.registerConfig(new BPToolManager());
		BPCore.registerConfig(new FormatAssocs());
		BPCore.registerConfig(new BPEnvs());
		BPCore.start(cliargs.contextpath);

		BPCore.S_ELIST.add(BPCLILauncher::safeExit);

		BPExtensionLoader[] loaders = BPExtensionManager.getExtensionLoaders();
		for (BPExtensionLoader loader : loaders)
		{
			if (loader.isUI() && BPExtensionLoaderCLI.UITYPE_CLI.equals(loader.getUIType()))
			{
				((BPExtensionLoaderCLI) loader).setup(BPCLICore.lineLoop());
			}
		}
		if (MAINLOOP == null)
		{
			String loop = cliargs.params.get("loop");
			if (loop == null)
			{
				MAINLOOP = new BPCLILoopMain();
			}
			else
			{
				MAINLOOP = ClassUtil.createObject(loop);
			}
		}
		if (MAINLOOP != null)
		{
			MAINLOOP.setup(cliargs);

			Runtime.getRuntime().addShutdownHook(new Thread()
			{
				public void run()
				{
					MAINLOOP.stop();
					BPCore.stop();
				}
			});
			int r = MAINLOOP.loop();
			if (r != 0)
				System.exit(r);
		}
	}

	public final static void safeExit()
	{
		MAINLOOP.stop();
	}
}
