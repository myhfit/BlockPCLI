package bp.cli;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import bp.BPCLICore;
import bp.BPCore;
import bp.cli.console.BPConsoleHandler;
import bp.cli.console.BPConsoleHandlerCommandMap;
import bp.event.BPEvent;
import bp.event.BPEventCoreUI;
import bp.task.BPTask;
import bp.task.BPTaskManager;
import bp.util.CommandLineArgs;

public class BPCLILoopTasks implements BPCLILoop
{
	protected List<String> m_taskids = new Vector<String>();
	protected volatile boolean m_ended = false;
	protected Object m_lo = new Object();
	protected BPConsoleHandler m_consolehandler = null;

	protected Function<String, Void> m_listtasksfunc;
	protected Function<String, Void> m_exitfunc;
	protected Function<String, Void> m_starttasksfunc;
	protected Function<String, Void> m_stoptasksfunc;

	public BPCLILoopTasks()
	{
		m_listtasksfunc = this::listTasks;
		m_exitfunc = this::exit;
		m_starttasksfunc = this::startTasks;
		m_stoptasksfunc = this::stopTasks;
	}

	protected Void listTasks(String cmd)
	{
		BPTaskManager taskman = BPCore.getWorkspaceContext().getTaskManager();
		List<BPTask<?>> tasks = taskman.listTasks();
		BPConsoleHandler consolehandler = m_consolehandler;
		for (BPTask<?> task : tasks)
		{
			consolehandler.writeLine(task.getName() + ":" + task.getStatus(), null);
		}
		return null;
	}

	protected Void exit(String cmd)
	{
		doStopAllTasks();
		return null;
	}

	protected Void startTasks(String cmd)
	{
		int vi = cmd.indexOf(" ");
		if (vi > 0)
		{
			BPTaskManager taskman = BPCore.getWorkspaceContext().getTaskManager();
			String namestr = cmd.substring(vi + 1);
			List<String> names = new CopyOnWriteArrayList<String>(namestr.split(","));
			List<BPTask<?>> tasks = taskman.listTasks();
			for (BPTask<?> task : tasks)
			{
				String name = task.getName();
				if (names.contains(name))
					task.start();
			}
		}
		return null;
	}

	protected Void stopTasks(String cmd)
	{
		int vi = cmd.indexOf(" ");
		if (vi > 0)
		{
			BPTaskManager taskman = BPCore.getWorkspaceContext().getTaskManager();
			String namestr = cmd.substring(vi + 1);
			List<String> names = new CopyOnWriteArrayList<String>(namestr.split(","));
			List<BPTask<?>> tasks = taskman.listTasks();
			for (BPTask<?> task : tasks)
			{
				String name = task.getName();
				if (names.contains(name))
					task.stop();
			}
		}
		return null;
	}

	public int loop()
	{
		if (!m_ended)
		{
			BPCLICore.initMainLoop();
			BPConsoleHandlerCommandMap handler = new BPConsoleHandlerCommandMap();
			handler.setIgnoreCase(true);
			m_consolehandler = handler;
			// BPConsoleHandlerCoreCommand h = new
			// BPConsoleHandlerCoreCommand();

			handler.setup(BPCLICore.lineLoop());
			handler.bindCommand("list", m_listtasksfunc);
			handler.bindCommand("exit", m_exitfunc);
			handler.bindCommand("start", m_starttasksfunc);
			handler.bindCommand("stop", m_stoptasksfunc);
			handler.bindCommand("help", handler.getShowCommandFunction());
			BPCLICore.lineLoop().startLoop();
			BPCLICore.lineLoop().waitLoop();
		}
		return 0;
	}

	public void setup(CommandLineArgs cliargs)
	{
		List<String> taskids = m_taskids;
		taskids.clear();
		m_ended = false;

		BPTaskManager taskman = BPCore.getWorkspaceContext().getTaskManager();
		String taskstr = cliargs.params.get("tasks");
		if (taskstr != null)
		{
			List<String> tasknames = Arrays.asList(taskstr.split(","));
			if (tasknames.size() > 0)
			{
				BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_CHANGETASKEND, this::onTaskEnd);
				List<BPTask<?>> tasks = taskman.listTasks();
				for (BPTask<?> task : tasks)
				{
					if (tasknames.contains(task.getName()))
					{
						taskids.add(task.start());
					}
				}
			}
		}
		if (taskids.size() == 0)
			m_ended = true;
	}

	protected void doStopAllTasks()
	{
		BPTaskManager taskman = BPCore.getWorkspaceContext().getTaskManager();
		List<BPTask<?>> tasks = taskman.listTasks();

		for (BPTask<?> task : tasks)
		{
			if (task.isRunning())
				task.stop();
		}
	}

	public void stop()
	{
		doStopAllTasks();
	}

	private void onTaskEnd(BPEvent e)
	{
		BPEventCoreUI eui = (BPEventCoreUI) e;
		BPTask<?> task = (BPTask<?>) eui.datas[0];
		m_taskids.remove(task.getID());
		if (m_taskids.size() == 0)
		{
			System.exit(0);
		}
	}

	protected void doEnd()
	{
		m_ended = true;
		synchronized (m_lo)
		{
			m_lo.notify();
		}
	}
}
