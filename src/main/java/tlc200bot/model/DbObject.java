package tlc200bot.model;

import tlc200bot.Database;

public class DbObject
{
	public void persist()
	{
		Database.em().persist(this);
	}
}
