package tlc200bot.model;

import org.jpark.ColumnExtended;
import tlc200bot.Database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "marketPosts")
public class MarketPost
{
	@Id
	@Column(name = "id", columnDefinition = "INT(11) NOT NULL AUTO_INCREMENT")
	@ColumnExtended(updateInsertId = true)
	private int _id;

	@Column(name = "userId", columnDefinition = "BIGINT NOT NULL")
	private long _userId;

	@Column(name = "title", columnDefinition = "VARCHAR(160) NULL")
	private String _title;

	@Column(name = "text", columnDefinition = "TEXT NULL")
	private String _text;

	public int getId()
	{
		return _id;
	}

	public String getTitle()
	{
		return _title != null ? _title : "";
	}

	public void setTitle(String title)
	{
		_title = title;
	}

	public String getText()
	{
		return _text != null ? _text : "";
	}

	public void setText(String text)
	{
		_text = text;
	}

	public long getUserId()
	{
		return _userId;
	}

	public void setUserId(long userId)
	{
		_userId = userId;
	}

	public void persist()
	{
		Database.em().persist(this);
	}
}
