package tlc200bot.model;

import java.util.HashMap;
import java.util.Map;

public enum UserState
{

	None(0),
	WaitMarketPostTitle(1),
	WaitMarketPostText(2),
	WaitMarketPostPhoto(3),
	WaitMarketPostPhone(4),

	// твое имя
	WaitInviteName(5),
	// откуда ты (город)
	WaitInviteCity(6),
	// год выпуска авто
	WaitInviteYearAuto(7),
	// тип двигателя
	WaitInviteEngineType(8);

	public final int id;

	private static final Map<Integer, UserState> lookup = new HashMap<>();

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
