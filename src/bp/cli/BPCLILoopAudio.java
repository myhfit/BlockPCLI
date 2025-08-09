package bp.cli;

import java.io.IOException;

import bp.res.BPResourceFileLocal;
import bp.service.BPService;
import bp.service.BPServiceManager;
import bp.util.Std;

public class BPCLILoopAudio implements BPCLILoop
{
	public int loop()
	{
		try
		{
			BPService service = BPServiceManager.get("Audio Serivce");
			try
			{
				service.call("addFile", new BPResourceFileLocal("D:\\mp3\\test.mp3"));
				service.call("setGain", 80);
				service.call("play");
			}
			catch (Exception e)
			{
				Std.err(e);
			}
			System.in.read();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
}
