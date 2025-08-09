package bp.cli.console;

import java.io.OutputStream;

import bp.util.IOLineLoop;
import bp.util.IOLineLoop.IOLineHandler;

public interface BPConsoleHandler extends IOLineHandler
{
	void setup(IOLineLoop loop);

	void writeLine(String text, OutputStream out);
}
