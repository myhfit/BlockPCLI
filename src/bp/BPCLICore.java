package bp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import bp.util.IOLineLoop;

public class BPCLICore
{
	private static IOLineLoop S_LOOP;

	public final static void initMainLoop()
	{
		S_LOOP = new IOLineLoop();
		InputStream in = System.in;
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		S_LOOP.setup(br, in, System.out);
	}

	public final static IOLineLoop lineLoop()
	{
		return S_LOOP;
	}
}
