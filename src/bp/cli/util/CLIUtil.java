package bp.cli.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import bp.data.BPXData;
import bp.data.BPXYData;
import bp.util.ObjUtil;
import bp.util.Std;

public class CLIUtil
{
	public final static void writeData(Object data, OutputStream out)
	{
		if (data == null)
			return;
		try
		{
			if (data instanceof BPXYData)
			{
				StringBuilder sb = new StringBuilder();
				{
					String[] collbls = ((BPXYData) data).getColumnLabels();
					for (String c : collbls)
					{
						sb.append(c);
						sb.append("\t");
					}
					out.write(sb.toString().getBytes());
					out.write('\n');
					out.flush();
					sb.setLength(0);
				}
				List<BPXData> xdatas = ((BPXYData) data).getDatas();
				int l = 0;
				int s = xdatas.size();
				for (int i = 0; i < s; i++)
				{
					BPXData xd = xdatas.get(i);
					sb.setLength(0);
					Object[] vs = xd.getValues();
					for (Object v : vs)
					{
						sb.append(ObjUtil.toString(v));
						sb.append("\t");
					}
					out.write(sb.toString().getBytes());
					if (i != s - 1)
						out.write('\n');
					sb.setLength(0);
					l++;
					if (l > 10)
					{
						l = 0;
						out.flush();
					}
				}
				out.flush();
			}
			else
			{
				out.write(ObjUtil.toString(data).getBytes());
			}
		}
		catch (IOException e)
		{
			Std.err(e);
		}
	}
}
