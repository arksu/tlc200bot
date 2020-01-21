package tlc200bot.model;

import java.util.HashMap;
import java.util.Map;

public enum UserState
{

	None(0),
	WaitMarketPostTitle(1),
	WaitMarketPostText(2),
	WaitMarketPostPhoto(3),
	WaitMarketPostPhone(4);

	public final int id;

	private static final Map<Integer, UserState> lookup = new HashMap<Integer, UserState>();

	static
	{
		for (UserState s : UserState.values())
		{
			lookup.put(s.id, s);
		}
	}

	UserState(int id)
	{
		this.id = id;
	}

	public static UserState get(int id)
	{
		return lookup.get(id);
	}
}
